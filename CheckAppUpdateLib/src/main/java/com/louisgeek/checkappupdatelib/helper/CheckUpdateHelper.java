package com.louisgeek.checkappupdatelib.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import com.louisgeek.checkappupdatelib.tool.InfoHolderSingleton;
import com.louisgeek.checkappupdatelib.fragment.MyDialogFragmentNormal;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.contract.UpdateContract;
import com.louisgeek.checkappupdatelib.presenter.UpdatePresenterImpl;
import com.louisgeek.checkappupdatelib.tool.AppTool;

/**
 * Created by louisgeek on 2016/11/24.
 */

public class CheckUpdateHelper {
    private static String mAppId;
    private static String mApiKey;

    public static void initFirst(String app_id, String api_key) {
        mAppId = app_id;
        mApiKey = api_key;
    }

    public static String getAppId() {
        return mAppId;
    }

    public static String getApiKey() {
        return mApiKey;
    }

    public static void checkUpdateSilent(Context context, CheckUpdateSilentCallBack checkUpdateSilentCallBack) {
        InfoHolderSingleton.getInstance().putMapObj("checkUpdateSilentCallBack", checkUpdateSilentCallBack);
        //没有view回调
        new UpdatePresenterImpl().gainCheckUpdateInfo(context);
    }

    public static void checkUpdate(FragmentActivity fragmentActivity, final CheckUpdateCallBack checkUpdateCallBack) {

        /**
         * 1下载更新初始化
         */
        UpdatePresenterImpl.registerMayOnCreate(fragmentActivity);
        //
        /**
         * 2设置下载更新回调
         */
        final UpdatePresenterImpl updatePresenterImpl = new UpdatePresenterImpl(new UpdateContract.View() {
            @Override
            public void backUpdateInfo(boolean hasUpdate) {
                if (checkUpdateCallBack != null) {
                    checkUpdateCallBack.backHasUpdate(hasUpdate);
                }
            }
        });
        /**
         * 3下载更新
         */
        updatePresenterImpl.gainCheckUpdateInfo(fragmentActivity);

    }


    public static void unregisterCheckUpdate(FragmentActivity fragmentActivity) {
        /**
         * 4解除下载更注册
         */
        UpdatePresenterImpl.unregisterMayOnDestroy(fragmentActivity);
    }

    public static void showApkIsInstallDialog(final FragmentActivity fragmentActivity, final PgyerGroupBean pgyerGroupBean, final String savedApkPath) {
        MyDialogFragmentNormal myDialogFragmentNormal = MyDialogFragmentNormal.newInstance("新的安装包已经准备好,是否安装？", "V" + pgyerGroupBean.getAppVersion() + "更新日志：\n"
                + pgyerGroupBean.getAppUpdateDescription());
        myDialogFragmentNormal.setOnBtnClickListener(new MyDialogFragmentNormal.OnBtnClickListener() {
            @Override
            public void onOkBtnClick(DialogInterface dialogInterface) {
                AppTool.installApk(fragmentActivity, savedApkPath);
            }

            @Override
            public void onCancelBtnClick(DialogInterface dialogInterface) {
            }
        });
        try {
            myDialogFragmentNormal.show(fragmentActivity.getSupportFragmentManager(), "myNo");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface CheckUpdateCallBack {
        void backHasUpdate(boolean hasUpdate);
    }

    public interface CheckUpdateSilentCallBack {
        void backUpdateInfo(PgyerGroupBean pgyerGroupBean, String savedApkPath);
    }
}
