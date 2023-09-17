package io.github.porum.flutterlibchecker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.porum.flutterlibchecker.db.model.AppInfo

@Dao
interface AppInfoDao {
  @Query("select * from AppInfo")
  fun getAll(): List<AppInfo>

  @Query("select * from AppInfo where appName like :appName")
  fun findByName(appName: String): AppInfo

  @Insert
  fun insertAll(vararg appInfos: AppInfo)
}