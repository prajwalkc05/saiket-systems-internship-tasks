plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.simplequizapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.simplequizapp"
        minSdk = 24   // ✅ FIX
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}