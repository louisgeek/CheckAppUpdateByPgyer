package com.louisgeek.checkappupdatelib.contract;

import android.content.Context;

import com.louisgeek.checkappupdatelib.bean.PgyerBaseListBean;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.helper.CheckUpdateHelper;

/**
 * Created by louisgeek on 2016/11/23.
 */

public class UpdateContract {

/*    private static String PGYER_APP_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";//应用App组ID
    private static String PGYER_API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";//api_key*/

    @Deprecated //不知道是不是真的是倒序  用getGroupInfo 判断返回字段 appIsLastest		是否是最新版（1:是; 2:否）
    private static String postAllVersionUrl = "http://www.pgyer.com/apiv1/app/builds";//POST
    private static String postAllVersionParamStr = String.format("aId=%s&_api_key=%s&page=%s", CheckUpdateHelper.getAppId(), CheckUpdateHelper.getApiKey(), "1");
    //
    public static String postGroupInfoUrl = "http://www.pgyer.com/apiv1/app/viewGroup";//POST
    public static String postGroupInfoParamStr = String.format("aId=%s&_api_key=%s", CheckUpdateHelper.getAppId(), CheckUpdateHelper.getApiKey());//POST

    /**
     * 来自  https://www.pgyer.com/doc/api#installApp 文档
     * 为了适应Android和IOS  会重定向到itms-services://?action=download-manifest&url=http://www.pgyer.com/app/plist/{aKey}
     * 不能直接下载到apk
     */
    @Deprecated
    private static String getAppInstall = "http://www.pgyer.com/apiv1/app/install";//GET
    /*
    //demo http://www.pgyer.com/apiv1/app/install?_api_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&aKey=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        @Deprecated
       private static String getAppInstallUrl=String.format("%s?aKey=%s&_api_key=%s",getAppInstall,appKey,api_key);
    */
    /**
     * 来自应用内API选项卡  like https://www.pgyer.com/manager/api/index/xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * 区别是 这个api用aId （app组id） 上面用的是 aKey （app 唯一标识key） 为啥要分两个呢。。。搞不懂
     */
    private static String getAppInstallSimple = "http://www.pgyer.com/apiv1/app/install";//GET
    //demo  https://www.pgyer.com/apiv1/app/install?aId=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&_api_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //or  http://www.pgyer.com/apiv1/app/install?aId=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&_api_key=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public static String getAppInstallSimpleUrl = String.format("%s?aId=%s&_api_key=%s", getAppInstallSimple, CheckUpdateHelper.getAppId(), CheckUpdateHelper.getApiKey());


    public interface View {
        void backUpdateInfo(boolean hasUpdate);
    }

    public interface Presenter {
        void gainCheckUpdateInfo(Context context);
    }

    public interface Model {
        void loadUpdateInfo(OnLoadDataListener onLoadDataListener);
    }

    public interface OnLoadDataListener {
        void onSuccess(PgyerBaseListBean<PgyerGroupBean> data);

        void onError(String errorMsg);
    }

}