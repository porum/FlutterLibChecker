package io.github.porum.flutterlibchecker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.porum.flutterlibchecker.repository.AppInfoRepository
import io.github.porum.flutterlibchecker.repository.RealtimeAppInfoRepository

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

  @Binds
  fun bindsAppInfoRepository(
    realtimeAppInfoRepository: RealtimeAppInfoRepository
  ): AppInfoRepository
}