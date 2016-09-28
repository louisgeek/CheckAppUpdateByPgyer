package com.louisgeek.checkappupdatebyfirim;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.louisgeek.checkappupdatelib.CheckUpdateTool;
import com.louisgeek.checkappupdatelib.DownloadFileService;
import com.louisgeek.checkappupdatelib.DownloadFirmImApkTool;
import com.louisgeek.checkappupdatelib.MyDialogFragmentProgress4Down;
import com.louisgeek.checkappupdatelib.NotificationMangerCenter;
import com.louisgeek.checkappupdatelib.bean.FirImBean;
import com.louisgeek.checkappupdatelib.callback.CheckUpdateCallBack;
import com.louisgeek.checkappupdatelib.callback.GetDownloadTokenCallBack;
import com.louisgeek.checkappupdatelib.tool.SdCardTool;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Context mContext;
    MyDialogFragmentProgress4Down myDialogFragmentProgress4Down;
    DownloadFileService mDownloadFileService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Button idbtn = (Button) findViewById(R.id.id_btn);
        idbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                CheckUpdateCallBack checkUpdateCallBack = new CheckUpdateCallBack() {
                    @Override
                    public void OnSuccess(String result, int statusCode,FirImBean firImBean) {
                        if (statusCode == CheckUpdateTool.NEED_UPDATE_CODE) {
                            //
                            MyDialogFragmentNormal myDialogFragmentNormal=MyDialogFragmentNormal.newInstance("有新的更新","最新版本："+firImBean.getVersionShort()+"\n\n"+"更新日志："+firImBean.getChangelog());
                            myDialogFragmentNormal.setOnBtnClickListener(new MyDialogFragmentNormal.OnBtnClickListener() {
                                @Override
                                public void onOkBtnClick(DialogInterface dialog) {
                                    doGetDownCode();
                                }

                                @Override
                                public void onCancelBtnClick(DialogInterface dialog) {
                                    //do nothing
                                }
                            });

                            myDialogFragmentNormal.show(getSupportFragmentManager(),"notice");
//

                        } else if (statusCode == CheckUpdateTool.NO_NEED_UPDATE_CODE) {
                            //do nothing
                        }
                    }

                    @Override
                    public void OnSuccessNotifyUI(String result, int statusCode,FirImBean firImBean) {
                        if (statusCode == CheckUpdateTool.NO_NEED_UPDATE_CODE) {
                            Toast.makeText(MainActivity.this, "不需要更新", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void OnError(String errorMsg, int statusCode) {
                        Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
                    }

                    @Override
                    public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                        Toast.makeText(MainActivity.this, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                };
                CheckUpdateTool.doCheckOnline(mContext, checkUpdateCallBack,false);
                //
            }
        });
    }

   private void doGetDownCode(){

         DownloadFirmImApkTool.doGetDownloadToken(new GetDownloadTokenCallBack() {
             @Override
             public void OnSuccess(String result, int statusCode) {
                 if (statusCode == DownloadFirmImApkTool.GET_DOWNLOAD_TOKEN_SUCCESS_CODE) {
                     //
                     String download_token = result;

                     Log.i(TAG, "OnSuccess: download_token:" + download_token);

                     String baseDownloadAPKUrlPath = "http://download.fir.im/apps/%s/install?download_token=%s";
                     String downloadAPKPathUrl = String.format(baseDownloadAPKUrlPath, CheckUpdateTool.ID_STR, download_token);

                     if (!SdCardTool.hasSDCardMounted()){
                         Log.e(TAG, "OnSuccess:has NO SDCard Mounted");
                         return;
                     }

                     doDownloadService(downloadAPKPathUrl);

                 }else{
                     Log.i(TAG, "OnSuccess: "+statusCode+result);
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
                 Toast.makeText(MainActivity.this, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
             }
         });
     }

    private void doDownloadService(String downloadAPKPathUrl) {
        //
        myDialogFragmentProgress4Down = MyDialogFragmentProgress4Down.newInstance(null);
        myDialogFragmentProgress4Down.show(getSupportFragmentManager(),"loading");
       //
        Intent intent=new Intent(MainActivity.this, DownloadFileService.class);
        intent.putExtra("downloadAPKPathUrl",downloadAPKPathUrl);
        //////////###startService(intent);
        bindService(intent,mServiceConnection,BIND_AUTO_CREATE);//BIND_AUTO_CREATE


        //
        NotificationMangerCenter.initNotification(MainActivity.this, R.drawable.icon120120,MainActivity.class);


    }
    int progressTemp=-1;
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DownloadFileService.DownloadFileBinder downloadFileBinder= (DownloadFileService.DownloadFileBinder) iBinder;
            mDownloadFileService=downloadFileBinder.getNowService();
            mDownloadFileService.setOnBackProgressListener(new DownloadFileService.OnBackProgressListener() {
                @Override
                public void onBackProgress(int progress) {
                    //
                    if (myDialogFragmentProgress4Down!=null) {
                        //更新太频繁  进度会卡慢
                        if (progress!=progressTemp){
                        myDialogFragmentProgress4Down.updateProgress(progress);
                        myDialogFragmentProgress4Down.setMessageText("新版本下载中，已完成:"+String.valueOf(progress)+"%");
                            progressTemp=progress;
                        }
                    }
                    //
                    NotificationMangerCenter.updateNotificationProgress(progress);
                }
            });
            mDownloadFileService.setOnDownLoadCompleteListener(new DownloadFileService.OnDownLoadCompleteListener() {
                @Override
                public void onDownLoadComplete(final String apkPath) {
                    //下载完解除绑定
                    unbindService(mServiceConnection);
                    //安装
                    DownloadFirmImApkTool.installApk(getApplicationContext(), apkPath);
                    //
                    if (myDialogFragmentProgress4Down!=null){
                        myDialogFragmentProgress4Down.setMessageText("新版本下载完成");
                        myDialogFragmentProgress4Down.finishDownload(new MyDialogFragmentProgress4Down.FinishDownloadListener() {
                            @Override
                            public void finishDown() {
                                //安装
                                DownloadFirmImApkTool.installApk(getApplicationContext(), apkPath);
                            }
                        });
                }}
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unbindService(mServiceConnection);
    }
}
