import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    //The Google services Gradle plugin for Firebase integration
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.app_ee3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app_ee3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val supabaseKey = localProperties.getProperty("SUPABASE_KEY") ?: ""

        buildConfigField("String", "SUPABASE_KEY", "\"${supabaseKey}\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.squareup.okhttp3:okhttp:4.10.0") // For HTTP Requests
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.google.code.gson:gson:2.9.0")  // For JSON Parsing

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    /*Import the Firebase BoM (Bill of Materials)
    * It specifies which version of the Firebase libraries you want to use,
    * and ensures that all Firebase dependencies are compatible with each other.
    */
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    /*Import Firebase Analytics library
    * Following the user usage in the app -> als je wilt meten hoeveel gebruikers op je pushmeldingen klikken.
    */
    implementation("com.google.firebase:firebase-analytics")
    /**/
    // Voeg Firebase Messaging toe zonder versie, omdat de BoM het beheert
    implementation ("com.google.firebase:firebase-messaging")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.transport.api)

    //implementation to let recyclerView work
    //implementation("androidx.recyclerview:recyclerview:1.3.2@aar")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
