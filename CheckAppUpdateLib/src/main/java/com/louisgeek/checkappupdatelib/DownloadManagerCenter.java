package com.louisgeek.checkappupdatelib;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.louisgeek.checkappupdatelib.bean.FirImBean;
import com.louisgeek.checkappupdatelib.callback.CheckUpdateCallBack;
import com.louisgeek.checkappupdatelib.callback.GetDownloadTokenCallBack;
import com.louisgeek.checkappupdatelib.tool.AppTool;
import com.louisgeek.checkappupdatelib.tool.SdCardTool;

/**
 * Created by louisgeek on 2016/9/29.
 */
public class DownloadManagerCenter {
    private static final String TAG = "DownloadManagerCenter";
    private static MyDialogFragmentProgress4Down myDialogFragmentProgress4Down;
    private static DownloadFileService mDownloadFileService;
    private static boolean mSilentDownload;
    private static Context mContext;
    //private static Class<?> mClazz;
    private static FragmentManager mFragmentManager;
    public static void startDown(Context context){
        startDown(context,true,false);
    }
    public static void startDown(Context context,boolean onlyCheckReleaseVersion){
        startDown(context,onlyCheckReleaseVersion,false);
    }
    public static void startDown(Context context,boolean onlyCheckReleaseVersion,boolean silentDownload){
        mContext=context;
       // mClazz=clazz;
        //！！！！！！！！！
        mFragmentManager=((FragmentActivity)context).getSupportFragmentManager();
        //
        CheckUpdateCallBack checkUpdateCallBack = new CheckUpdateCallBack() {
            @Override
            public void OnSuccess(String result, int statusCode, FirImBean firImBean, final boolean silentDownload) {
                if (statusCode == CheckUpdateTool.NEED_UPDATE_CODE) {

                    if (silentDownload) {
                        doGetDownCode(silentDownload);
                    } else {
                        String changelog=firImBean.getChangelog();
                        changelog=changelog.replace(CheckUpdateTool.TAG_RELEASE,"");
                        //
                        MyDialogFragmentNormal myDialogFragmentNormal = MyDialogFragmentNormal.newInstance("有新的更新", "最新版本：" + firImBean.getVersionShort() + "\n\n" +changelog);
                        myDialogFragmentNormal.setOnBtnClickListener(new MyDialogFragmentNormal.OnBtnClickListener() {
                            @Override
                            public void onOkBtnClick(DialogInterface dialog) {
                                doGetDownCode(silentDownload);
                            }

                            @Override
                            public void onCancelBtnClick(DialogInterface dialog) {
                                //do nothing
                            }
                        });

                        myDialogFragmentNormal.show(mFragmentManager, "notice");
//
                    }
                } else if (statusCode == CheckUpdateTool.NO_NEED_UPDATE_CODE) {
                    //do nothing
                }
            }

            @Override
            public void OnSuccessNotifyUI(String result, int statusCode, FirImBean firImBean, boolean silentDownload) {
                if (statusCode == CheckUpdateTool.NO_NEED_UPDATE_CODE) {
                    Toast.makeText(mContext, "不需要更新", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnError(String errorMsg, int statusCode) {
                Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
            }

            @Override
            public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                Toast.makeText(mContext, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        };
        CheckUpdateTool.doCheckOnline(context, checkUpdateCallBack, onlyCheckReleaseVersion, silentDownload);
        //
    }

    private static void doGetDownCode(boolean silentDownload) {
        mSilentDownload = silentDownload;

        DownloadFirmImApkTool.doGetDownloadToken(new GetDownloadTokenCallBack() {
            @Override
            public void OnSuccess(String result, int statusCode) {
                if (statusCode == DownloadFirmImApkTool.GET_DOWNLOAD_TOKEN_SUCCESS_CODE) {
                    //
                    String download_token = result;

                    Log.i(TAG, "OnSuccess: download_token:" + download_token);

                    String baseDownloadAPKUrlPath = "http://download.fir.im/apps/%s/install?download_token=%s";
                    String downloadAPKPathUrl = String.format(baseDownloadAPKUrlPath, CheckUpdateTool.ID_STR, download_token);

                    if (!SdCardTool.hasSDCardMounted()) {
                        Log.e(TAG, "OnSuccess:has NO SDCard Mounted");
                        return;
                    }

                    doDownloadService(downloadAPKPathUrl);

                } else {
                    Log.i(TAG, "OnSuccess: " + statusCode + result);
                }
            }

            @Override
            public void OnSuccessNotifyUI(String result, int statusCode) {

            }

            @Override
            public void OnError(String errorMsg, int statusCode) {
                Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
            }

            @Override
            public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                Toast.makeText(mContext, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void doDownloadService(String downloadAPKPathUrl) {
        //
        if (!mSilentDownload) {
            myDialogFragmentProgress4Down = MyDialogFragmentProgress4Down.newInstance(null);
            myDialogFragmentProgress4Down.show(mFragmentManager, "loading");
        }
        //
        Intent intent = new Intent(mContext, DownloadFileService.class);
        intent.putExtra("downloadAPKPathUrl", downloadAPKPathUrl);
        //////////###startService(intent);
        mContext.bindService(intent, mServiceConnection, mContext.BIND_AUTO_CREATE);//BIND_AUTO_CREATE


        if (!mSilentDownload) {
            //
            NotificationMangerCenter.initNotification(mContext);
        }

    }


    private static int progressTemp = -1;
    private static ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DownloadFileService.DownloadFileBinder downloadFileBinder = (DownloadFileService.DownloadFileBinder) iBinder;
            mDownloadFileService = downloadFileBinder.getNowService();
            mDownloadFileService.setOnBackProgressListener(new DownloadFileService.OnBackProgressListener() {
                @Override
                public void onBackProgress(int progress) {
                    //
                    if (!mSilentDownload && myDialogFragmentProgress4Down != null) {
                        //更新太频繁  进度会卡慢
                        if (progress != progressTemp) {
                            myDialogFragmentProgress4Down.updateProgress(progress);
                            myDialogFragmentProgress4Down.setMessageText("新版本下载中，已完成:" + String.valueOf(progress) + "%");
                            progressTemp = progress;
                        }
                    }
                    if (!mSilentDownload) {
                        //
                        NotificationMangerCenter.updateNotificationProgress(progress);
                    }
                }
            });
            mDownloadFileService.setOnDownLoadCompleteListener(new DownloadFileService.OnDownLoadCompleteListener() {
                @Override
                public void onDownLoadComplete(final String apkPath) {
                    //下载完解除绑定
                    mContext.unbindService(mServiceConnection);

                    if (!mSilentDownload) {
                        //
                        NotificationMangerCenter.updateNotificationClick(apkPath);
                    }
                    //安装
                    AppTool.installApk(mContext, apkPath);
                    //
                    if (!mSilentDownload && myDialogFragmentProgress4Down != null) {
                        myDialogFragmentProgress4Down.setMessageText("新版本下载完成");
                        myDialogFragmentProgress4Down.finishDownload(new MyDialogFragmentProgress4Down.FinishDownloadListener() {
                            @Override
                            public void finishDown() {
                                //安装
                                AppTool.installApk(mContext, apkPath);
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /**
     *
       It is best to call at the aty  onDestroy function
     */
    public static void unbindDownService() {
        if (mContext==null||mServiceConnection==null){
            return;
        }
        mContext.unbindService(mServiceConnection);
    }
}
