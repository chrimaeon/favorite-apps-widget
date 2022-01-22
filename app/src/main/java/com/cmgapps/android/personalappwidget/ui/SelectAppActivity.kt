/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Px
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.cmgapps.android.personalappwidget.R
import com.cmgapps.android.personalappwidget.infra.db.AppDao
import com.cmgapps.android.personalappwidget.infra.db.SelectedApp
import com.cmgapps.android.personalappwidget.model.App
import com.cmgapps.android.personalappwidget.widget.FavoriteAppWidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SelectAppActivity : ComponentActivity() {

    @Inject
    lateinit var appDao: AppDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.title_select_app_title)
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        FavoriteAppWidgetProvider.refreshWidgets(this@SelectAppActivity)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.refrrsh_widget)
                                    )
                                }
                            }
                        )
                    }
                ) {
                    SelectAppScreen(appDao)
                }
            }
        }
    }
}

val DefaultIconSize = 40.dp

@Composable
private fun SelectAppScreen(appDao: AppDao) {

    val context = LocalContext.current
    val pm: PackageManager = context.packageManager

    val intent = Intent(Intent.ACTION_MAIN, null).also { intent ->
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val allAppsInfo: List<App> by produceState(emptyList(), pm) {
        value = withContext(Dispatchers.IO) {
            pm.queryIntentActivities(intent, 0)
                .map {
                    App(
                        info = it,
                        displayName = it.loadLabel(pm).toString(),
                    )
                }.sortedBy { it.displayName }
        }
    }

    val imageSizePx = with(LocalDensity.current) { DefaultIconSize.roundToPx() }

    val selectedApps by produceState<List<SelectedApp>>(emptyList(), appDao) {
        appDao.getAll().collect {
            value = it
        }
    }

    val scope = rememberCoroutineScope()

    LazyColumn {
        items(allAppsInfo) { app ->
            val bitmap by loadIcon(pm, app.info, imageSizePx)

            Item(
                app = app,
                icon = bitmap,
                iconSize = DefaultIconSize,
                selected = selectedApps.find {
                    it.packageName == app.packageName && it.activityName == app.activityName
                } != null,
                onSelectionChange = {
                    scope.launch {
                        if (it) {
                            appDao.insert(SelectedApp(app.packageName, app.activityName))
                        } else {
                            appDao.delete(app.packageName)
                        }
                        FavoriteAppWidgetProvider.refreshWidgets(context)
                    }
                }
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
    onSelectionChange: (Boolean) -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .toggleable(
                    value = selected,
                    onValueChange = onSelectionChange
                )
                .background(MaterialTheme.colors.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))
            if (icon == null) {
                Box(
                    modifier = Modifier.size(iconSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.background)
                )
            } else {
                Image(
                    modifier = Modifier.size(iconSize),
                    bitmap = icon,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = app.displayName,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = selected,
                onCheckedChange = onSelectionChange
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
