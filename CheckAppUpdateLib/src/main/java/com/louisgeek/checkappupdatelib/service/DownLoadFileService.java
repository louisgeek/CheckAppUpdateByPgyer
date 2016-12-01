package com.louisgeek.checkappupdatelib.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.louisgeek.checkappupdatelib.tool.InfoHolderSingleton;
import com.louisgeek.checkappupdatelib.R;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.contract.UpdateContract;
import com.louisgeek.checkappupdatelib.helper.CheckUpdateHelper;
import com.louisgeek.checkappupdatelib.presenter.UpdatePresenterImpl;
import com.louisgeek.checkappupdatelib.tool.AppTool;
import com.louisgeek.checkappupdatelib.tool.DateTool;
import com.louisgeek.checkappupdatelib.tool.HttpTool;

/**
 * Created by louisgeek on 2016/11/23.
 */

public class DownLoadFileService extends Service {

    private static final String TAG = "DownLoadFileService";
    private boolean mIsSilentDownLoad = false;

    public static final String DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS = "ACT_UPDATE_PROGRESS";
    public static final String DOWNLOAD_SERVICE_ACTION_UPDATE_FINISH = "ACT_UPDATE_FINISH";
    private static final String[] mActionArr = new String[]{
            DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS,
            DOWNLOAD_SERVICE_ACTION_UPDATE_FINISH
    };
    private DownLoadServiceBroadcastReceiver mDownLoadServiceBroadcastReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 注册广播
         */
        mDownLoadServiceBroadcastReceiver = new DownLoadServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // intentFilter.addAction(DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS); //为BroadcastReceiver指定action，即要监听的消息名字。
        for (int i = 0; i < mActionArr.length; i++) {
            intentFilter.addAction(mActionArr[i]);
        }
        registerReceiver(mDownLoadServiceBroadcastReceiver, intentFilter);


        /**
         * 循环任务
         */
      /*  handler.post(taskRunnable);//立即启动*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mIsSilentDownLoad = intent.getBooleanExtra("isSilentDownLoad", false);
        /**
         * 初始化前台服务通知
         */
        if (!mIsSilentDownLoad) {
            initForegroundNotification();
        }
        initDownLoad();

     //  return super.onStartCommand(intent, flags, startId);
       return Service.START_REDELIVER_INTENT;//重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
    }

    private void initDownLoad() {
        /**
         *下载
         */
        HttpTool.getUrlDownloadFile(UpdateContract.getAppInstallSimpleUrl, new HttpTool.OnUrlDownloadFileCallBack() {
            @Override
            public void onSuccess(String savedFilePath) {
                Log.d(TAG, "onSuccess: savedFilePath;" + savedFilePath);
                if (!mIsSilentDownLoad) {
                    Intent intent = new Intent(DOWNLOAD_SERVICE_ACTION_UPDATE_FINISH);
                    intent.putExtra("savedFilePath", savedFilePath);
                    sendBroadcast(intent);
                    //
                    Intent intent2userface = new Intent(UpdatePresenterImpl.ACTION_UPDATE_FINISH_TO_USERFACE);
                    intent2userface.putExtra("savedFilePath", savedFilePath);
                    sendBroadcast(intent2userface);
                } else {
                    //静默时候 回调
                    CheckUpdateHelper.CheckUpdateSilentCallBack checkUpdateSilentCallBack = (CheckUpdateHelper.CheckUpdateSilentCallBack) InfoHolderSingleton.getInstance().getMapObj("checkUpdateSilentCallBack");
                    if (checkUpdateSilentCallBack != null) {
                        PgyerGroupBean pgyerGroupBean= (PgyerGroupBean) InfoHolderSingleton.getInstance().getMapObj("pgyerGroupBean");
                        checkUpdateSilentCallBack.backUpdateInfo(pgyerGroupBean,savedFilePath);
                    }
                }

            }

            @Override
            public void onError(String errorMsg) {
                Log.d(TAG, "onError: errorMsg;" + errorMsg);
            }

            @Override
            public void OnProgress(int progress) {
                // Log.d(TAG, "OnProgress: progress;"+progress);
                if (!mIsSilentDownLoad) {
                    Intent intent = new Intent(DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS);
                    intent.putExtra("progress", progress);
                    sendBroadcast(intent);
                    //
                    Intent intent2userface = new Intent(UpdatePresenterImpl.ACTION_UPDATE_PROGRESS_TO_USERFACE);
                    intent2userface.putExtra("progress", progress);
                    sendBroadcast(intent2userface);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: service XXSS");
        /**
         * 取消注册广播
         */
        unregisterReceiver(mDownLoadServiceBroadcastReceiver);
    }


    /**
     *
     */
    private class DownLoadServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: action:" + action);
            switch (action) {
                case DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS:
                    int progress = intent.getIntExtra("progress", 0);
                    //
                    updateNotificationProgress(progress);
                    break;
                case DOWNLOAD_SERVICE_ACTION_UPDATE_FINISH:
                    String savedFilePath = intent.getStringExtra("savedFilePath");
                    //
                    updateNotificationFinishClick(savedFilePath);
                    break;
            }
        }
    }

    /**
     *
     */
  /*  private Handler handler = new Handler();
    private Runnable taskRunnable = new Runnable() {
        public void run() {
            //需要执行的代码
            Log.d(TAG, "run: taskRunnable:" + taskRunnable);
            handler.postDelayed(taskRunnable, 1000);//设置延迟时间  1000毫秒
            //### handler.post(taskRunnable);
        }
    };*/
    /**
     * Notification
     */
    private NotificationCompat.Builder mBuilder;
    private RemoteViews mContentView;


    public void initForegroundNotification() {

        //创建大图标的Bitmap
        // Bitmap largeBitmap = BitmapFactory.decodeResource(context.getResources(),  R.drawable.ic_file_download_light_green_700_18dp);
        Bitmap largeBitmap = AppTool.getIconBitmap(this);

        // notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //NotificationCompat.Builder
        mBuilder = new NotificationCompat.Builder(this);
        //mBuilder.setAutoCancel(false);
        //mBuilder.setContentTitle("有新版本");
        // mBuilder.setContentText("下载中");
        mBuilder.setTicker("新版" + AppTool.getAppName(this) + "正在下载中");//设置收到通知时在顶部显示的文字信息
        mBuilder.setSmallIcon(R.drawable.ic_file_download_light_green_600_36dp);//不可少  单独设置下载图标

        // mBuilder.setSubText("内容下面的一小段文字")                    //内容下面的一小段文字
        mBuilder.setWhen(System.currentTimeMillis())           //设置通知时间
                // .setLargeIcon(largeBitmap)//设置大图标
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.biaobiao))  //设置自定义的提示音
                //.setAutoCancel(true)                           //设置点击后取消Notification
                .setOngoing(true);//不让清除通知
        //.setContentIntent(pendingIntent);


        mContentView = new RemoteViews(this.getPackageName(), R.layout.notification_content_layout);

        //mContentView.setIcon();
        // mContentView.setImageViewResource(R.id.content_view_image, R.drawable.ic_file_download_light_green_700_18dp);
        mContentView.setImageViewBitmap(R.id.content_view_image, largeBitmap);
        //mContentView.setBitmap();
        mContentView.setTextViewText(R.id.content_view_time, DateTool.getChinaDateTime());
        //一样 mBuilder.setCustomContentView(contentView);
        mBuilder.setContent(mContentView);

        Notification notification = mBuilder.build();
        //### notificationManager.notify(1, notification); //换成 startForeground(1, notification);
        /**
         *  启用前台服务  让 app kill后服务还能继续运行
         */
        startForeground(1, notification);
    }


    public void updateNotificationProgress(int progress) {

        Log.d(TAG, "updateNotificationProgress: " + progress);
        //Log.i("progress", "updateNotificationProgress: progress:"+progress);

        mContentView.setTextViewText(R.id.content_view_text, "新版本下载中,已完成:" + progress + "%");
        mContentView.setProgressBar(R.id.content_view_progress, 100, progress, false);

        mBuilder.setContent(mContentView);
        Notification notification = mBuilder.build();
        //###notificationManager.notify(1, notification);
        startForeground(1, notification);//前台服务

    }

    public void updateNotificationFinishClick(String savedFilePath) {

        mContentView.setTextViewText(R.id.content_view_text, "新版本下载完成,点击安装");
        mContentView.setProgressBar(R.id.content_view_progress, 100, 100, false);

        mBuilder.setContent(mContentView);

        /**
         * install app intent
         */
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + savedFilePath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /**
         *
         */
        PendingIntent pendingIntent4install = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent4install);

        mBuilder.setAutoCancel(true);//点击完成 可清除

        Notification notification = mBuilder.build();
        //###notificationManager.notify(1, notification);
        startForeground(1, notification);//前台服务
        // stopForeground(true);//前台服务 取消
    }


    /**
     *
     */

}
