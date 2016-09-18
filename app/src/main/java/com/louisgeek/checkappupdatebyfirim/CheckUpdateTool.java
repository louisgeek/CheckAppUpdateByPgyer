package com.louisgeek.checkappupdatebyfirim;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.louisgeek.checkappupdatebyfirim.callback.CheckUpdateCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by louisgeek on 2016/9/18.
 */
public class CheckUpdateTool {

    private static final String TAG = "CheckUpdateTool";
    public static final int NEED_UPDATE_CODE = 11;
    public static final int NO_NEED_UPDATE_CODE = 10;
    public final static String ID_STR = "57d4ba0e959d6911010001a5";
    public final static String API_TOKEN = "b87f6759f2862d5d66605f66e186c3bb";
    private static String baseUrlByID = "http://api.fir.im/apps/latest/%s?api_token=%s";

    private static String packageName = "com.sunstar.cloudseeds";
    //使用 bundle_id / Package name 请求必填  type应用类型 ( ios / android )
    private static String baseUrlByPackageName = "http://api.fir.im/apps/latest/%s?api_token=%s&type=android";
    private static String checkUpdateUrlByID = String.format(baseUrlByID, ID_STR, API_TOKEN);
    private static String checkUpdateUrlByPackageName = String.format(baseUrlByPackageName, packageName, API_TOKEN);

    public static void doCheckOnline(final Context context, final CheckUpdateCallBack checkUpdateCallBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int code = -1;
                String message = "";
                //
                String json = checkUpdate();
                Log.i(TAG, "doCheckOnline: json:" + json);
                if (json != null && !json.equals("")) {
                    FirImBean firImBean = parseFirImJson(json);
                    if (firImBean != null) {
                        if (firImBean.getVersion() != null && !firImBean.getVersion().equals("")) {
                            int versionCode = Integer.parseInt(firImBean.getVersion());
                            boolean isNeed = isNeedUpdate(context, versionCode, firImBean.getVersionShort());
                            Log.i(TAG, "doCheckOnline: isNeed:" + isNeed);
                            if (isNeed) {
                                code = NEED_UPDATE_CODE;
                                message = "需要更新";
                            } else {
                                code = NO_NEED_UPDATE_CODE;
                                message = "不需要更新";
                            }
                        } else {
                            code = -12;
                            message = "网络version不存在";
                        }

                    } else {
                        code = -11;
                        message = "json转化失败";
                    }
                } else {
                    code = -10;
                    message = "请求失败";
                }
                final String finalMessage = message;
                final int finalCode = code;
                if (code > 0) {
                    checkUpdateCallBack.OnSuccess(message, code);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkUpdateCallBack.OnSuccessNotifyUI(finalMessage, finalCode);
                        }
                    });
                } else {
                    checkUpdateCallBack.OnError(message, code);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkUpdateCallBack.OnErrorNotifyUI(finalMessage, finalCode);
                        }
                    });
                }

            }
        };
        new Thread(runnable).start();
    }


    /**
     * http://fir.im/docs/version_detection 查询fir.im线上版本
     *
     * @return
     */
    private static String checkUpdate() {
        String jsonStr = "";
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(checkUpdateUrlByID);
            Log.i(TAG, "checkUpdate: checkUpdateUrlByID:" + checkUpdateUrlByID);
            Log.i(TAG, "checkUpdate: checkUpdateUrlByPackageName:" + checkUpdateUrlByPackageName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            // User-Agent  IE9的标识
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            /**
             * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
             根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

             当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
             */
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
            //connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();//
            int contentLength = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();//会隐式调用connect()
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                jsonStr = baos.toString();
                Log.i(TAG, "checkUpdate: jsonStr:" + jsonStr);
            } else {
                Log.e(TAG, "checkUpdate: 请求失败 code:" + connection.getResponseCode());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonStr;
    }

    /**
     * {
     * "name": "XXX",
     * "version": "1",
     * "changelog": "xxx",
     * "updated_at": 1474164353,
     * "versionShort": "1.0",
     * "build": "1",
     * "installUrl": "http://download.fir.im/v2/app/install/xxxxxxxx",
     * "install_url": "http://download.fir.im/v2/app/install/xxxxxxxx",
     * "direct_install_url": "http://download.fir.im/v2/app/install/xxxxxxxx",
     * "update_url": "http://fir.im/xxxxxxxx",
     * "binary": {
     * "fsize": 9029591
     * }
     * }
     *
     * @param jsonStr
     * @throws JSONException
     */
    private static FirImBean parseFirImJson(String jsonStr) {
        FirImBean firImBean = new FirImBean();
        //json处理
        // JSONArray jsonArray=new JSONArray(jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String name = jsonObject.getString("name");//应用名称
            String version = jsonObject.getString("version");//版本
            String changelog = jsonObject.getString("changelog");//更新日志
            String updated_at = jsonObject.getString("updated_at");
            String versionShort = jsonObject.getString("versionShort"); //版本编号(兼容旧版字段)
            String build = jsonObject.getString("build");//编译号
            String installUrl = jsonObject.getString("installUrl");//安装地址（兼容旧版字段）
            String install_url = jsonObject.getString("install_url");//安装地址
            String direct_install_url = jsonObject.getString("direct_install_url");//
            String update_url = jsonObject.getString("update_url");//更新地址 http://fir.im/xxxxxxxx
            JSONObject binary = jsonObject.getJSONObject("binary");//更新文件的对象
            String fsize = binary.getString("fsize");//文件大小

            Log.i(TAG, "parseFirImJson:name:" + name);
            Log.i(TAG, "parseFirImJson:version:" + version);
            Log.i(TAG, "parseFirImJson:changelog:" + changelog);
            Log.i(TAG, "parseFirImJson:updated_at:" + updated_at);
            Log.i(TAG, "parseFirImJson:versionShort:" + versionShort);
            Log.i(TAG, "parseFirImJson:build:" + build);
            Log.i(TAG, "parseFirImJson:installUrl:" + installUrl);
            Log.i(TAG, "parseFirImJson:install_url:" + install_url);
            Log.i(TAG, "parseFirImJson:direct_install_url:" + direct_install_url);
            Log.i(TAG, "parseFirImJson:update_url:" + update_url);
            Log.i(TAG, "parseFirImJson:fsize:" + fsize);


            firImBean.setName(name);
            firImBean.setVersion(version);
            firImBean.setChangelog(changelog);
            firImBean.setUpdated_at(updated_at);
            firImBean.setVersionShort(versionShort);
            firImBean.setBuild(build);
            firImBean.setInstallUrl(installUrl);
            firImBean.setInstall_url(install_url);
            firImBean.setDirect_install_url(direct_install_url);
            firImBean.setUpdate_url(update_url);
            FirImBean.BinaryBean binaryBean = new FirImBean.BinaryBean();
            binaryBean.setFsize(fsize);
            firImBean.setBinary(binaryBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firImBean;
    }


    /**
     * 是否需要更新
     *
     * @param context
     * @param firVersionCode
     * @param firVersionName
     * @return
     */
    public static boolean isNeedUpdate(Context context, int firVersionCode, String firVersionName) {
        boolean needUpdate = false;
        PackageManager packageManager = context.getPackageManager();
        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                int currentVersionCode = packageInfo.versionCode;
                String currentVersionName = packageInfo.versionName;
                if (firVersionCode > currentVersionCode) {
                    //需要更新
                    Log.i("info", "need update app");
                    needUpdate = true;
                } else if (firVersionCode == currentVersionCode) {
                    //如果本地app的versionCode与FIR上的app的versionCode一致，则需要判断versionName.
                    if (!currentVersionName.equals(firVersionName)) {
                        Log.i("info", "need update app versionName is not the same");
                        needUpdate = true;
                    }
                } else {
                    //不需要更新,当前版本高于FIR上的app版本.
                    Log.i("info", " not need update app");
                    needUpdate = false;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return needUpdate;

    }


}
