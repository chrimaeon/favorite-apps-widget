/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.annotation.Px
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.cmgapps.android.personalappwidget.infra.db.SelectedApp
import kotlinx.coroutines.CoroutineDispatcher
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
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
    ) {
        items(
            allApps,
            // apps can have more than one launcher activity;
            // add the activity name for uniqueness
            key = { it.packageName + it.activityName },
        ) { app ->
            val optionalIcon by loadIcon(packageManager, app.info, imageSizePx)

            ListItem(
                headlineContent = {
                    Text(
                        text = app.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingContent = {
                    val icon = optionalIcon
                    if (icon == null) {
                        Box(
                            modifier =
                                Modifier
                                    .size(DefaultIconSize)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background),
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(DefaultIconSize),
                            bitmap = icon,
                            contentDescription = null,
                        )
                    }
                },
                trailingContent = {
                    Checkbox(
                        checked = selectedApps.contains(SelectedApp(app.packageName, app.activityName)),
                        onCheckedChange = {
                            viewModel.appSelectionChanged(context, app, it)
                        },
                    )
                },
                supportingContent = {
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

@Composable
@SuppressLint("ProduceStateDoesNotAssignValue")
private fun loadIcon(
    packageManager: PackageManager,
    info: ResolveInfo,
    @Px imageSize: Int,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): State<ImageBitmap?> =
    produceState<ImageBitmap?>(null, info, imageSize) {
        value =
            withContext(dispatcher) {
                info
                    .loadIcon(packageManager)
                    .toBitmap(width = imageSize, height = imageSize)
                    .asImageBitmap()
            }

        // awaitDispose {}
    }
