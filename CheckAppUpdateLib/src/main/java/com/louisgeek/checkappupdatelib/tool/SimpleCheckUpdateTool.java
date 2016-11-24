package com.louisgeek.checkappupdatelib.tool;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.helper.CheckUpdateHelper;

/**
 * Created by louisgeek on 2016/11/24.
 */

public class SimpleCheckUpdateTool {
    public static void updateSilentDown(final FragmentActivity fragmentActivity, String app_id, String api_key) {
        CheckUpdateHelper.initFirst(app_id, api_key);
        CheckUpdateHelper.checkUpdateSilent(fragmentActivity, new CheckUpdateHelper.CheckUpdateSilentCallBack() {
            @Override
            public void backUpdateInfo(PgyerGroupBean pgyerGroupBean, String savedApkPath) {
                CheckUpdateHelper.showApkIsInstallDialog(fragmentActivity, pgyerGroupBean, savedApkPath);
            }
        });
    }
    public static void updateNormal_HasNoMsg(final FragmentActivity fragmentActivity, String app_id, String api_key) {
        CheckUpdateHelper.initFirst(app_id, api_key);
        CheckUpdateHelper.checkUpdate(fragmentActivity, null);
    }
    public static void updateNormal(final FragmentActivity fragmentActivity, String app_id, String api_key) {
        CheckUpdateHelper.initFirst(app_id, api_key);
        CheckUpdateHelper.checkUpdate(fragmentActivity, new CheckUpdateHelper.CheckUpdateCallBack() {
            @Override
            public void backHasUpdate(boolean hasUpdate) {
                if (!hasUpdate) {
                    Toast.makeText(fragmentActivity, "当前已经是最新版本！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateNormalUnregister(final FragmentActivity fragmentActivity) {
        CheckUpdateHelper.unregisterCheckUpdate(fragmentActivity);
    }
}
