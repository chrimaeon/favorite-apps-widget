/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.repository

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.cmgapps.android.personalappwidget.infra.db.AppDao
import com.cmgapps.android.personalappwidget.infra.db.SelectedApp
import com.cmgapps.android.personalappwidget.model.App
import com.cmgapps.android.personalappwidget.widget.FavoriteApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppsRepository @Inject constructor(
    private val appDao: AppDao,
    private val packageManager: PackageManager,
) {

    suspend fun getAllApps() = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null).also { intent ->
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, 0)
        }.map {
            App(
                info = it,
                displayName = it.loadLabel(packageManager).toString(),
            )
        }.sortedBy { it.displayName }
    }

    fun getFavoriteApps() = appDao.getFavoriteApps().mapNotNull { selectedApp ->
        val intent = Intent(Intent.ACTION_MAIN).also {
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.component =
                ComponentName(selectedApp.packageName, selectedApp.activityName)
        }
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY,
            )
        }

        if (resolveInfo == null) {
            null
        } else {
            FavoriteApp(
                packageName = selectedApp.packageName,
                selectedApp.activityName,
                resolveInfo,
            )
        }
    }

    suspend fun addSelectedApp(selectedApp: SelectedApp) {
        appDao.insert(selectedApp)
    }

    suspend fun removeSelectedApp(selectedApp: SelectedApp) {
        appDao.delete(selectedApp)
    }

    val selectedApps = appDao.getAll()
}
