import java.net.Inet4Address
import java.net.NetworkInterface

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kurantsov.integritycheck"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kurantsov.integritycheck"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "BUILD_MACHINE_IP", "\"${getBuildMachineIpAddress()}\"")
        buildConfigField("String", "API_KEY", "\"SECRET_API_KEY\"")
        resValue("string", "api_key", "\"SECRET_API_KEY\"")

        val xorValue = 0xAA
        val API_KEY = "SECRET_API_KEY"
        val apiKeyBytes = API_KEY.toByteArray().map {
            it.toInt() xor xorValue
        }
        val apiKeyDefinitionString =
            apiKeyBytes.joinToString(prefix = "[${apiKeyBytes.size}]{", postfix = "}")
        externalNativeBuild {
            cmake {
                cppFlags(
                    "-DAPI_KEY_LENGTH=${apiKeyBytes.size}",
                    "-DAPI_KEY_BYTES_DEFINITION=\"$apiKeyDefinitionString\"",
                    "-DXOR_VALUE=$xorValue",
                )
            }
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/integrity_check.keystore")
            storePassword = "IntegrityCheck"
            keyAlias = "android"
            keyPassword = "IntegrityCheck"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles.addAll(
                files(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
            signingConfig = signingConfigs["release"]
        }
        debug {
            signingConfig = signingConfigs["release"]
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    kotlin {
        jvmToolchain(17)
    }
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.kotlin.bom))
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.appcheck.playintegrity)

    debugImplementation(libs.firebase.appcheck.debug)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}

kapt {
    correctErrorTypes = true
}

fun getBuildMachineIpAddress(): String {
    val addresses = NetworkInterface.getNetworkInterfaces()
        .asSequence()
        .filter { it.isUp && !it.isLoopback && !it.isVirtual }
        .flatMap { networkInterface ->
            networkInterface.inetAddresses
                .asSequence()
                .filter { !it.isLoopbackAddress && it is Inet4Address }
                .map { it.hostAddress }
        }.toList()
    logger.log(LogLevel.INFO, "Addresses: ${addresses.joinToString { it }}")
    return addresses.first()
}