/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colorAmber = Color(0xFFFFC107)
private val colorHeliotrope = Color(0xFFE823FC)

val DarkColors = darkColors(
    primary = colorAmber,
    secondary = colorHeliotrope,
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

val LightColors = lightColors(
    primary = colorAmber,
    secondary = colorHeliotrope,
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
