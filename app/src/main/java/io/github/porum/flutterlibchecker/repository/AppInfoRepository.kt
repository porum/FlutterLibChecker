package io.github.porum.flutterlibchecker.repository

import io.github.porum.flutterlibchecker.db.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppInfoRepository {
  val appInfoList: Flow<List<AppInfo>>

  fun getPackageList(applicationId: String) : Flow<Collection<String>>
}