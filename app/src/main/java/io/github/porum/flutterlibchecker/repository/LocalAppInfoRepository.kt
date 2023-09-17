package io.github.porum.flutterlibchecker.repository

import io.github.porum.flutterlibchecker.db.model.AppInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalAppInfoRepository @Inject constructor(

) : AppInfoRepository {

  override val appInfoList: Flow<List<AppInfo>>
    get() = TODO("Not yet implemented")

  override fun getPackageList(applicationId: String): Flow<Collection<String>> {
    TODO("Not yet implemented")
  }
}