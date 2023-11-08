/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import android.content.ComponentName
import android.content.Context
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.cmgapps.android.personalappwidget.R
import com.cmgapps.android.personalappwidget.ui.SelectAppActivity
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class FavoriteAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val appContext = context.applicationContext
        val viewModel =
            EntryPoints.get(
                appContext,
                FavoriteAppWidgetEntryPoint::class.java,
            ).getViewModel()

        val packageManager = appContext.packageManager
        val resources = appContext.resources
        val iconSize = resources.getDimensionPixelSize(R.dimen.icon_size)

        val favApps = viewModel.getFavoriteApps()

        provideContent {
            WidgetTheme {
                val textColor = GlanceTheme.colors.onBackground
                Column(
                    modifier =
                        GlanceModifier.background(GlanceTheme.colors.background)
                            .appWidgetBackground()
                            .appWidgetBackgroundRadius()
                            .padding(16.dp),
                ) {
                    Box(
                        modifier = GlanceModifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_add_24),
                            contentDescription = resources.getString(R.string.add_app),
                            colorFilter = ColorFilter.tint(textColor),
                            modifier =
                                GlanceModifier
                                    .clickable(actionStartActivity(SelectAppActivity::class.java)),
                        )
                    }
                    Spacer(GlanceModifier.height(2.dp))
                    val lastIndex = favApps.lastIndex
                    favApps.forEachIndexed { index, favoriteApp ->
                        Row(
                            modifier =
                                GlanceModifier
                                    .fillMaxWidth()
                                    .clickable(
                                        actionStartActivity(
                                            ComponentName(
                                                favoriteApp.packageName,
                                                favoriteApp.activityName,
                                            ),
                                        ),
                                    ),
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                        ) {
                            Image(
                                provider =
                                    ImageProvider(
                                        favoriteApp.resolveInfo.loadIcon(packageManager)
                                            .toBitmap(iconSize, iconSize),
                                    ),
                                contentDescription = null,
                            )
                            Spacer(GlanceModifier.width(8.dp))
                            Text(
                                favoriteApp.resolveInfo.loadLabel(packageManager).toString(),
                                style =
                                    TextStyle(
                                        color = textColor,
                                    ),
                                maxLines = 2,
                            )
                        }
                        if (index != lastIndex) {
                            Spacer(GlanceModifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    fun interface FavoriteAppWidgetEntryPoint {
        fun getViewModel(): FavoriteAppWidgetViewModel
    }
}

data class FavoriteApp(
    val packageName: String,
    val activityName: String,
    val resolveInfo: ResolveInfo,
)

class FavoriteAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FavoriteAppWidget()
}

private fun GlanceModifier.appWidgetBackgroundRadius(): GlanceModifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this then cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        this then cornerRadius(16.dp)
    }
