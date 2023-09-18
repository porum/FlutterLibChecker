package io.github.porum.flutterlibchecker.datasource

import android.app.Application
import android.content.pm.PackageInfo
import io.github.porum.flutterlibchecker.db.dao.VersionDao
import io.github.porum.flutterlibchecker.db.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.ByteString
import okio.buffer
import okio.source
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import javax.inject.Inject

class DataSource @Inject constructor(
  private val application: Application,
  private val versionDao: VersionDao,
) {

  val appInfoList: Flow<List<AppInfo>> = flow {
    val flutterAppList = mutableListOf<AppInfo>()
    val packageInfoList = application.packageManager.getInstalledPackages(0)
    for (packageInfo in packageInfoList) {
      File(packageInfo.applicationInfo.publicSourceDir).parentFile
        ?.listFiles { file -> file.name.endsWith(".apk") }
        ?.map { ZipFile(it) }
        ?.any { zipFile ->
          zipFile.use { it ->
            it.entries()
              .asSequence()
              .firstOrNull { it.isDirectory.not() && it.name.endsWith("libapp.so") }
              ?.also { flutterAppList.add(getAppInfo(packageInfo, zipFile.getInputStream(it))) }
          } != null
        }
    }

    flutterAppList.sortBy { it.appName }
    emit(flutterAppList)
  }.flowOn(Dispatchers.IO)

  @OptIn(ExperimentalStdlibApi::class)
  private fun getAppInfo(packageInfo: PackageInfo, inputStream: InputStream): AppInfo {
    val size: String
    val kind: String
    val versionHash: String
    inputStream.source().buffer().use { source ->
      val index = source.indexOf(SNAPSHOT_DATA_MAGIC_VALUE)
      source.skip(index + 4)
      size = source.readByteArray(8).toHexString()
      kind = source.readByteArray(8).toHexString()
      versionHash = String(source.readByteArray(32))
    }

    val versionEntity = versionDao.find(versionHash)

    return AppInfo(
      applicationId = packageInfo.packageName,
      appName = application.packageManager.getApplicationLabel(packageInfo.applicationInfo)
        .toString(),
      appIcon = packageInfo.applicationInfo.icon,
      versionName = packageInfo.versionName,
      versionCode = packageInfo.versionCode,
      flutterVersion = versionEntity?.version ?: "unknown",
      dartVersion = versionEntity?.dartSdkVersion ?: "unknown",
      channel = versionEntity?.channel ?: "unknown",
      packages = emptyList(),
    )
  }

  fun getPackageList(applicationId: String) = flow {
    val packageList = sortedSetOf<String>()
    File(application.packageManager.getPackageInfo(
      applicationId,
      0
    ).applicationInfo.publicSourceDir).parentFile
      ?.listFiles { file -> file.name.endsWith(".apk") }
      ?.map { ZipFile(it) }
      ?.any {
        it.use { zipFile ->
          it.entries()
            .asSequence()
            .firstOrNull { it.isDirectory.not() && it.name.endsWith("libapp.so") }
            ?.also { packageList.addAll(getPackageList(zipFile.getInputStream(it))) }
        } != null
      }

    emit(packageList)
  }.flowOn(Dispatchers.IO)

  private fun getPackageList(inputStream: InputStream): Set<String> {
    val results = sortedSetOf<String>()
    inputStream.source().buffer().use { source ->
      while (true) {
        try {
          val content = source.readByteString(1024 * 1024).utf8()
          val list = Regex("""package:\w+/""")
            .findAll(content)
            .map { it.value.substring(8, it.value.length - 1) }
          results.addAll(list)
        } catch (th: Throwable) {
          break
        }
      }
    }

    return results
  }

  companion object {
    private val SNAPSHOT_DATA_MAGIC_VALUE =
      ByteString.of(0xF5.toByte(), 0xF5.toByte(), 0xDC.toByte(), 0xDC.toByte())
  }

}