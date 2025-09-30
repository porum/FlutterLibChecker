package io.github.porum.flutterlibchecker.datasource

import android.app.Application
import io.github.porum.flutterlibchecker.data.ProgressInfo
import io.github.porum.flutterlibchecker.db.dao.VersionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
  private val application: Application,
  private val versionDao: VersionDao,
) : DataSource {
  override fun getFlutterApps(): Flow<ProgressInfo> {
    TODO("Not yet implemented")
  }
}