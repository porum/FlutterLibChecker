package io.github.porum.flutterlibchecker.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class AppInfo(
  @PrimaryKey val applicationId: String,
  val appName: String,
  val appIcon: Int,
  val versionName: String,
  val versionCode: Int,
  val flutterVersion: String,
  val dartVersion: String,
  val channel: String,
  val packages: Set<String>
) : Serializable