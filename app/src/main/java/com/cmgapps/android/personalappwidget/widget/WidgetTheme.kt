/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.glance.LocalContext
import androidx.glance.appwidget.unit.ColorProvider

@Composable
fun WidgetTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val dayBackgroundColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(
            context.resources.getColor(
                android.R.color.system_accent1_100,
                LocalContext.current.theme,
            ),
        )
    } else {
        Color(0xfffffbfe)
    }
    val nightBackgroundColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(
            context.resources.getColor(
                android.R.color.system_accent1_700,
                LocalContext.current.theme,
            ),
        )
    } else {
        Color(0xff1c1b1f)
    }

    val dayTextColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(
            context.resources.getColor(
                android.R.color.system_accent1_900,
                LocalContext.current.theme,
            ),
        )
    } else {
        Color(0xff1c1b1f)
    }
    val nightTextColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Color(
            context.resources.getColor(
                android.R.color.system_accent1_100,
                LocalContext.current.theme,
            ),
        )
    } else {
        Color(0xffe6e1e5)
    }

    CompositionLocalProvider(
        LocalWidgetBackground provides WidgetBackground(
            day = dayBackgroundColor,
            night = nightBackgroundColor,
        ),
        LocalTextColorProvider provides ColorProvider(day = dayTextColor, night = nightTextColor),
    ) {
        content()
    }
}

data class WidgetBackground(
    val day: Color = Color(0xfffffbfe),
    val night: Color = Color(0xff1c1b1f),
)

val LocalWidgetBackground = staticCompositionLocalOf { WidgetBackground() }

val LocalTextColorProvider =
    staticCompositionLocalOf {
        ColorProvider(
            day = Color(0xff1c1b1f),
            night = Color(0xffe6e1e5),
        )
    }
