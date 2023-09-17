package io.github.porum.flutterlibchecker.repository

import io.github.porum.flutterlibchecker.datasource.DataSource
import io.github.porum.flutterlibchecker.db.model.AppInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RealtimeAppInfoRepository @Inject constructor(
  private val datasource: DataSource,
) : AppInfoRepository {

  override val appInfoList: Flow<List<AppInfo>> = datasource.appInfoList

  override fun getPackageList(applicationId: String) = datasource.getPackageList(applicationId)

}