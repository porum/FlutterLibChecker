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
  compileSdk = 36

  defaultConfig {
    applicationId = "io.github.porum.flutterlibchecker"
    minSdk = 24
    targetSdk = 36
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.17.0")
  implementation("androidx.appcompat:appcompat:1.7.1")
  implementation("androidx.activity:activity-ktx:1.11.0")
  implementation("androidx.fragment:fragment-ktx:1.8.9")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
  implementation("com.google.android.material:material:1.13.0")
  implementation("androidx.browser:browser:1.9.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation("com.squareup.okio:okio:3.5.0")

  ksp("androidx.room:room-compiler:2.8.1")
  implementation("androidx.room:room-ktx:2.8.1")

  ksp("com.google.dagger:hilt-compiler:2.52")
  implementation("com.google.dagger:hilt-android:2.52")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.3.0")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}