plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "dev.arkbuilders.rate"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.arkbuilders.rate"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.2.0"
        setProperty("archivesBaseName", "ark-rate")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("testRelease") {
            storeFile = project.rootProject.file("keystore.jks")
            storePassword = "sw0rdf1sh"
            keyAlias = "ark-builders-test"
            keyPassword = "rybamech"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            addManifestPlaceholders(
                mapOf(
                    "appIcon" to "@mipmap/ic_launcher_debug",
                    "appLabel" to "@string/app_name_debug",
                ),
            )
        }

        release {
            postprocessing {
                isRemoveUnusedCode = true
                isObfuscate = false
                isOptimizeCode = true
                proguardFiles("proguard-rules.pro")
            }
            signingConfig = signingConfigs.getByName("testRelease")

            addManifestPlaceholders(
                mapOf(
                    "appIcon" to "@mipmap/ic_launcher_debug",
                    "appLabel" to "@string/app_name_debug",
                ),
            )
        }
    }

    flavorDimensions += "publishTarget"
    productFlavors {
        create("github") {
            dimension = "publishTarget"
            buildConfigField("boolean", "GOOGLE_PLAY_BUILD", "false")
        }

        create("googleplay") {
            dimension = "publishTarget"
            buildConfigField("boolean", "GOOGLE_PLAY_BUILD", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":fiaticons"))
    implementation(project(":cryptoicons"))

    implementation(libs.ark.about)
    implementation(libs.androidx.ui)

    implementation(libs.navigation.compose)
    implementation(libs.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.constraintlayout.compose)

    implementation(libs.dagger)
    implementation(libs.androidx.glance.appwidget)
    ksp(libs.dagger.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.timber)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.tagcloud)

    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)

    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    implementation(libs.qrgenerator)

    implementation(libs.compose.destinations.animations)
    ksp(libs.compose.destinations.compiler)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
}

tasks.getByPath(":app:preBuild").dependsOn("ktlintCheck")

tasks.getByPath(":app:preBuild").dependsOn("ktlintFormat")

tasks.getByPath("ktlintCheck").shouldRunAfter("ktlintFormat")
