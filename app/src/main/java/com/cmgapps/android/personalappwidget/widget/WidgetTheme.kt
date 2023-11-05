/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.material3.ColorProviders

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
    val resources = LocalContext.current.resources
    val theme = LocalContext.current.theme
    val lightBackgroundColor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Color(
                resources.getColor(
                    android.R.color.system_neutral2_100,
                    theme,
                ),
            )
        } else {
            Color(0xfffffbfe)
        }
    val darkBackgroundColor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Color(
                resources.getColor(
                    android.R.color.system_neutral2_700,
                    theme,
                ),
            )
        } else {
            Color(0xff1c1b1f)
        }

    val lightTextColor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Color(
                resources.getColor(
                    android.R.color.system_accent1_900,
                    theme,
                ),
            )
        } else {
            Color(0xff1c1b1f)
        }
    val darkTextColor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Color(
                resources.getColor(
                    android.R.color.system_accent1_100,
                    theme,
                ),
            )
        } else {
            Color(0xffe6e1e5)
        }

    GlanceTheme(
        colors =
            ColorProviders(
                light =
                    lightColorScheme(
                        background = lightBackgroundColor,
                        onBackground = lightTextColor,
                    ),
                dark =
                    darkColorScheme(
                        background = darkBackgroundColor,
                        onBackground = darkTextColor,
                    ),
            ),
        content = content,
    )
}
