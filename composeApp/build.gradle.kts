import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

// Load local.properties
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

fun getLocalProperty(key: String, defaultValue: String = ""): String {
    return localProperties.getProperty(key, defaultValue)
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            // Ktor CIO engine for Android
            implementation(libs.ktor.client.cio)
            // Google Maps Compose
            implementation(libs.maps.compose)
            // Google Play Services Location
            implementation(libs.play.services.location)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.navigation)
            implementation(libs.kotlinx.serialization.json)
            // KMPAuth - Firebase Authentication
            implementation(libs.kmpauth.firebase)
            implementation(libs.kmpauth.uihelper)
            implementation(libs.kotlinx.coroutines.core)
            // GitLive Firebase Firestore
            implementation(libs.firebase.firestore)
            // Peekaboo Image Picker (camera disabled on iOS due to UIKitView compatibility)
            implementation(libs.peekaboo.image.picker)
            // Ktor Client for HTTP requests
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            // Coil Image Loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        iosMain.dependencies {
            // Ktor Darwin engine for iOS
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.harsh.playspot"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.harsh.playspot"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        
        // Google Maps API Key for AndroidManifest
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = getLocalProperty("GOOGLE_MAPS_API_KEY")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

buildkonfig {
    packageName = "com.harsh.playspot"
    objectName = "BuildConfig"

    defaultConfigs {
        buildConfigField(STRING, "APP_NAME", "PlaySpot")
        buildConfigField(STRING, "VERSION_NAME", "1.0.0")
        buildConfigField(BOOLEAN, "DEBUG", "true")
        // API Keys from local.properties
        buildConfigField(STRING, "GOOGLE_PLACES_API_KEY", getLocalProperty("GOOGLE_PLACES_API_KEY"))
        buildConfigField(STRING, "GOOGLE_MAPS_API_KEY", getLocalProperty("GOOGLE_MAPS_API_KEY"))
        // ImageKit API Keys
        buildConfigField(STRING, "IMAGEKIT_PUBLIC_KEY", getLocalProperty("IMAGEKIT_PUBLIC_KEY"))
        buildConfigField(STRING, "IMAGEKIT_PRIVATE_KEY", getLocalProperty("IMAGEKIT_PRIVATE_KEY"))
        buildConfigField(STRING, "IMAGEKIT_URL_ENDPOINT", getLocalProperty("IMAGEKIT_URL_ENDPOINT"))
    }

    defaultConfigs("release") {
        buildConfigField(BOOLEAN, "DEBUG", "false")
    }
}