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
import com.louisgeek.checkappupdatelib.bean.PgyerBeanBase;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.callback.CheckUpdateCallBack;
import com.louisgeek.checkappupdatelib.callback.GetDownloadTokenCallBack;
import com.louisgeek.checkappupdatelib.tool.AppTool;
import com.louisgeek.checkappupdatelib.tool.SdCardTool;
import com.louisgeek.checkappupdatelib.tool.ThreadUtil;

import static com.louisgeek.checkappupdatelib.CheckUpdateTool.NEED_UPDATE_CODE;
import static com.louisgeek.checkappupdatelib.CheckUpdateTool.NO_NEED_UPDATE_CODE;

/**
 * Created by louisgeek on 2016/9/29.
 */
public class DownloadManagerCenter {
    private static final String TAG = "DownloadManagerCenter";
    private static MyDialogFragmentProgress4Down myDialogFragmentProgress4Down;
    private static DownloadFileService mDownloadFileService;
    private static boolean mSilentDownload;//静默下载
    public static final String TAG_RELEASE = "W3JlbGVhc2Vd";//[release]  base64  W3JlbGVhc2Vd
    private static Context mContext;
    //private static Class<?> mClazz;
    private static FragmentManager mFragmentManager;

    public static void startDown(Context context, OnUserOperationMessageCallBack callBack) {
        startDown(context, false, callBack);
    }

    public static void startDown(Context context, boolean onlyCheckReleaseVersion, OnUserOperationMessageCallBack callBack) {
        startDown(context, onlyCheckReleaseVersion, false, callBack);
    }

    public static void startDown(final Context context, final boolean onlyCheckReleaseVersion, final boolean silentDownload, final OnUserOperationMessageCallBack callBack) {
        mContext = context;
        // mClazz=clazz;
        //！！！！！！！！！
        mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        if (PGYER_APP_ID != null && !PGYER_APP_ID.equals("") && PGYER_API_KEY != null && !PGYER_API_KEY.equals("")) {

            DownloadPgyerApkTool.getGroupInfo(new DownloadPgyerApkTool.GroupInfoCallBack() {
                /**
                 * DEMO
                 *  "appKey":"6063000f05a67b3f5bbc14fd0a298e47",
                 "appType":"2",//应用类型（1:iOS; 2:Android）
                 "appIsLastest":"2",//是否是最新版（1:是; 2:否）
                 "appFileName":"CloudSeeds_Android108.apk",
                 "appFileSize":"6340136",//App 文件大小
                 "appName":"云种",//应用名称
                 "appVersion":"1.0",    //版本名称
                 "appVersionNo":"1",   //版本号
                 "appBuildVersion":"1", //蒲公英生成的用于区分历史版本的build号
                 "appIdentifier":"com.sunstar.cloudseeds",//应用程序包名，iOS为BundleId，Android为包名
                 "appIcon":"eef6a7133243f4b924f58cc218b4b5b8",//应用的Icon图标key，访问地址为 http://o1wh05aeh.qnssl.com/image/view/app_icons/[应用的Icon图标key]
                 "appDescription":"云种，是一个专为家庭农场量身打造的移动端的农作物种植跟踪系统。多类目的种植品种，全程记录各时期的种植表现，更加方便的拍照记录，自动化的位置、气候判断，使家庭农场的管理更加便捷。",
                 "appUpdateDescription":"10-08",//应用更新说明
                 "appScreenshots":"",
                 "appShortcutUrl":"lvseeds",//应用短链接
                 "appCreated":"2015-10-08 10:08:05",
                 "appUpdated":"2015-10-08 10:08:05",
                 "appQRCodeURL":"http://static.pgyer.com/app/qrcodeHistory/e2775ec0c68dce8183048289bc45a9ad945ff9e8bc96372b9f5fd7b90b9eccf4"

                 * @param pgyerBeanBasePgyerGroupBean
                 */
                @Override
                public void groupInfo(PgyerBeanBase<PgyerGroupBean> pgyerBeanBasePgyerGroupBean) {
                    final int code;
                    final String message;
                    if (pgyerBeanBasePgyerGroupBean.getData().getData() != null
                            && pgyerBeanBasePgyerGroupBean.getData().getData().size() > 0) {
                        for (int i = 0; i < pgyerBeanBasePgyerGroupBean.getData().getData().size(); i++) {
                            PgyerGroupBean.DataBean pgbdb = pgyerBeanBasePgyerGroupBean.getData().getData().get(i);
                            //是否是最新版（1:是; 2:否）
                            if (pgbdb.getAppIsLastest().equals("1")) {
                                //
                                int versionCode = Integer.parseInt(pgbdb.getAppVersionNo());
                                String versionName = pgbdb.getAppVersion();
                                boolean isNeed = CheckUpdateTool.isNeedUpdate(context, versionCode, versionName);
                                String changelog = pgbdb.getAppUpdateDescription();
                                // Log.i(TAG, "doCheckOnline: isNeed:" + isNeed);
                                //
                                if (isNeed) {
                                    if (onlyCheckReleaseVersion) {
                                        if (changelog.contains(DownloadManagerCenter.TAG_RELEASE)) {
                                            code = NEED_UPDATE_CODE;
                                            message = "需要更新";
                                            Log.i(TAG, "message3:正式版有更新");
                                            goToPgyerDown(silentDownload,versionName, changelog);
                                        } else {
                                            code = NO_NEED_UPDATE_CODE;
                                            message = "不需要更新";
                                            Log.i(TAG, "message4:测试版有更新 正式版无更新");
                                        }
                                    } else {
                                        code = NEED_UPDATE_CODE;
                                        message = "需要更新";
                                        Log.i(TAG, "message2:正式版或测试版有更新");
                                    }
                                } else {
                                    code = NO_NEED_UPDATE_CODE;
                                    message = "不需要更新";
                                    Log.i(TAG, "message1:无更新");
                                }
                            } else {
                                code = -12;
                                message = "网络version不存在";
                            }
                            //
                            ThreadUtil.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onUserOperationMessage(code, message);
                                }
                            });
                            ///
                            break;
                        }

                        ///
                    }


                }
            });
            //直接返回 不执行fir.im的
            return;
        }
        //
        CheckUpdateCallBack checkUpdateCallBack = new CheckUpdateCallBack() {
            @Override
            public void OnSuccess(String result, int statusCode, FirImBean firImBean, final boolean silentDownload) {
                if (statusCode == NEED_UPDATE_CODE) {

                    if (silentDownload) {
                        doGetDownCode(silentDownload);
                    } else {
                        String changelog = firImBean.getChangelog();
                        changelog = changelog.replace(TAG_RELEASE, "");
                        //
                        MyDialogFragmentNormal myDialogFragmentNormal = MyDialogFragmentNormal.newInstance("有新的更新", "最新版本：" + firImBean.getVersionShort() + "\n\n" + changelog);
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
                } else if (statusCode == NO_NEED_UPDATE_CODE) {
                    //do nothing
                }
            }

            @Override
            public void OnSuccessNotifyUI(String result, int statusCode, FirImBean firImBean, boolean silentDownload) {
                if (statusCode == NO_NEED_UPDATE_CODE) {
                    //#####Toast.makeText(mContext, "不需要更新", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "OnSuccessNotifyUI: App不需要更新");
                }
                /**
                 * 回调给user
                 */
                if (callBack != null) {
                    callBack.onUserOperationMessage(statusCode, result);
                }
            }

            @Override
            public void OnError(String errorMsg, int statusCode) {
                Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
            }

            @Override
            public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                // Toast.makeText(mContext, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                /**
                 * 回调给user
                 */
                if (callBack != null) {
                    callBack.onUserOperationMessage(statusCode, errorMsg);
                }
            }
        };
        CheckUpdateTool.doCheckOnline(context, checkUpdateCallBack, onlyCheckReleaseVersion, silentDownload);
        //

    }

    private static void goToPgyerDown(final boolean silentDownload, String versionName, String changelog) {
        if (!SdCardTool.hasSDCardMounted()) {
            Log.e(TAG, "OnSuccess:has NO SDCard Mounted");
            return;
        }
        if (silentDownload) {
            //
            doDownloadService(DownloadPgyerApkTool.getAppInstallSimpleUrl, true);
        } else {
            changelog = changelog.replace(TAG_RELEASE, "");
            //
            MyDialogFragmentNormal myDialogFragmentNormal = MyDialogFragmentNormal.newInstance("有新的更新", "最新版本：" + versionName + "\n\n" + changelog);
            myDialogFragmentNormal.setOnBtnClickListener(new MyDialogFragmentNormal.OnBtnClickListener() {
                @Override
                public void onOkBtnClick(DialogInterface dialog) {

                    //
                    doDownloadService(DownloadPgyerApkTool.getAppInstallSimpleUrl, true);
                }

                @Override
                public void onCancelBtnClick(DialogInterface dialog) {
                    //do nothing
                }
            });

            myDialogFragmentNormal.show(mFragmentManager, "notice");
//
        }
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
                    String downloadAPKPathUrl = String.format(baseDownloadAPKUrlPath, DownloadManagerCenter.ID_STR, download_token);

                    if (!SdCardTool.hasSDCardMounted()) {
                        Log.e(TAG, "OnSuccess:has NO SDCard Mounted");
                        return;
                    }

                    doDownloadService(downloadAPKPathUrl, false);

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

    private static void doDownloadService(String downloadAPKPathUrl, boolean isPger) {
        //
        if (!mSilentDownload) {
            myDialogFragmentProgress4Down = MyDialogFragmentProgress4Down.newInstance(null);
            myDialogFragmentProgress4Down.show(mFragmentManager, "loading");
        }
        //
        Intent intent = new Intent(mContext, DownloadFileService.class);
        intent.putExtra("downloadAPKPathUrl", downloadAPKPathUrl);
        intent.putExtra("isPger", isPger);
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
     * It is best to call at the aty  onDestroy function
     */
    public static void unbindDownService() {
        if (mContext == null || mServiceConnection == null) {
            return;
        }
        try {
            mContext.unbindService(mServiceConnection);
        } catch (Exception e) {
            Log.w(TAG, "unbindDownService:" + e.getMessage());
        }
    }

    public static String ID_STR;
    public static String API_TOKEN;
    public static String mPackageName;

    public static void initFirimApiConfig(String idStr, String apiToken, String appPackageName) {
        ID_STR = idStr;
        API_TOKEN = apiToken;
        mPackageName = appPackageName;
    }

    public static String PGYER_APP_ID;
    public static String PGYER_API_KEY;

    public static void initPgyerApiConfig(String app_id, String api_key, String appPackageName) {
        PGYER_APP_ID = app_id;
        PGYER_API_KEY = api_key;
        mPackageName = appPackageName;
    }

    /**
     * you  may deal "statusCode"  by
     * CheckUpdateTool.NO_NEED_UPDATE_CODE
     * and
     * CheckUpdateTool.NEED_UPDATE_CODE
     */
    public interface OnUserOperationMessageCallBack {
        void onUserOperationMessage(int statusCode, String message);
    }
}
