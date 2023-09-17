package io.github.porum.flutterlibchecker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.porum.flutterlibchecker.db.converter.StringListConverter
import io.github.porum.flutterlibchecker.db.dao.AppInfoDao
import io.github.porum.flutterlibchecker.db.dao.VersionDao
import io.github.porum.flutterlibchecker.db.model.AppInfo
import io.github.porum.flutterlibchecker.db.model.VersionEntity

@Database(
  entities = [
    AppInfo::class,
    VersionEntity::class,
  ],
  version = 1,
)
@TypeConverters(
  StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun appInfoDao(): AppInfoDao
  abstract fun versionDao(): VersionDao
}