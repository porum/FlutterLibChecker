package io.github.porum.flutterlibchecker.data

import io.github.porum.flutterlibchecker.db.model.AppInfo

data class ProgressInfo(
  val index: Int,
  val total: Int,
  val appList: List<AppInfo>
)