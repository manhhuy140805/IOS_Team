plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.manhhuy.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.manhhuy.myapplication"
        minSdk = 30
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
    buildFeatures {
        viewBinding = true
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
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

//component đẹp chuẩn cho anh em quốc nhá
    implementation("com.google.android.material:material:1.12.0")
//    motion
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha10")
//

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
}