/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.toBitmap
import com.cmgapps.android.personalappwidget.R
import com.cmgapps.android.personalappwidget.infra.db.AppDao
import com.cmgapps.android.personalappwidget.widget.FavoriteAppWidgetProvider.Companion.EXTRA_ACTIVITY_NAME
import com.cmgapps.android.personalappwidget.widget.FavoriteAppWidgetProvider.Companion.EXTRA_PACKAGE_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteAppViewsService : RemoteViewsService() {
    @Inject
    lateinit var appDao: AppDao
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = ListRemoteViewFactory(this, appDao)
}

private class ListRemoteViewFactory(private val context: Context, private val appDao: AppDao) :
    RemoteViewsService.RemoteViewsFactory {
    private val packageManager = context.packageManager
    private var appInfos: MutableList<ResolveInfo> = mutableListOf()
    private val iconSize = context.resources.getDimensionPixelSize(R.dimen.icon_size)

    override fun onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
    }

    override fun onDestroy() {
        // nothing to do
    }

    override fun getCount(): Int = appInfos.size

    override fun onDataSetChanged() {
        appInfos.clear()
        appDao.getAllOneShot().forEach { selectedApp ->

            packageManager.resolveActivity(
                Intent(Intent.ACTION_MAIN).also {
                    it.addCategory(Intent.CATEGORY_LAUNCHER)
                    it.component = ComponentName(selectedApp.packageName, selectedApp.activityName)
                }, PackageManager.MATCH_DEFAULT_ONLY
            )?.let {
                appInfos.add(it)
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val info = appInfos[position]
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item)
        remoteViews.setTextViewText(R.id.text, info.loadLabel(packageManager))
        remoteViews.setImageViewBitmap(
            R.id.icon, info.loadIcon(packageManager).toBitmap(width = iconSize, height = iconSize)
        )

        val intent = Intent().also {
            it.putExtra(EXTRA_PACKAGE_NAME, info.activityInfo.applicationInfo.packageName)
            it.putExtra(EXTRA_ACTIVITY_NAME, info.activityInfo.name)
        }
        remoteViews.setOnClickFillInIntent(R.id.widget_item, intent)
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int): Long =
        appInfos[position].activityInfo.applicationInfo.packageName.hashCode().toLong()

    override fun hasStableIds(): Boolean = true
}
