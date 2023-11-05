/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.cmgapps.gradle.ktlint")
}

android {
    namespace = "com.cmgapps.android.personalappwidget"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.cmgapps.android.personalappwidget"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    val debugSigningConfig by signingConfigs.named("debug") {
        storeFile = projectDir.resolve("keystore").resolve("debug.keystore")
    }

    buildTypes {
        debug {
            signingConfig = debugSigningConfig
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    // jvmToolchain(17)
}

ksp {
    arg("room.schemaLocation", projectDir.resolve("schema").absolutePath)
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.glance)

    implementation(libs.logtag.lib)
    ksp(libs.logtag.processor)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.bundles.hilt)
    kapt(libs.hilt.compiler)
}
