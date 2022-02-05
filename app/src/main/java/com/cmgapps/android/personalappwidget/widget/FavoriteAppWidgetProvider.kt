/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.cmgapps.android.personalappwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import com.cmgapps.LogTag
import com.cmgapps.android.personalappwidget.BuildConfig
import com.cmgapps.android.personalappwidget.R

@LogTag("FavoriteWidgetProvider")
class FavoriteAppWidgetProvider : AppWidgetProvider() {
    override fun onEnabled(context: Context) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onEnabled")
        }
    }

    override fun onDisabled(context: Context) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onDisabled")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_OPEN_APP -> intent.extras?.let { extras ->
                val packageName = extras.getString(EXTRA_PACKAGE_NAME)
                val activityName = extras.getString(EXTRA_ACTIVITY_NAME)
                if (packageName != null && activityName != null) {
                    onOpenApp(context, packageName, activityName)
                }
            }
            else -> super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onUpdate")
        }

        appWidgetIds.forEach { appWidgetId ->
            val remoteViews = buildLayout(context, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun buildLayout(context: Context, appWidgetId: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)

        remoteViews.setRemoteAdapter(
            R.id.list,
            Intent(context, FavoriteAppViewsService::class.java).also {
                it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                it.data = Uri.parse(it.toUri(Intent.URI_INTENT_SCHEME))
            },
        )

        remoteViews.setEmptyView(R.id.list, R.id.empty)

        val flag =
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }

        remoteViews.setPendingIntentTemplate(
            R.id.list,
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, FavoriteAppWidgetProvider::class.java).also {
                    it.action = ACTION_OPEN_APP
                },
                flag,
            )
        )
        return remoteViews
    }

    private fun onOpenApp(context: Context, packageName: String, activityName: String) {
        val startActivityIntent = Intent().also { intent ->
            intent.component = ComponentName(packageName, activityName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(startActivityIntent)
        } catch (exc: ActivityNotFoundException) {
            Log.e(LOG_TAG, "Activity not found for intent: $startActivityIntent")
        }
    }

    companion object {

        const val ACTION_OPEN_APP = BuildConfig.APPLICATION_ID + ".action.ACTION_OPEN_APP"
        const val EXTRA_PACKAGE_NAME = BuildConfig.APPLICATION_ID + ".extra.EXTRA_PACKAGE_NAME"
        const val EXTRA_ACTIVITY_NAME = BuildConfig.APPLICATION_ID + ".extra.EXTRA_ACTIVITY_NAME"

        @JvmStatic
        fun refreshWidgets(context: Context) {
            Log.i("FavoriteAppWidgetProvider", "refreshing favorite app widgets")
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
