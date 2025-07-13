plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hitcapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hitcapp"
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
}
dependencies {
    // ... các dependencies hiện có của bạn ...


    implementation ("com.squareup.okhttp3:okhttp:4.12.0")// Sử dụng phiên bản mới nhất
    // Gson (để chuyển đổi đối tượng Java thành JSON và ngược lại)
    implementation ("com.google.code.gson:gson:2.10.1") // Sử dụng phiên bản mới nhất
}
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.android.volley:volley:1.2.1")


}