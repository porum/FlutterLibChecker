package io.github.porum.flutterlibchecker.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.porum.flutterlibchecker.db.dao.AppInfoDao
import io.github.porum.flutterlibchecker.db.dao.VersionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  private const val DB_NAME = "app-database"

  @Provides
  @Singleton
  fun provideAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    DB_NAME
  )
    .createFromAsset("${DB_NAME}.db")
    .build()

  @Provides
  fun provideAppInfoDao(
    db: AppDatabase,
  ): AppInfoDao = db.appInfoDao()

  @Provides
  fun provideVersionDao(
    db: AppDatabase,
  ): VersionDao = db.versionDao()
}