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
// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
// Gson converter (chuyển JSON <-> Object)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
// OkHttp (HTTP client chính của Retrofit)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
// Logging Interceptor (dùng để log request/response)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

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
// thư viện lưu token 
    // implementation("androidx.security:security-crypto:1.1.0-alpha06")
//component đẹp chuẩn cho anh em quốc nhá
    implementation("com.google.android.material:material:1.12.0")
//    motion
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha10")
//

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)


}