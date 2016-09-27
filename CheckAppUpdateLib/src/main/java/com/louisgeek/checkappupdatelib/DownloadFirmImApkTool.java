package com.louisgeek.checkappupdatebyfirim;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.louisgeek.checkappupdatelib.callback.DownloadApkCallBack;
import com.louisgeek.checkappupdatelib.callback.GetDownloadTokenCallBack;
import com.louisgeek.checkappupdatelib.CheckUpdateTool;
import com.louisgeek.checkappupdatelib.tool.ThreadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by louisgeek on 2016/9/18.
 */
public class DownloadFirmImApkTool {

    public static final int GET_DOWNLOAD_TOKEN_SUCCESS_CODE = 20;
    public static final int DOWNLOAD_APK_SUCCESS_CODE = 30;

    private static final String TAG = "DownloadApkTool";
    private static String baseDownloadTokenUrl = "http://api.fir.im/apps/%s/download_token?api_token=%s";
    private static String downloadTokenUrl = String.format(baseDownloadTokenUrl, CheckUpdateTool.ID_STR, CheckUpdateTool.API_TOKEN);

    /**
     * http://fir.im/docs/install  第一步  接口
     *
     * @return
     */
    private static String getDownloadTokenJson() {
        String jsonStr = "";
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(downloadTokenUrl);
            Log.i(TAG, "getDownloadTokenJson: downloadTokenUrl:" + downloadTokenUrl);
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
                Log.i(TAG, "getDownloadTokenJson: jsonStr:" + jsonStr);
            } else {
                Log.e(TAG, "getDownloadTokenJson: 请求失败 code:" + connection.getResponseCode());
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
     * http://fir.im/docs/install  第二步 POST 接口
     * POST 返回提供下载的apk直链  url
     *
     * @param pathurl
     * @param paramsStr
     * @return
     */
    public static String postBackDownloadApkPathJson(String pathurl, String paramsStr) {
        String jsonStr = "";
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(pathurl);
            Log.i(TAG, "postBackDownloadApkPathJson: pathurl:" + pathurl);
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
                Log.i(TAG, "postBackDownloadApkPathJson: jsonStr:" + jsonStr);
            } else {
                Log.e(TAG, "postBackDownloadApkPathJson: 请求失败 code:" + connection.getResponseCode());
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

    // 获得存储卡的路径  返回外部存储空间路径（可能是sd卡，也可能是内部存储器）
    private static String sd_path = Environment.getExternalStorageDirectory() + File.separator;
    private static String sd_path_2_way = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static String sd_path_3_way = Environment.getExternalStoragePublicDirectory("")+ File.separator;
    private static String filePath =String.format("%sMyFileDir%sTest%s",sd_path,File.separator,File.separator);
    private static String saveFileAllName = filePath + "app.apk";

    /**
     * get  直接下载   http://fir.im/docs/install  第二步 GET 接口
     *
     * @param pathurl
     */
    private static String getDownloadApk(String pathurl) {

        /**
         getDownloadApk: sd_path:/storage/emulated/0/
         getDownloadApk: sd_path_2_way:/storage/emulated/0/
         getDownloadApk: sd_path_3_way:/storage/emulated/0/
         getDownloadApk: filePath:/storage/emulated/0/MyFileDir/Test/
         getDownloadApk: saveFileAllName:/storage/emulated/0/MyFileDir/Test/app.apk
         */
        Log.i(TAG, "getDownloadApk: sd_path:"+sd_path);
        Log.i(TAG, "getDownloadApk: sd_path_2_way:"+sd_path_2_way);
        Log.i(TAG, "getDownloadApk: sd_path_3_way:"+sd_path_3_way);
        Log.i(TAG, "getDownloadApk: filePath:"+filePath);
        Log.i(TAG, "getDownloadApk: saveFileAllName:"+saveFileAllName);

        String apkPath = "";
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(pathurl);
            Log.i(TAG, "getDownloadApk: pathurl:" + pathurl);
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

                File file_dir  = new File(filePath);
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
                apkPath = myFile.getAbsolutePath();
                Log.i(TAG, "getDownloadApk: apkPath:" + apkPath);
            } else {
                Log.e(TAG, "getDownloadApk: 请求失败 code:" + connection.getResponseCode());
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
        return apkPath;
    }

    //============================
    public static void doGetDownloadToken(final GetDownloadTokenCallBack getDownloadTokenCallBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int code = -1;
                String message = "";

                /**
                 * {
                 "download_token": "7ba9ab27170b31d4ce222ba64cfd2233"
                 }
                 */
                String json = getDownloadTokenJson();
                Log.i(TAG, "doDownloadApkMain: json:" + json);
                if (json != null && !json.equals("")) {
                    try {
                        JSONObject jSONObject = new JSONObject(json);
                        String download_token = jSONObject.getString("download_token");
                        Log.i(TAG, "doDownloadApkMain: download_token:" + download_token);

                        if (download_token != null && !download_token.equals("")) {
                            code = GET_DOWNLOAD_TOKEN_SUCCESS_CODE;
                            message = download_token;
                        } else {
                            code = -22;
                            message = "网络download_token不存在";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        code = -21;
                        message = "json转化失败";
                    }
                } else {
                    code = -20;
                    message = "请求失败";
                }

                final int finalCode = code;
                final String finalMessage = message;
                if (code > 0) {
                    getDownloadTokenCallBack.OnSuccess(message, code);

                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDownloadTokenCallBack.OnSuccessNotifyUI(finalMessage, finalCode);
                        }
                    });
                } else {
                    getDownloadTokenCallBack.OnError(message, code);
                    ThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDownloadTokenCallBack.OnErrorNotifyUI(finalMessage, finalCode);
                        }
                    });
                }

            }
        };
        new Thread(runnable).start();

    }

    public static DownloadApkCallBack mDownloadApkCallBack;
    public static void doDownloadApk(final String pathurl, final DownloadApkCallBack downloadApkCallBack) {
        mDownloadApkCallBack = downloadApkCallBack;
        //
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int code = -1;
                String message = "";

                String apkPath = getDownloadApk(pathurl);

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

    public static void installApk(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void uninstallApk(Context context) {
        Uri uri = Uri.parse("package:com.xxx.xxx");
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

}
