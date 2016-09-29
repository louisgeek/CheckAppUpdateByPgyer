package com.louisgeek.checkappupdatelib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.louisgeek.checkappupdatelib.tool.AppTool;
import com.louisgeek.checkappupdatelib.tool.DateTool;

/**
 * Created by louisgeek on 2016/9/28.
 */
public class NotificationMangerCenter {
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static NotificationCompat.Builder mBuilder;
    private static final int NOTIFYID_1 = 1;
    //通知栏显示所用到的布局文件
    private static RemoteViews contentView;
    private static int mPogressTemp;

    public static void initNotification(Context context, Class<?> clazz) {
        //定义一个PendingIntent点击Notification后启动一个Activity
        Intent intent = new Intent(context, clazz);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //创建大图标的Bitmap
      // Bitmap largeBitmap = BitmapFactory.decodeResource(context.getResources(),  R.drawable.ic_file_download_light_green_700_18dp);
        Bitmap largeBitmap = AppTool.getIconBitmap(context);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //NotificationCompat.Builder
        mBuilder = new NotificationCompat.Builder(context);
        //mBuilder.setAutoCancel(false);
        //mBuilder.setContentTitle("有新版本");
       // mBuilder.setContentText("下载中");
        mBuilder.setTicker("新版"+AppTool.getAppName(context)+"正在下载中");//设置收到通知时在顶部显示的文字信息
        mBuilder.setSmallIcon(R.drawable.ic_file_download_light_green_600_36dp);//不可少  单独设置下载图标

        // mBuilder.setSubText("——记住我叫叶良辰")                    //内容下面的一小段文字
        mBuilder.setWhen(System.currentTimeMillis())           //设置通知时间
               // .setLargeIcon(largeBitmap)//设置大图标
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
                //.setAutoCancel(true)                           //设置点击后取消Notification
                .setOngoing(true)//不让清除通知
                .setContentIntent(pendingIntent);


        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_content_layout);

        //contentView.setIcon();
        // contentView.setImageViewResource(R.id.content_view_image, R.drawable.ic_file_download_light_green_700_18dp);
       contentView.setImageViewBitmap(R.id.content_view_image, largeBitmap);
        //contentView.setBitmap();
        contentView.setTextViewText(R.id.content_view_time, DateTool.getChinaDateTime());
        //一样 mBuilder.setCustomContentView(contentView);
        mBuilder.setContent(contentView);

        notification = mBuilder.build();
        notificationManager.notify(NOTIFYID_1, notification);
    }


    public static void updateNotificationProgress(int progress) {

        //Log.i("progress", "updateNotificationProgress: progress:"+progress);
        //更新太频繁  进度会卡慢
        if (progress != mPogressTemp) {
            if (progress == 100) {
                contentView.setTextViewText(R.id.content_view_text, "新版本下载完成,点击安装");
                contentView.setProgressBar(R.id.content_view_progress, 100, progress, false);

            } else {
                contentView.setTextViewText(R.id.content_view_text, "新版本下载中,已完成:" + progress + "%");
                contentView.setProgressBar(R.id.content_view_progress, 100, progress, false);

            }
            mBuilder.setContent(contentView);
            notification = mBuilder.build();
            notificationManager.notify(NOTIFYID_1, notification);

            //
            mPogressTemp = progress;
        }
    }


}
