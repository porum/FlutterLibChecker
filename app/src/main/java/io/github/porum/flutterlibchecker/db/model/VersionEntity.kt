package io.github.porum.flutterlibchecker.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VersionEntity(
  @ColumnInfo("release_date") @PrimaryKey val releaseDate: String,
  @ColumnInfo("channel") val channel: String,
  @ColumnInfo("version") val version: String,
  @ColumnInfo("dart_sdk_version") val dartSdkVersion: String,
  @ColumnInfo("engine_commit") val engineCommit: String,
  @ColumnInfo("snapshot_hash") val snapshotHash: String,
)