package com.cmgapps.android.personalapplauncher;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

public class PersonalAppLauncherWidgetProvider extends AppWidgetProvider
{
  private static final String TAG = "LanguageSwitcherWidgetProvider";
  private static final String PHONEWS_PACKAGE = "at.cmg.android.phonews";
  private static final String PHONEWSPRO_PACKAGE = "com.cmgapps.android.phonewspro";
  private static final String NUMERALSCONVERTER_PACKAGE = "com.cmgapps.android.numeralsconverter";
  private static final String BIERDECKEL_PACKAGE = "com.cmgapps.android.bierdeckel";

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    final int numWidgets = appWidgetIds.length;

    for (int i = 0; i < numWidgets; i++)
    {
      int appWidgetId = appWidgetIds[i];
      initWidget(context, appWidgetManager, appWidgetId);
    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  public static void initWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
  {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

    try
    {
      initView(context, R.id.phonews, PHONEWS_PACKAGE, PHONEWS_PACKAGE + ".activity.Welcome", views);
      initView(context, R.id.phonewspro, PHONEWSPRO_PACKAGE, PHONEWSPRO_PACKAGE + ".activity.Welcome", views);
      initView(context, R.id.numeralsconverter, NUMERALSCONVERTER_PACKAGE, NUMERALSCONVERTER_PACKAGE
          + ".NumeralsConverterActivity", views);
      initView(context, R.id.bierdeckel, BIERDECKEL_PACKAGE, BIERDECKEL_PACKAGE + ".ui.BierdeckelActivity", views);
    }
    catch (NameNotFoundException exc)
    {
      Log.e(TAG, "Application icon not found for package: " + exc.getMessage());
    }

    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  private static void initView(Context context, int viewId, String pkg, String cls, RemoteViews views)
      throws NameNotFoundException
  {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    intent.setComponent(new ComponentName(pkg, cls));

    views.setOnClickPendingIntent(viewId, PendingIntent.getActivity(context, 0, intent, 0));

    Drawable drawable = context.getPackageManager().getApplicationIcon(pkg);

    if (drawable instanceof BitmapDrawable)
      views.setBitmap(viewId, "setImageBitmap", ((BitmapDrawable) drawable).getBitmap());

  }

}
