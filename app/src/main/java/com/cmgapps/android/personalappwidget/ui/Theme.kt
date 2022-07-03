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

private val amber = Color(0xFFFFC107)
private val amberVariantDark = Color(0xFFC79100)
private val amberVariantLight = Color(0xFFFFF350)
private val deepPurple = Color(0xFF9575CD)
private val deepPurpleVariantDark = Color(0xFF65499C)
private val deepPurpleVariantLight = Color(0xFFC7A4FF)

val DarkColors = darkColors(
    primary = amberVariantLight,
    primaryVariant = amber,
    secondary = deepPurpleVariantLight,
    secondaryVariant = deepPurpleVariantLight,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
)

val LightColors = lightColors(
    primary = amber,
    primaryVariant = amberVariantDark,
    secondary = deepPurple,
    secondaryVariant = deepPurpleVariantDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
