import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.app.myrickshawparent"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.myrickshawparent"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    applicationVariants.all {
        val variant = this
        variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val formattedDate = SimpleDateFormat("MMM_dd_yyyy_HH_mm").format(Date())
                val newName = rootProject.name
                val outputFileName =
                    "${newName}_${formattedDate}_${variant.flavorName}_${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    flavorDimensions += "MyRickshawSP"
    productFlavors {
        create("development") {
            dimension = "MyRickshawSP"
            buildConfigField("String", "BASE_URL", "\"http://192.168.5.208/\"")
            buildConfigField("String", "BASE_API_URL", "\"http://192.168.5.208:8000/api/\"")
            buildConfigField("String", "BASE_SOCKET_URL", "\"http://192.168.5.208:3000\"")
            buildConfigField("String", "MAP_KEY", "\"AIzaSyC-SFqp8tjooejbFiIa-hwq2Gl-og7ZeSQ\"")
        }
        create("production") {
            dimension = "MyRickshawSP"
            buildConfigField("String", "BASE_URL", "\"http://18.118.183.99/\"")
            buildConfigField("String", "BASE_API_URL", "\"http://18.118.183.99/api/\"")
            buildConfigField("String", "BASE_SOCKET_URL", "\"http://18.118.183.99:8080\"")
            buildConfigField("String", "MAP_KEY", "\"AIzaSyC-SFqp8tjooejbFiIa-hwq2Gl-og7ZeSQ\"")
        }
    }
}
composeCompiler {
    enableStrongSkippingMode = true
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.window)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.transport.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.runtime)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.core)

    //Map
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.play.services.base)


    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Data store
    implementation(libs.androidx.datastore.preferences)

    //retrofit
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //hilt
    implementation(libs.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)

    //Preview Compose
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    //Glide
    implementation(libs.glide)

    //Glide compose
    implementation(libs.compose)

    //lottie
    implementation(libs.lottie)

    //Socket
    implementation(libs.socket.io.client)
    implementation(libs.okhttp)
}