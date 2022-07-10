/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val amber30 = Color(0xffffd54f)
private val amber40 = Color(0xffffca28)
private val amber90 = Color(0xffff6f00)
private val amber80 = Color(0xffff8f00)
private val deepPurple30 = Color(0xff9575cd)
private val deepPurple40 = Color(0xff7e57c2)
private val deepPurple90 = Color(0xff311b92)
private val deepPurple80 = Color(0xff4527a0)

val DarkColors = darkColorScheme(
    primary = amber80,
    onPrimary = Color.Black,
    primaryContainer = amber30,
    onPrimaryContainer = Color.Black,
    inversePrimary = amber40,
    secondary = deepPurple80,
    onSecondary = Color.White,
    secondaryContainer = deepPurple30,
    onSecondaryContainer = Color.Black,
)

val LightColors = lightColorScheme(
    primary = amber40,
    onPrimary = Color.Black,
    primaryContainer = amber90,
    onPrimaryContainer = Color.Black,
    inversePrimary = amber80,
    secondary = deepPurple40,
    onSecondary = Color.White,
    secondaryContainer = deepPurple90,
    onSecondaryContainer = Color.White,
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val lightColorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(LocalContext.current)
        } else {
            LightColors
        }

    val darkColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        content = content,
    )
}
