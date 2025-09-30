package io.github.porum.flutterlibchecker.datasource

import io.github.porum.flutterlibchecker.data.ProgressInfo
import kotlinx.coroutines.flow.Flow

interface DataSource {
  fun getFlutterApps(): Flow<ProgressInfo>
}