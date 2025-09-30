package io.github.porum.flutterlibchecker.datasource

import android.app.Application
import android.content.pm.PackageInfo
import io.github.porum.flutterlibchecker.data.FlutterInfo
import io.github.porum.flutterlibchecker.data.ProgressInfo
import io.github.porum.flutterlibchecker.db.dao.VersionDao
import io.github.porum.flutterlibchecker.db.model.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import okio.ByteString.Companion.decodeHex
import okio.buffer
import okio.source
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
  private val application: Application,
  private val versionDao: VersionDao,
) : DataSource {

  override fun getFlutterApps(): Flow<ProgressInfo> {
    return channelFlow {
      val flutterApps = mutableListOf<AppInfo>()
      val packageInfoList = application.packageManager.getInstalledPackages(0)
        .filter { it.applicationInfo != null }
      val total = packageInfoList.size
      packageInfoList.forEachIndexed { index, packageInfo ->
        File(packageInfo.applicationInfo!!.publicSourceDir).parentFile
          ?.listFiles { file -> file.isFile && file.name.endsWith(".apk") }
          ?.map { apk -> ZipFile(apk) }
          ?.mapNotNull { zipFile ->
            zipFile.use {
              it.entries().asSequence()
                .firstOrNull { it.isDirectory.not() && it.name.endsWith("libapp.so") }
                ?.let {
                  assembleAppInfo(
                    packageInfo = packageInfo,
                    flutterInfo = parseDynamicLib(zipFile.getInputStream(it))
                  )
                }
            }
          }
          ?.mapTo(flutterApps) { it }

        trySend(ProgressInfo(index = index, total = total, appList = flutterApps))
      }
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  private fun parseDynamicLib(inputStream: InputStream): FlutterInfo {
    val size: String
    val kind: String
    val snapshotHash: String
    val flutterPackages = sortedSetOf<String>()

    inputStream.source().buffer().use { source ->
      val index = source.indexOf(SNAPSHOT_DATA_MAGIC_VALUE)
      source.skip(index + 4)
      size = source.readByteArray(8).toHexString()
      kind = source.readByteArray(8).toHexString()
      snapshotHash = String(source.readByteArray(32))

      while (true) {
        try {
          val content = source.readByteString(1024 * 1024).utf8()
          val list = Regex("""package:\w+/""")
            .findAll(content)
            .map { it.value.substring(8, it.value.length - 1) }
          flutterPackages.addAll(list)
        } catch (th: Throwable) {
          break
        }
      }
    }

    return FlutterInfo(
      snapshotHash = snapshotHash,
      packages = flutterPackages
    )
  }

  private fun assembleAppInfo(packageInfo: PackageInfo, flutterInfo: FlutterInfo): AppInfo {
    val versionEntity = versionDao.find(flutterInfo.snapshotHash)
    return AppInfo(
      applicationId = packageInfo.packageName,
      appName = application.packageManager.getApplicationLabel(packageInfo.applicationInfo!!)
        .toString(),
      appIcon = packageInfo.applicationInfo!!.icon,
      versionName = packageInfo.versionName ?: "unknown",
      versionCode = packageInfo.versionCode,
      flutterVersion = versionEntity?.version ?: "unknown",
      dartVersion = versionEntity?.dartSdkVersion ?: "unknown",
      channel = versionEntity?.channel ?: "unknown",
      packages = flutterInfo.packages,
    )
  }

  companion object {
    private val SNAPSHOT_DATA_MAGIC_VALUE = "F5F5DCDC".decodeHex()
  }
}