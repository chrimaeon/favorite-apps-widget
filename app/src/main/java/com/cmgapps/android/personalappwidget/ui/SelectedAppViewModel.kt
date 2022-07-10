/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.ui

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.android.personalappwidget.infra.db.SelectedApp
import com.cmgapps.android.personalappwidget.model.App
import com.cmgapps.android.personalappwidget.repository.AppsRepository
import com.cmgapps.android.personalappwidget.widget.FavoriteAppWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class UiState(
    val allApps: List<App> = emptyList(),
    val selectedApps: List<SelectedApp> = emptyList(),
)

@HiltViewModel
class SelectedAppViewModel @Inject constructor(
    private val appsRepository: AppsRepository,
) : ViewModel() {

    var uiState: UiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            appsRepository.selectedApps.collect {
                uiState = uiState.copy(selectedApps = it)
            }
        }
        viewModelScope.launch {
            val allApps = appsRepository.getAllApps()
            uiState = uiState.copy(allApps = allApps)
        }
    }

    fun appSelectionChanged(context: Context, app: App, selected: Boolean) {
        viewModelScope.launch {
            with(SelectedApp(app.packageName, app.activityName)) {
                if (selected) {
                    appsRepository.addSelectedApp(this)
                } else {
                    appsRepository.removeSelectedApp(this)
                }
            }
            FavoriteAppWidgetReceiver.sendAppsUpdatedBroadcast(context)
        }
    }
}
