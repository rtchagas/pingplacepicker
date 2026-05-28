plugins {
    alias(libs.plugins.android.library)
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
    implementation(libs.places)

    // Koin for Android
    implementation(libs.koin.android)

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
