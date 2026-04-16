plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hethongbangiay"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hethongbangiay"
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

    signingConfigs {
        create("sharedDebug") {
            storeFile = file("$rootDir/shared-debug.keystore")
            storePassword = "android"
            keyAlias = "debug"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("sharedDebug")
        }
        release {
            signingConfig = signingConfigs.getByName("sharedDebug")
        }
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    // 1. UI & Core Libraries (Sử dụng Version Catalog 'libs' cho đồng bộ)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 2. Firebase (Sử dụng BoM để quản lý phiên bản tự động)
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // implementation("com.google.firebase:firebase-storage") // Mở ra nếu cần lưu ảnh

    // 3. Google Sign-In (Dành cho chức năng Login Google)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // 4. Cloudinary (Lưu trữ ảnh Cloud)
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.viewpager2)
    implementation(fileTree(mapOf(
        "dir" to "D:\\ZaloPayLibrary",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))

//    implementation(files("libs/zpdk-release-v3.1.aar"))




    // 5. Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //6 . Room database
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.core:core-ktx:1.12.0")

    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}