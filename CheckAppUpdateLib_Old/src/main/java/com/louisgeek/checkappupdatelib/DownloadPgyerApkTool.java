package com.louisgeek.checkappupdatelib;

import android.os.Environment;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.louisgeek.checkappupdatelib.bean.PgyerBeanBase;
import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.bean.PgyerVersionBean;
import com.louisgeek.checkappupdatelib.callback.DownloadApkCallBack;
import com.louisgeek.checkappupdatelib.tool.ThreadUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.louisgeek.checkappupdatelib.DownloadFirmImApkTool.DOWNLOAD_APK_SUCCESS_CODE;
import static com.louisgeek.checkappupdatelib.DownloadFirmImApkTool.mDownloadApkCallBack;

/**
 * Created by louisgeek on 2016/11/23.
 */

public class DownloadPgyerApkTool {
    private static final String TAG = "DownloadPgyerApkTool";

    private static String app_id = "d1e91f34eda39834a4d5903ed4f7c899";//应用App组ID
    private static String api_key = "c4adcf888e612b58a588c5431cde0ae4";

    @Deprecated //不知道是不是真的是倒序  用getGroupInfo 判断返回字段 appIsLastest		是否是最新版（1:是; 2:否）
    private static String getAllVersion = "http://www.pgyer.com/apiv1/app/builds";//POST
    private static String getAllVersionParamStr = String.format("aId=%s&_api_key=%s&page=%s", app_id, api_key, "22");
    private static String getGroupInfo = "http://www.pgyer.com/apiv1/app/viewGroup";//POST
    private static String getGroupInfoParamStr = String.format("aId=%s&_api_key=%s", app_id, api_key);//POST

    /**
     * 来自  https://www.pgyer.com/doc/api#installApp 文档
     * 为了适应Android和IOS  会重定向到itms-services://?action=download-manifest&url=http://www.pgyer.com/app/plist/{aKey}
     * 不能直接下载到apk
     */
    @Deprecated
    private static String getAppInstall = "http://www.pgyer.com/apiv1/app/install";//GET
    /*
    //demo http://www.pgyer.com/apiv1/app/install?_api_key=c4adcf888e612b58a588c5431cde0ae4&aKey=f3c7897cf8ac70a3d345699ef6e1584d
        @Deprecated
       private static String getAppInstallUrl=String.format("%s?aKey=%s&_api_key=%s",getAppInstall,appKey,api_key);
    */
    /**
     * 来自应用内API选项卡  like https://www.pgyer.com/manager/api/index/d1e91f34eda39834a4d5903ed4f7c899
     * 区别是 这个api用aId （app组id） 上面用的是 aKey （app 唯一标识key） 为啥要分两个呢。。。搞不懂
     */
    private static String getAppInstallSimple = "http://www.pgyer.com/apiv1/app/install";//GET
    //demo  https://www.pgyer.com/apiv1/app/install?aId=d1e91f34eda39834a4d5903ed4f7c899&_api_key=c4adcf888e612b58a588c5431cde0ae4
    //or  http://www.pgyer.com/apiv1/app/install?aId=d1e91f34eda39834a4d5903ed4f7c899&_api_key=c4adcf888e612b58a588c5431cde0ae4
    public static String getAppInstallSimpleUrl = String.format("%s?aId=%s&_api_key=%s", getAppInstallSimple, app_id, api_key);

    @Deprecated
    public static String getAllVersionJson() {
        String jsonStr = postUrlBackStr(getAllVersion, getAllVersionParamStr);
        TypeToken<PgyerBeanBase<PgyerVersionBean>> typeToken = new TypeToken<PgyerBeanBase<PgyerVersionBean>>() {
        };
        PgyerBeanBase<PgyerVersionBean> pgyerBeanBase = PgyerBeanBase.fromJson(jsonStr, typeToken);
        Log.d(TAG, "getAllVersionJson: pgyerBeanBase" + pgyerBeanBase.getData().getList().size());
        return jsonStr;
    }

    public interface GroupInfoCallBack {
        void groupInfo(PgyerBeanBase<PgyerGroupBean> pgyerBeanBasePgyerGroupBean);
    }

    public static void getGroupInfo(final GroupInfoCallBack groupInfoCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String jsonStr = postUrlBackStr(getGroupInfo, getGroupInfoParamStr);
                TypeToken<PgyerBeanBase<PgyerGroupBean>> typeToken = new TypeToken<PgyerBeanBase<PgyerGroupBean>>() {
                };
                final PgyerBeanBase<PgyerGroupBean> pgyerBeanBase = PgyerBeanBase.fromJson(jsonStr, typeToken);
                //Log.d(TAG, "getAllVersionJson: pgyerBeanBase" + pgyerBeanBase.getData().getDataBean().size());
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (groupInfoCallBack != null) {
                            groupInfoCallBack.groupInfo(pgyerBeanBase);
                        }
                    }
                });

            }
        }).start();

    }



    /**
     * @param pathurl
     * @param paramsStr
     * @return
     */
    private static String postUrlBackStr(String pathurl, String paramsStr) {
        String jsonStr = "";
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(pathurl);
            Log.i(TAG, "getAllVersionJson: pathurl:" + pathurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("POST");
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
            connection.setDoOutput(true);
            connection.setUseCaches(false);


            /**
             * set  params one way   OutputStream
             */
         /*   byte[] bytesParams = paramsStr.getBytes();
            // 发送请求params参数
            connection.getOutputStream().write(bytesParams);*/

            /**
             * set  params two way  PrintWriter
             */
           /* PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            //PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            // 发送请求params参数
            printWriter.write(paramsStr);
            printWriter.flush();*/

            /**
             * set  params three way  OutputStreamWriter
             */
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), "UTF-8");
            // 发送请求params参数
            out.write(paramsStr);
            out.flush();


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
                Log.i(TAG, "getAllVersionJson: jsonStr:" + jsonStr);

            } else {
                Log.e(TAG, "getAllVersionJson: 请求失败 code:" + connection.getResponseCode());
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

    @Deprecated
    private static String getUrlBackStr(String pathUrl) {
        String jsonStr = "";
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(pathUrl);
            Log.i(TAG, "getAllVersionJson: getAllVersion:" + getAllVersion);
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
                Log.i(TAG, "getAllVersionJson: jsonStr:" + jsonStr);
            } else {
                Log.e(TAG, "getAllVersionJson: 请求失败 code:" + connection.getResponseCode());
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

    private static String sd_path = Environment.getExternalStorageDirectory() + File.separator;
    private static String filePath = String.format("%sMyFileDir%sTest%s", sd_path, File.separator, File.separator);
    private static String saveFileAllName = filePath + "app.apk";

    private static String getDownloadFile(String pathurl) {

        /**
         getDownloadFile: sd_path:/storage/emulated/0/
         getDownloadFile: filePath:/storage/emulated/0/MyFileDir/Test/
         getDownloadFile: saveFileAllName:/storage/emulated/0/MyFileDir/Test/app.apk
         */

        String filePath = "";
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(pathurl);
            Log.i(TAG, "getDownloadFile: pathurl:" + pathurl);
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

                File file_dir = new File(filePath);
                // 判断文件目录是否存在
                if (!file_dir.exists()) {
                    file_dir.mkdirs();
                }
                //file.mkdir();//只能生成单层目录

                inputStream = connection.getInputStream();//会隐式调用connect()

                File myFile = new File(saveFileAllName);
                /*if (!myFile.exists()){
                    myFile.mkdir();
                }*/
                //输出流
                fileOutputStream = new FileOutputStream(myFile);

                long totalReaded = 0;
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    //
                    totalReaded += readLen;
                    // Log.i("XXXX", "totalReaded:" + totalReaded);
                    final long progress = totalReaded * 100 / contentLength;
                    // Log.i("XXXX", "progress:" + progress);
                    //
                    fileOutputStream.write(bytes, 0, readLen);

                    //
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadApkCallBack.OnProgressNotifyUI((int) progress);
                        }
                    });

                }
                //下载完成
                filePath = myFile.getAbsolutePath();
                Log.i(TAG, "getDownloadFile: filePath:" + filePath);
            } else {
                Log.e(TAG, "getDownloadFile: 请求失败 code:" + connection.getResponseCode());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
    public static void doDownloadApk(final String pathurl, final DownloadApkCallBack downloadApkCallBack) {
        mDownloadApkCallBack = downloadApkCallBack;
        //
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int code = -1;
                String message = "";

                String apkPath = getDownloadFile(pathurl);

                if (apkPath != null && !apkPath.equals("")) {
                    code = DOWNLOAD_APK_SUCCESS_CODE;
                    message = apkPath;
                } else {
                    code = -30;
                    message = "请求失败";
                }

                final String finalMessage = message;
                final int finalCode = code;
                if (code > 0) {
                    downloadApkCallBack.OnSuccess(message, code);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadApkCallBack.OnSuccessNotifyUI(finalMessage, finalCode);
                        }
                    });
                } else {
                    downloadApkCallBack.OnError(message, code);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downloadApkCallBack.OnErrorNotifyUI(finalMessage, finalCode);
                        }
                    });
                }

            }
        };
        new Thread(runnable).start();

    }
}
