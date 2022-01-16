/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("com.android.application")
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

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {

}