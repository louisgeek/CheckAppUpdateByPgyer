package com.louisgeek.checkappupdatelib;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.louisgeek.checkappupdatelib.callback.DownloadApkCallBack;

/**
 * Created by louisgeek on 2016/9/28.
 */
public class DownloadFileService extends Service {

    private static final String TAG = "DownloadFileService";

    /* public int getProgress() {
         return mProgress;
     }

     private int mProgress;*/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");

        doDownloadTing(intent);

        return downloadFileBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG, "onRebind");
    }

    private void doDownloadTing(Intent intent) {
        //
        final String downloadAPKPathUrl = intent.getStringExtra("downloadAPKPathUrl");
        Log.i(TAG, "onStartCommand: downloadAPKPathUrl");
        /**耗时操作必备*/
        new Thread(new Runnable() {
            @Override
            public void run() {

                DownloadFirmImApkTool.doDownloadApk(downloadAPKPathUrl, new DownloadApkCallBack() {
                    @Override
                    public void OnSuccess(String result, int statusCode) {

                    }

                    @Override
                    public void OnSuccessNotifyUI(String result, int statusCode) {
                        if (statusCode == DownloadFirmImApkTool.DOWNLOAD_APK_SUCCESS_CODE) {
                            //Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "OnSuccessNotifyUI: 下载完成");

                                                    /*if (myDialogFragmentProgress != null) {
                                                        myDialogFragmentProgress.dismiss();
                                                    }*/
                            final String apkPath = result;
                            if (onDownLoadCompleteListener != null) {
                                onDownLoadCompleteListener.onDownLoadComplete(apkPath);
                            }

                        }
                    }

                    @Override
                    public void OnError(String errorMsg, int statusCode) {
                        Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
                    }

                    @Override
                    public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                      /* if (myDialogFragmentProgress4Down != null) {
                           myDialogFragmentProgress4Down.dismiss();
                       }*/
                        //Toast.makeText(MainActivity.this, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnProgressNotifyUI(int progress) {
                     /*  if (myDialogFragmentProgress4Down != null) {
                           myDialogFragmentProgress4Down.updateProgress(progress);
                       }*/
                        //  mProgress=progress;
                        if (onBackProgressListener != null) {
                            onBackProgressListener.onBackProgress(progress);
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private DownloadFileBinder downloadFileBinder = new DownloadFileBinder();

    public class DownloadFileBinder extends Binder {

        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public DownloadFileService getNowService() {
            return DownloadFileService.this;
        }

    }

    public interface OnBackProgressListener {
        void onBackProgress(int progress);
    }

    public void setOnBackProgressListener(OnBackProgressListener onBackProgressListener) {
        this.onBackProgressListener = onBackProgressListener;
    }

    private OnBackProgressListener onBackProgressListener;

    public interface OnDownLoadCompleteListener {
        void onDownLoadComplete(String apkPath);
    }

    private OnDownLoadCompleteListener onDownLoadCompleteListener;

    public void setOnDownLoadCompleteListener(OnDownLoadCompleteListener onDownLoadCompleteListener) {
        this.onDownLoadCompleteListener = onDownLoadCompleteListener;
    }
}
