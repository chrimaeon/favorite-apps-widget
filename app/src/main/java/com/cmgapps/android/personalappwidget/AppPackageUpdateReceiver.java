/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class AppPackageUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "AppPackageUpdateReceiver";
    public static final String ACTION_APP_ADDED = "com.cmgapps.android.personalappwidget.action.APP_ADDED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent);

        if (intent.getAction().equals(ACTION_APP_ADDED)) {
            // TODO handle app selection
            Log.d(TAG, intent.getData().getSchemeSpecificPart());
            return;
        }

        Uri data = intent.getData();

        for (String pkg : PersonalAppLauncherWidgetProvider.PACKAGES) {
            if (pkg.equals(data.getSchemeSpecificPart())) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);

                PersonalAppLauncherWidgetProvider.updateAppWidget(context, manager,
                        manager.getAppWidgetIds(new ComponentName(context, PersonalAppLauncherWidgetProvider.class)));
                break;
            }
        }
    }

}
