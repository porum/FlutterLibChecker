package io.github.porum.flutterlibchecker.repository

import io.github.porum.flutterlibchecker.data.ProgressInfo
import io.github.porum.flutterlibchecker.datasource.LocalDataSource
import io.github.porum.flutterlibchecker.datasource.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoRepository @Inject constructor(
  private val remoteDatasource: RemoteDataSource,
  private val localDataSource: LocalDataSource,
) {

  fun getFlutterApps(): Flow<ProgressInfo> {
    return remoteDatasource.getFlutterApps().flowOn(Dispatchers.IO)
  }

}