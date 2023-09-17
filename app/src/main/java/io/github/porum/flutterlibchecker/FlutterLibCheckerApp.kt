package io.github.porum.flutterlibchecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlutterLibCheckerApp : Application() {

  override fun onCreate() {
    super.onCreate()
    app = this
  }

  companion object {
    lateinit var app: Application
  }
}