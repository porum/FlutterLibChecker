package io.github.porum.flutterlibchecker.datasource

import android.app.Application
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
import javax.inject.Inject

class DataSource @Inject constructor(
  private val application: Application,
  private val versionDao: VersionDao,
) {

  @OptIn(ExperimentalStdlibApi::class)
  val appInfoList: Flow<List<AppInfo>> = flow {
    emit(application.packageManager.getInstalledPackages(0).filter {
      File(it.applicationInfo.nativeLibraryDir).listFiles { _, name ->
        name == "libapp.so" || name == "libflutter.so"
      }?.size == 2
    }.map {
      val bufferedSource = File(it.applicationInfo.nativeLibraryDir, "libapp.so").source().buffer()
      val index = bufferedSource.indexOf(SNAPSHOT_DATA_MAGIC_VALUE)
      bufferedSource.skip(index + 4)
      val length = bufferedSource.readByteArray(8).toHexString()
      val kind = bufferedSource.readByteArray(8).toHexString()
      val versionHash = String(bufferedSource.readByteArray(32))
      bufferedSource.close()

      val versionEntity = versionDao.find(versionHash)

      AppInfo(
        applicationId = it.packageName,
        appName = application.packageManager.getApplicationLabel(it.applicationInfo).toString(),
        appIcon = it.applicationInfo.icon,
        versionName = it.versionName,
        versionCode = it.versionCode,
        flutterVersion = versionEntity?.version ?: "unknown",
        dartVersion = versionEntity?.dartSdkVersion ?: "unknown",
        channel = versionEntity?.channel ?: "unknown",
        packages = emptyList(),
      )
    }.sortedBy {
      it.appName
    })
  }.flowOn(Dispatchers.IO)

  fun getPackageList(applicationId: String) = flow {
    val applicationInfo =
      application.packageManager.getPackageInfo(applicationId, 0).applicationInfo
    val bufferedSource = File(applicationInfo.nativeLibraryDir, "libapp.so").source().buffer()
    //        val content = bufferedSource.readUtf8()
    //        val start = SystemClock.elapsedRealtime()
    //        val results = Regex("""package:\w+/""").findAll(content).map {
    //          it.value.substring(8, it.value.length - 1)
    //        }.toSet()
    //        Log.i("PackageListActivity", "cost: ${SystemClock.elapsedRealtime() - start}")
    //        bufferedSource.close()

    val results = sortedSetOf<String>()
    while (true) {
      try {
        val content = bufferedSource.readByteString(1024 * 1024).utf8()
        val list = Regex("""package:\w+/""").findAll(content).map {
          it.value.substring(8, it.value.length - 1)
        }
        results.addAll(list)
      } catch (th: Throwable) {
        break
      }
    }
    bufferedSource.close()
    emit(results)
  }.flowOn(Dispatchers.IO)

  companion object {
    private val SNAPSHOT_DATA_MAGIC_VALUE =
      ByteString.of(0xF5.toByte(), 0xF5.toByte(), 0xDC.toByte(), 0xDC.toByte())
  }

}