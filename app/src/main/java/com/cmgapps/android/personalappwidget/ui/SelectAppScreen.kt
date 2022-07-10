/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.cmgapps.android.personalappwidget.ui

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.annotation.Px
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.cmgapps.android.personalappwidget.infra.db.SelectedApp
import com.cmgapps.android.personalappwidget.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val DefaultIconSize = 40.dp

@Composable
fun SelectAppScreen(
    modifier: Modifier = Modifier,
    viewModel: SelectedAppViewModel = hiltViewModel(),
) {
    val imageSizePx = with(LocalDensity.current) { DefaultIconSize.roundToPx() }

    val uiState = viewModel.uiState

    val context = LocalContext.current
    val packageManager = context.packageManager
    val selectedApps = uiState.selectedApps
    val allApps = uiState.allApps

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            allApps,
            key = { it.packageName },
        ) { app ->
            val bitmap by loadIcon(packageManager, app.info, imageSizePx)

            Item(
                app = app,
                icon = bitmap,
                iconSize = DefaultIconSize,
                selected = selectedApps.contains(SelectedApp(app.packageName, app.activityName)),
                onSelectionChange = {
                    viewModel.appSelectionChanged(context, app, it)
                },
            )
        }
    }
}

@Composable
private fun Item(
    app: App,
    icon: ImageBitmap?,
    iconSize: Dp = DefaultIconSize,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = selected,
                    onValueChange = onSelectionChange,
                )
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(8.dp))
            if (icon == null) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background),
                )
            } else {
                Image(
                    modifier = Modifier.size(iconSize),
                    bitmap = icon,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = app.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = selected,
                onCheckedChange = onSelectionChange,
            )
        }
    }
}

@Composable
private fun loadIcon(
    packageManager: PackageManager,
    info: ResolveInfo,
    @Px imageSize: Int,
): State<ImageBitmap?> {
    return produceState<ImageBitmap?>(null, info, imageSize) {
        value = withContext(Dispatchers.IO) {
            info.loadIcon(packageManager)
                .toBitmap(width = imageSize, height = imageSize).asImageBitmap()
        }
    }
}
