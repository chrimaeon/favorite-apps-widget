package com.cmgapps.android.personalapplauncher;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class PersonalAppLauncherWidgetProvider extends AppWidgetProvider
{
  private static final String TAG = "PersonalAppLauncherWidgetProvider";
  private static final String PHONEWS_PACKAGE = "at.cmg.android.phonews";
  private static final String PHONEWSPRO_PACKAGE = "com.cmgapps.android.phonewspro";
  private static final String NUMERALSCONVERTER_PACKAGE = "com.cmgapps.android.numeralsconverter";
  private static final String BIERDECKEL_PACKAGE = "com.cmgapps.android.bierdeckel";
  private static final String SUSHI_COUNTER = "com.cmgapps.android.sushicounter";

  public static final String[] PACKAGES = { PHONEWS_PACKAGE, PHONEWSPRO_PACKAGE, NUMERALSCONVERTER_PACKAGE,
      BIERDECKEL_PACKAGE, SUSHI_COUNTER };

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    if (BuildConfig.DEBUG)
      Log.d(TAG, "onUpdate");

    updateAppWidget(context, appWidgetManager, appWidgetIds);
  }

  @Override
  public void onEnabled(Context context)
  {
    if (BuildConfig.DEBUG)
      Log.d(TAG, "onEnabled");

    context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, AppPackageUpdateReceiver.class),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
  }

  @Override
  public void onDisabled(Context context)
  {
    if (BuildConfig.DEBUG)
      Log.d(TAG, "onDisabled");

    context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, AppPackageUpdateReceiver.class),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

  }

  public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
  {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

    initView(context, R.id.phonews, PHONEWS_PACKAGE, PHONEWS_PACKAGE + ".activity.Welcome", views);
    initView(context, R.id.phonewspro, PHONEWSPRO_PACKAGE, PHONEWSPRO_PACKAGE + ".activity.Welcome", views);
    initView(context, R.id.numeralsconverter, NUMERALSCONVERTER_PACKAGE, NUMERALSCONVERTER_PACKAGE
        + ".NumeralsConverterActivity", views);
    initView(context, R.id.bierdeckel, BIERDECKEL_PACKAGE, BIERDECKEL_PACKAGE + ".ui.BierdeckelActivity", views);
    initView(context, R.id.sushicounter, SUSHI_COUNTER, SUSHI_COUNTER + ".activity.SushiActivity", views);
    
    appWidgetManager.updateAppWidget(appWidgetIds, views);
  }

  private static void initView(Context context, int viewId, String pkg, String cls, RemoteViews views)
  {
    try
    {
      ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg, 0);

      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
      intent.setComponent(new ComponentName(info.packageName, cls));

      views.setOnClickPendingIntent(viewId, PendingIntent.getActivity(context, 0, intent, 0));

      Drawable drawable = context.getPackageManager().getApplicationIcon(info);

      if (drawable instanceof BitmapDrawable)
        views.setBitmap(viewId, "setImageBitmap", ((BitmapDrawable) drawable).getBitmap());

      views.setViewVisibility(viewId, View.VISIBLE);
    }
    catch (NameNotFoundException exc)
    {
      Log.d(TAG, "Application info not found for package: " + exc.getMessage());
      views.setViewVisibility(viewId, View.GONE);
    }

  }
}
