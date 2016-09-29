package com.louisgeek.checkappupdatelib.tool;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by louisgeek on 2016/9/29.
 */
public class AppTool {
    /*
    获取自己应用程序的名称
     */
    public static String getAppName(Context context)
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;

        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var4) {
            applicationInfo = null;
        }

        String applicationName = (String)packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /*
获取自己应用程序的图标
 */
    public static Bitmap getIconBitmap(Context context)
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d=packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }
}
