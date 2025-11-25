plugins {
    id("com.android.application")  // <-- Вот он, только один раз
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")                 // <-- Этот мы добавили для Room
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.weekly"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.weekly"
        minSdk = 24
        targetSdk = 36
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
        isCoreLibraryDesugaringEnabled = true
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ViewModel/LiveData в Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

    // Для комбинированного клика (longClick)
    implementation("androidx.compose.foundation:foundation:1.6.7")



    // Room (База данных)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Для корутин
    kapt("androidx.room:room-compiler:$room_version") // Обработчик аннотаций

    // build.gradle.kts (Module: app) - в блоке dependencies { ... }
    implementation("androidx.navigation:navigation-compose:2.7.7") // Убедитесь, что версия актуальна

    // ⭐️ ДОБАВЬТЕ ЭТУ ЗАВИСИМОСТЬ
    implementation("javax.inject:javax.inject:1")

    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.0.0") // Или новее
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
}