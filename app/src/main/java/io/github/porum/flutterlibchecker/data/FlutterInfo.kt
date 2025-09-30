package io.github.porum.flutterlibchecker.data

data class FlutterInfo(
  val snapshotHash: String,
  val packages: Set<String>,
)
