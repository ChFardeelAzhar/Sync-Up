plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
    kotlin("plugin.serialization") version "2.1.0"

}

android {
    namespace = "com.example.syncup"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.syncup"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // dagger-hilt
    implementation(libs.hilt.android)
    annotationProcessor("com.google.dagger:hilt-compiler:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // navigation
    implementation(libs.androidx.navigation.compose)

    // google-icons
    implementation("androidx.compose.material:material-icons-extended:1.7.5")

    // coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // supabase

    implementation(platform("io.github.jan-tennert.supabase:bom:1.4.2"))
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.4.2") // Main Supabase library
    implementation("io.github.jan-tennert.supabase:storage-kt:0.9.0")

    implementation("io.ktor:ktor-client-core:2.3.3")        // Core HTTP client
    implementation("io.ktor:ktor-client-cio:2.3.3")         // CIO engine for HTTP calls
    implementation("io.ktor:ktor-client-android:2.3.3")

    implementation("io.ktor:ktor-client-plugins:2.3.3")



}

kapt {
    correctErrorTypes = true
}