/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.cmgapps.android.personalappwidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.cmgapps.LogTag
import com.cmgapps.android.personalappwidget.BuildConfig
import com.cmgapps.android.personalappwidget.R

@LogTag("AppLauncherWidgetProvider")
class FavoriteAppWidgetProvider : AppWidgetProvider() {
    override fun onEnabled(context: Context) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onEnabled")
        }

        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, AppPackageUpdateReceiver::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }

    override fun onDisabled(context: Context) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onDisabled")
        }
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, AppPackageUpdateReceiver::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onUpdate")
        }

        appWidgetIds.forEach { appWidgetId ->
            val remoteViews = buildLayout(context, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun buildLayout(context: Context, appWidgetId: Int): RemoteViews {
        val intent = Intent(context, FavoriteAppViewsService::class.java).also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
        }
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
        remoteViews.setRemoteAdapter(R.id.list, intent)
        remoteViews.setEmptyView(R.id.list, R.id.empty)
        return remoteViews
    }

    companion object {
        @JvmStatic
        fun refreshWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(
                    ComponentName(context, FavoriteAppWidgetProvider::class.java),
                ),
                R.id.list,
            )
        }
    }
}
