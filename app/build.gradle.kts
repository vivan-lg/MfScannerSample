plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.ledgergreen.mfsample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ledgergreen.mfsample"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
    implementation(platform(libs.compose.bom))
    implementation(libs.kotlinx.dateTime)
    implementation(libs.kotlinx.collection.immutable)

    implementation(libs.mrzJava)

    implementation(libs.bundles.app.ui)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.hilt.navigationCompose)

    implementation(libs.kotlinx.serialization)

    implementation(libs.timber)

    implementation(libs.dagger.android)
    kapt(libs.dagger.compiler)
    implementation(libs.androidx.hilt.navigationCompose)
    kapt(libs.androidx.hilt.compiler)
    testImplementation(libs.dagger.android)
    kaptTest(libs.dagger.compiler)
    androidTestImplementation(libs.dagger.android)
    kaptAndroidTest(libs.dagger.compiler)

    implementation(libs.coroutines.android)

    debugImplementation(libs.leakcanary)
}
