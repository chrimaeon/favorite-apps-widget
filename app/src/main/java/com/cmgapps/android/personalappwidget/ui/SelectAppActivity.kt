/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.cmgapps.android.personalappwidget.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cmgapps.android.personalappwidget.R
import com.cmgapps.android.personalappwidget.widget.FavoriteAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.title_select_app_title),
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        FavoriteAppWidgetReceiver.sendAppsUpdatedBroadcast(this@SelectAppActivity)
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = stringResource(R.string.refresh_widget),
                                    )
                                }
                            },
                        )
                    },
                ) {
                    SelectAppScreen(
                        modifier = Modifier.padding(it),
                    )
                }
            }
        }
    }
}
