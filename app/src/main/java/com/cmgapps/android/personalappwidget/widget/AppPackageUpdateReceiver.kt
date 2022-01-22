/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.cmgapps.android.personalappwidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cmgapps.LogTag
import com.cmgapps.android.personalappwidget.BuildConfig
import dagger.hilt.android.AndroidEntryPoint

@LogTag("AppUpdateReceiver")
@AndroidEntryPoint
class AppPackageUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onReceive $intent")
        }

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED ->
                FavoriteAppWidgetProvider.refreshWidgets(context)
        }
    }
}
