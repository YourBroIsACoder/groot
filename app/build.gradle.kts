plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)

}

android {
    namespace = "com.example.groot"
    compileSdk = 35

    buildFeatures {
        buildConfig = true // This is needed to access the API key

    }

    defaultConfig {
        applicationId = "com.example.groot"
        minSdk = 24
        targetSdk = 35
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
    implementation(libs.coil.compose)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.ui)
    implementation (libs.androidx.datastore.preferences) // latest stable
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.common.android)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.datastore.core.android)

    implementation(libs.androidx.datastore.preferences.core.android)
    testImplementation(libs.junit)
        // ... other dependencies
    // Core TensorFlow Lite runtime
    implementation(libs.tensorflow.lite)
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // For using delay() in the ViewModel
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.google.firebase:firebase-firestore-ktx")

// Optional: support library (for TensorImage, TensorBuffer, etc.)
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.14.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation(libs.google.firebase.auth.ktx)

// Task library (ImageClassifier, ObjectDetector, etc.)
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}