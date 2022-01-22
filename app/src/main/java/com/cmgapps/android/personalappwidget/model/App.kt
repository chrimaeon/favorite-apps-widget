/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.model

import android.content.pm.ResolveInfo

class App(
    val info: ResolveInfo,
    val displayName: String,
) {
    val packageName: String = info.activityInfo.applicationInfo.packageName

    val activityName: String = info.activityInfo.name
}
