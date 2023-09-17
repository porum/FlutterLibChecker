package io.github.porum.flutterlibchecker.db

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.porum.flutterlibchecker.db.dao.AppInfoDao
import io.github.porum.flutterlibchecker.db.dao.VersionDao

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

  @Provides
  fun provideAppInfoDao(
    db: AppDatabase,
  ): AppInfoDao = db.appInfoDao()

  @Provides
  fun provideVersionDao(
    db: AppDatabase,
  ): VersionDao = db.versionDao()
}