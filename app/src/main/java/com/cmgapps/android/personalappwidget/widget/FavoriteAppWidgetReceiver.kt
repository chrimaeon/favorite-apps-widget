/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.cmgapps.android.personalappwidget.BuildConfig
import com.cmgapps.android.personalappwidget.R
import com.cmgapps.android.personalappwidget.infra.db.AppDao
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent

class FavoriteAppWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        val appContext = LocalContext.current.applicationContext
        val appDao = EntryPoints.get(
            appContext,
            FavoriteAppWidgetInterface::class.java
        ).getAppDao()

        val packageManager = appContext.packageManager

        val iconSize = appContext.resources.getDimensionPixelSize(R.dimen.icon_size)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(day = Color.LightGray, night = Color.DarkGray)
                .appWidgetBackground()
                .appWidgetBackgroundRadius()
                .padding(16.dp),
        ) {
            val favApps = appDao.getAllOneShot().map { selectedApp ->
                val intent = Intent(Intent.ACTION_MAIN).also {
                    it.addCategory(Intent.CATEGORY_LAUNCHER)
                    it.component =
                        ComponentName(selectedApp.packageName, selectedApp.activityName)
                }
                val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.resolveActivity(
                        intent,
                        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.resolveActivity(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                }

                FavoriteApp(
                    packageName = selectedApp.packageName,
                    selectedApp.activityName,
                    resolveInfo
                )
            }.filter { it.resolveInfo != null }
            val lastIndex = favApps.lastIndex
            favApps.forEachIndexed { index, favoriteApp ->
                val resolveInfo = favoriteApp.resolveInfo!!
                Row(
                    modifier = GlanceModifier.clickable(
                        actionStartActivity(
                            ComponentName(
                                favoriteApp.packageName,
                                favoriteApp.activityName
                            )
                        )
                    ),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(
                            resolveInfo.loadIcon(packageManager).toBitmap(iconSize, iconSize)
                        ),
                        contentDescription = null,
                    )
                    Spacer(GlanceModifier.width(8.dp))
                    Text(
                        resolveInfo.loadLabel(packageManager).toString(),
                        maxLines = 2
                    )
                }
                if (index != lastIndex) {
                    Spacer(GlanceModifier.height(8.dp))
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FavoriteAppWidgetInterface {
        fun getAppDao(): AppDao
    }
}

private data class FavoriteApp(
    val packageName: String,
    val activityName: String,
    val resolveInfo: ResolveInfo?,
)

@AndroidEntryPoint
class FavoriteAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = FavoriteAppWidget()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_APPS_UPDATED -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName =
                    ComponentName(context.packageName, checkNotNull(javaClass.canonicalName))
                onUpdate(
                    context,
                    appWidgetManager,
                    appWidgetManager.getAppWidgetIds(componentName)
                )
            }
            else -> super.onReceive(context, intent)
        }
    }

    companion object {
        const val ACTION_APPS_UPDATED = BuildConfig.APPLICATION_ID + ".action.ACTION_APPS_UPDATED"

        @JvmStatic
        fun sendAppsUpdatedBroadcast(context: Context) {
            context.sendBroadcast(
                Intent(context, FavoriteAppWidgetReceiver::class.java).also {
                    it.action = ACTION_APPS_UPDATED
                }
            )
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundRadius(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this.cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        this.cornerRadius(16.dp)
    }
}
