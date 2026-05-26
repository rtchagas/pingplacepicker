plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    `maven-publish`
}

android {
    namespace = "com.rtchagas.pingplacepicker"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlin {
        jvmToolchain(libs.versions.jvmTarget.get().toInt())
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // Kotlin coroutines
    implementation(libs.bundles.coroutines)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.bundles.androidx.lifecycle)

    // Google Play Services
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.utils)
    implementation(libs.places)

    // Koin for Android
    implementation(libs.koin.android)

    // Rx (removed in Phase 3)
    implementation(libs.bundles.rx)

    // Moshi
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Retrofit
    implementation(libs.bundles.retrofit.rx)

    // Coil
    implementation(libs.coil)

    // 3rd party
    implementation(libs.material.colors)

    testImplementation(libs.junit)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.rtchagas"
                artifactId = "pingplacepicker"
            }
        }
    }
}
