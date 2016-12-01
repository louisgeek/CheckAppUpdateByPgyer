package com.louisgeek.checkappupdatelib.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.louisgeek.checkappupdatelib.tool.InfoHolderSingleton;
import com.louisgeek.checkappupdatelib.fragment.MyDialogFragmentNormal;
import com.louisgeek.checkappupdatelib.fragment.MyDialogFragmentProgress4Down;
import com.louisgeek.checkappupdatelib.bean.PgyerBaseListBean;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.contract.UpdateContract;
import com.louisgeek.checkappupdatelib.model.UpdateModelImpl;
import com.louisgeek.checkappupdatelib.service.DownLoadFileService;
import com.louisgeek.checkappupdatelib.tool.AppTool;
import com.louisgeek.checkappupdatelib.tool.ThreadUtil;

/**
 * Created by louisgeek on 2016/11/23
 */

public class UpdatePresenterImpl implements UpdateContract.Presenter {

    private UpdateContract.Model mModel = new UpdateModelImpl();

    private UpdateContract.View mView;
    private static final String TAG = "UpdatePresenterImpl";

    public UpdatePresenterImpl(UpdateContract.View view) {
        mView = view;
    }

    public UpdatePresenterImpl() {
    }

    @Override
    public void gainCheckUpdateInfo(final Context context) {
        /**
         *
         */
        mModel.loadUpdateInfo(new UpdateContract.OnLoadDataListener() {
            @Override
            public void onSuccess(PgyerBaseListBean<PgyerGroupBean> data) {
                /**
                 * 找到最后一个版本
                 */
                PgyerGroupBean pgyerGroupBean = findLastVer(data);
                if (pgyerGroupBean != null) {
                    final boolean hasUpdate = isNeedUpdate(context, Integer.valueOf(pgyerGroupBean.getAppVersionNo()), pgyerGroupBean.getAppVersion());

                    if (hasUpdate) {

                        if (mView != null) {
                            /**
                             * 设置回调  此时context应该是FragmentActivity
                             */
                            FragmentActivity fragmentActivity = (FragmentActivity) context;
                            showNoticeUpdateDialog(pgyerGroupBean, fragmentActivity);
                        } else {
                            /**
                             * 没有设置回调  静默下载  直接启动
                             */
                            InfoHolderSingleton.getInstance().putMapObj("pgyerGroupBean",pgyerGroupBean);
                            startDownloadService(context, true);
                        }
                    }
                    //
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mView != null) {
                                mView.backUpdateInfo(hasUpdate);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private void showNoticeUpdateDialog(PgyerGroupBean pgyerGroupBean, final FragmentActivity fragmentActivity) {
        String changelog = pgyerGroupBean.getAppUpdateDescription();
        //changelog = changelog.replace(CheckUpdateTool.TAG_RELEASE, "");
        //
        MyDialogFragmentNormal myDialogFragmentNormal = MyDialogFragmentNormal.newInstance("应用有的更新，是否下载更新？","V"+pgyerGroupBean.getAppVersion()+"更新日志：\n"+changelog);
        myDialogFragmentNormal.setOnBtnClickListener(new MyDialogFragmentNormal.OnBtnClickListener() {
            @Override
            public void onOkBtnClick(DialogInterface dialog) {
                startDownloadService(fragmentActivity, false);
                //显示前台aty的dialog进度条
                showProgress(fragmentActivity);
            }

            @Override
            public void onCancelBtnClick(DialogInterface dialog) {
                //do nothing
            }
        });
        myDialogFragmentNormal.show(fragmentActivity.getSupportFragmentManager(), "notice");
    }

    public static final String ACTION_UPDATE_FINISH_TO_USERFACE = "ACT_UPDATE_FINISH_TO_USERFACE";
    public static final String ACTION_UPDATE_PROGRESS_TO_USERFACE = "ACT_UPDATE_PROGRESS_TO_USERFACE";
    private static final String[] mActionArr = new String[]{
            ACTION_UPDATE_FINISH_TO_USERFACE,
            ACTION_UPDATE_PROGRESS_TO_USERFACE
    };
    private static UserFaceBroadcastReceiver mUserFaceBroadcastReceiver;

    public static void registerMayOnCreate(Context context) {
        /**
         * 注册广播  判断是否为空，为空才将它初始化
         */
        if (mUserFaceBroadcastReceiver == null) {
            mUserFaceBroadcastReceiver = new UserFaceBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            // intentFilter.addAction(DOWNLOAD_SERVICE_ACTION_UPDATE_PROGRESS); //为BroadcastReceiver指定action，即要监听的消息名字。
            for (int i = 0; i < mActionArr.length; i++) {
                intentFilter.addAction(mActionArr[i]);
            }
            context.registerReceiver(mUserFaceBroadcastReceiver, intentFilter);
        }
    }


    public static void unregisterMayOnDestroy(Context context) {
        /**
         * 取消注册广播
         */
        try{
            context.unregisterReceiver(mUserFaceBroadcastReceiver);
        }catch (Exception e){
           // e.printStackTrace();
            Log.w(TAG, "unregisterMayOnDestroy: unregisterReceiver"+e.getMessage());
        }
        try{
            context.stopService(mStartDownLoadFileServiceIntent);
        }catch (Exception e){
           // e.printStackTrace();
            Log.w(TAG, "unregisterMayOnDestroy: stopService"+e.getMessage());
        }
    }

    private static MyDialogFragmentProgress4Down mMyDialogFragmentProgress4Down;

    private void showProgress(FragmentActivity fragmentActivity) {
        mMyDialogFragmentProgress4Down = MyDialogFragmentProgress4Down.newInstance(null);
        mMyDialogFragmentProgress4Down.show(fragmentActivity.getSupportFragmentManager(), "loading");
    }

    private static void updateProgress(int progress) {
        mMyDialogFragmentProgress4Down.setMessageText("新版本下载中，已完成:" + String.valueOf(progress) + "%");
        mMyDialogFragmentProgress4Down.updateProgress(progress);
    }

    private static void finishShow(final Context context, final String savedApkPath) {
        //静默下载时候 为空
        mMyDialogFragmentProgress4Down.setMessageText("新版本下载完成");
        mMyDialogFragmentProgress4Down.finishDownload(new MyDialogFragmentProgress4Down.FinishDownloadListener() {
            @Override
            public void finishDown() {
                AppTool.installApk(context, savedApkPath);
            }
        });
    }
    private static Intent mStartDownLoadFileServiceIntent;
    private static void startDownloadService(Context context, boolean isSilentDownLoad) {
        mStartDownLoadFileServiceIntent = new Intent(context, DownLoadFileService.class);
        mStartDownLoadFileServiceIntent.putExtra("isSilentDownLoad", isSilentDownLoad);
        context.startService(mStartDownLoadFileServiceIntent);
    }

    private PgyerGroupBean findLastVer(PgyerBaseListBean<PgyerGroupBean> data) {
        PgyerGroupBean pgyerGroup = null;
        for (int i = 0; i < data.getData().size(); i++) {
            PgyerGroupBean pgyerGroupBean = data.getData().get(i);
            //最后一个版本
            if ("1".equals(pgyerGroupBean.getAppIsLastest())) {
                //
                pgyerGroup = pgyerGroupBean;
                break;
            }
        }
        return pgyerGroup;
    }

    private boolean isNeedUpdate(Context context, int webNewVersionCode, String webNewVersionName) {
        boolean needUpdate = false;
        int currentVersionCode = AppTool.getVersionCode(context);
        String currentVersionName = AppTool.getVersionName(context);
        if (webNewVersionCode > currentVersionCode) {
            //需要更新
            Log.i("info", "need update app");
            needUpdate = true;
        } else if (webNewVersionCode == currentVersionCode) {
            //如果本地app的versionCode与web上的app的versionCode一致，则需要判断versionName.
            if (!currentVersionName.equals(webNewVersionName)) {
                Log.i("info", "need update app versionName is not the same");
                needUpdate = true;
            }
        } else {
            //不需要更新,当前版本高于web上的app版本.
            Log.i("info", " not need update app");
            needUpdate = false;
        }
        return needUpdate;
    }


    /**
     *
     */
    private static class UserFaceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_FINISH_TO_USERFACE:
                    String savedFilePath = intent.getStringExtra("savedFilePath");
                    finishShow(context, savedFilePath);
                    break;
                case ACTION_UPDATE_PROGRESS_TO_USERFACE:
                    int progress = intent.getIntExtra("progress", 0);
                    updateProgress(progress);
                    break;
            }
        }
    }

}