package com.louisgeek.checkappupdatelib.tool;

import android.os.Environment;
import android.util.Log;

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
 * Created by louisgeek on 2016/11/23.
 */

public class HttpTool {
    private static final String TAG = "HttpTool";


    public static void postUrlBackString(final String webUrl, final String paramsStr, final OnUrlBackStringCallBack onUrlBackStringCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //
                postUrlBackStr(webUrl, paramsStr, onUrlBackStringCallBack);
                //
            }
        }).start();
    }

    /**
     * @param webUrl
     * @param paramsStr
     * @return
     */
    private static void postUrlBackStr(String webUrl, String paramsStr, OnUrlBackStringCallBack onUrlBackStringCallBack) {
        boolean isSuccess = false;
        String message;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
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
                String backStr = baos.toString();
                Log.i(TAG, "backStr:" + backStr);

                message = backStr;
                isSuccess = true;
            } else {
                // Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (MalformedURLException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            message = e.getMessage();
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
                message = e.getMessage();
                e.printStackTrace();
            }
        }
        if (isSuccess) {
            onUrlBackStringCallBack.onSuccess(message);
        } else {
            onUrlBackStringCallBack.onError(message);
        }
    }

    public static void getUrlBackString(final String webUrl, final OnUrlBackStringCallBack onUrlBackStringCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //
                getUrlBackStr(webUrl, onUrlBackStringCallBack);
                //
            }
        }).start();
    }

    private static void getUrlBackStr(String webUrl, OnUrlBackStringCallBack onUrlBackStringCallBack) {
        boolean isSuccess = false;
        String message;

        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
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
            //connection.setDoOutput(true);//Android  4.0 GET时候 用这句会变成POST  报错java.io.FileNotFoundException
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
                String backStr = baos.toString();
                Log.i(TAG, " backStr:" + backStr);

                message = backStr;
                isSuccess = true;
            } else {
                //Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (MalformedURLException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            message = e.getMessage();
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
                message = e.getMessage();
                e.printStackTrace();
            }
        }
        if (isSuccess) {
            onUrlBackStringCallBack.onSuccess(message);
        } else {
            onUrlBackStringCallBack.onError(message);
        }
    }

    /**
     * private static String sd_path = Environment.getExternalStorageDirectory() + File.separator;
     * private static String filePath = String.format("%sMyFileDir%sTest%s", sd_path, File.separator, File.separator);
     * private static String saveFileAllName = filePath + "app.apk";
     * <p>
     * getDownloadFile: sd_path:/storage/emulated/0/
     * getDownloadFile: filePath:/storage/emulated/0/MyFileDir/Test/
     * getDownloadFile: saveFileAllName:/storage/emulated/0/MyFileDir/Test/app.apk
     */
    public static void getUrlDownloadFile(String webUrl, OnUrlDownloadFileCallBack onUrlDownloadFileCallBack) {
        String sd_path = Environment.getExternalStorageDirectory() + File.separator;
        String filePath = String.format("%sMyFileDir%sDownFile%s", sd_path, File.separator, File.separator);
        String fileNameWithExt = "app.apk";
        getUrlDownloadFile(webUrl, filePath, fileNameWithExt, onUrlDownloadFileCallBack);
    }

    public static void getUrlDownloadFile(final String webUrl, final String filePath, final String fileNameWithExt, final OnUrlDownloadFileCallBack onUrlDownloadFileCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ///////////
                int mPogressTemp = 0;
                boolean isSuccess = false;
                String message;

                if (!SdCardTool.hasSDCardMounted()) {
                    //Log.e(TAG, "getDownloadFile: 无SD卡");
                    message = "无SD卡";
                    if (onUrlDownloadFileCallBack != null) {
                        onUrlDownloadFileCallBack.onError(message);
                    }
                    return;
                }
                String saveFileAllPath = filePath + fileNameWithExt;
                //
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    URL url = new URL(webUrl);
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

                        File myFile = new File(saveFileAllPath);
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
                            int progressInt=(int) progress;

                            //更新不要太频繁  进度会卡慢
                            if (progress != mPogressTemp) {
                                mPogressTemp=progressInt;
                                /**
                                 *
                                 */
                                onUrlDownloadFileCallBack.OnProgress(progressInt);
                            }


                        }
                        //下载完成
                        String savedFilePath = myFile.getAbsolutePath();
                        Log.i(TAG, "getDownloadFile: savedFilePath:" + savedFilePath);
                        isSuccess = true;
                        message = savedFilePath;
                    } else {
                        //Log.e(TAG, "getDownloadFile: 请求失败 code:" + connection.getResponseCode());
                        message = "请求失败code:" + connection.getResponseCode();
                    }


                } catch (MalformedURLException e) {
                    message = e.getMessage();
                    e.printStackTrace();
                } catch (IOException e) {
                    message = e.getMessage();
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
                        message = e.getMessage();
                        e.printStackTrace();
                    }
                }
                if (isSuccess) {
                    onUrlDownloadFileCallBack.onSuccess(message);
                } else {
                    onUrlDownloadFileCallBack.onError(message);
                }

                /////////////////

            }
        }).start();
    }

    public interface OnUrlDownloadFileCallBack {
        void onSuccess(String savedFilePath);

        void onError(String errorMsg);

        void OnProgress(int progress);
    }

    public interface OnUrlBackStringCallBack {
        void onSuccess(String backStr);

        void onError(String errorMsg);
    }
}
