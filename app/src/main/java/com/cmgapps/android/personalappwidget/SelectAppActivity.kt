/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Px
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SelectAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SelectAppScreen()
            }
        }
    }
}

val DefaultIconSize = 40.dp

@Composable
private fun SelectAppScreen() {
    val context = LocalContext.current
    val pm: PackageManager = context.packageManager

    val intent = Intent(Intent.ACTION_MAIN, null).also { intent ->
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val allAppsInfo = pm.queryIntentActivities(intent, 0).apply {
        sortBy { it.loadLabel(pm).toString() }
    }

    val imageSizePx = with(LocalDensity.current) { DefaultIconSize.roundToPx() }

    val selectedApps = remember { mutableStateListOf<ResolveInfo>() }

    LazyColumn {
        items(allAppsInfo) { info ->
            val bitmap by loadIcon(pm, info, imageSizePx)

            Item(
                appName = info.loadLabel(pm).toString(),
                icon = bitmap,
                iconSize = DefaultIconSize,
                selected = selectedApps.contains(info),
                onSelectionChange = {
                    if (it) {
                        selectedApps += info
                        context.sendBroadcast(
                            Intent(context, AppPackageUpdateReceiver::class.java).also { intent ->
                                intent.action = AppPackageUpdateReceiver.ACTION_APP_ADDED
                                intent.data =
                                    Uri.parse("package:${info.activityInfo.applicationInfo.packageName}")
                            }
                        )
                    } else {
                        selectedApps -= info
                    }
                }

            )
        }
    }
}

@Composable
private fun Item(
    appName: String,
    icon: ImageBitmap?,
    iconSize: Dp = DefaultIconSize,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = selected,
                onValueChange = onSelectionChange
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        if (icon == null) {
            Box(
                modifier = Modifier.size(iconSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
            )
        } else {
            Image(
                modifier = Modifier.size(iconSize),
                bitmap = icon,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = appName,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(16.dp))
        Checkbox(
            checked = selected,
            onCheckedChange = onSelectionChange
        )
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

