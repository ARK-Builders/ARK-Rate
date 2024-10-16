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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
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

    implementation("dev.arkbuilders.components:about:0.1.1")
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("com.google.dagger:dagger:2.50")
    implementation("androidx.glance:glance-appwidget:1.1.0")
    ksp("com.google.dagger:dagger-compiler:2.50")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("io.github.oleksandrbalan:tagcloud:1.1.0")

    implementation("org.orbit-mvi:orbit-compose:4.6.1")
    implementation("org.orbit-mvi:orbit-viewmodel:6.1.0")

    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.1")

    implementation("com.github.androidmads:QRGenerator:1.0.1")

    implementation("io.github.raamcosta.compose-destinations:animations-core:1.9.62")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.62")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
}

tasks.getByPath(":app:preBuild").dependsOn("ktlintCheck")

tasks.getByPath(":app:preBuild").dependsOn("ktlintFormat")

tasks.getByPath("ktlintCheck").shouldRunAfter("ktlintFormat")
