package com.louisgeek.checkappupdatelib;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by louisgeek on 2016/8/19.
 */
public class DownloadFileTool {


    // 获得存储卡的路径
    private static String sd_path = Environment.getExternalStorageDirectory() + "/";
    private static String filePath = sd_path + "MyFileDir/Test/";
    private static String saveFileAllName = filePath + "QQ.exe";

    public static void doDownloadThread() {
        new Thread(new DownloadFileThread()).start();
    }


    private static class DownloadFileThread implements Runnable {
        @Override
        public void run() {
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            //
            try {
                URL url = new URL("http://dldir1.qq.com/qqfile/qq/QQ8.6/18781/QQ8.6.exe");
                //获取连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                //打开连接
                connection.connect();
                //获取内容长度
                int contentLength = connection.getContentLength();


                File file = new File(filePath);
                // 判断文件目录是否存在
                if (!file.exists()) {
                    file.mkdir();
                }
                //file.mkdirs();

                //输入流
                inputStream = connection.getInputStream();

                File myFile = new File(saveFileAllName);
                //输出流
                fileOutputStream = new FileOutputStream(myFile);

                byte[] bytes = new byte[1024];
                // int  index=0;
                long totalReaded = 0;
                int temp_Len;
                while ((temp_Len = inputStream.read(bytes)) != -1) {
                    // bytes[index]= (byte) temp_Len;
                    // index++;
                    totalReaded += temp_Len;
                    Log.i("XXXX", "run: totalReaded:" + totalReaded);
                    long progress = totalReaded * 100 / contentLength;
                    Log.i("XXXX", "run: progress:" + progress);
                    fileOutputStream.write(bytes, 0, temp_Len);

                }
              /*  byte[] bytes = new byte[1024];
                while (inputStream.read(bytes)!=-1){
                    fileOutputStream.write(bytes);
                }*/

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
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

        }
    }

}