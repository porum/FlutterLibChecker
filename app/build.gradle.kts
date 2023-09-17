plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("com.google.dagger.hilt.android")
}

hilt {
  enableAggregatingTask = true
}

android {
  namespace = "io.github.porum.flutterlibchecker"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.github.porum.flutterlibchecker"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.activity:activity-ktx:1.7.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
  implementation("com.google.android.material:material:1.9.0")
  implementation("androidx.browser:browser:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("com.squareup.okio:okio:3.5.0")

  ksp("androidx.room:room-compiler:2.5.2")
  implementation("androidx.room:room-ktx:2.5.2")

  ksp("com.google.dagger:hilt-compiler:2.47")
  implementation("com.google.dagger:hilt-android:2.47")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}