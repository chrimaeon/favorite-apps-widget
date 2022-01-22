/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    idea
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
    id("com.cmgapps.gradle.ktlint")
}

android {
    compileSdk = 31
    buildToolsVersion = "32.0.0"

    defaultConfig {
        applicationId = "com.cmgapps.android.personalappwidget"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    applicationVariants.all {
        val variantName = name
        sourceSets {
            named("main") {
                java.srcDir(buildDir.resolve("generated").resolve("ksp").resolve(variantName).resolve("kotlin"))
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", projectDir.resolve("schema").absolutePath)
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.logtag.lib)
    ksp(libs.logtag.processor)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
