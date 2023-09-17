package io.github.porum.flutterlibchecker.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.porum.flutterlibchecker.db.model.VersionEntity

@Dao
interface VersionDao {
  @Query("select * from VersionEntity where snapshot_hash = :snapshotHash")
  fun find(snapshotHash: String): VersionEntity?
}