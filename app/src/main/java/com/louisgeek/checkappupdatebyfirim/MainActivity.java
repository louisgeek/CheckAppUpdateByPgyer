package com.louisgeek.checkappupdatebyfirim;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.louisgeek.checkappupdatebyfirim.callback.CheckUpdateCallBack;
import com.louisgeek.checkappupdatebyfirim.callback.DownloadApkCallBack;
import com.louisgeek.checkappupdatebyfirim.callback.GetDownloadTokenCallBack;

public class MainActivity extends AppCompatActivity {

    MyDialogFragmentProgress myDialogFragmentProgress;
    private static final String TAG = "MainActivity";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Button idbtn = (Button) findViewById(R.id.id_btn);
        idbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUpdateCallBack checkUpdateCallBack = new CheckUpdateCallBack() {
                    @Override
                    public void OnSuccess(String result, int statusCode) {
                        if (statusCode == CheckUpdateTool.NEED_UPDATE_CODE) {
                            DownloadFirmImApkTool.doGetDownloadToken(new GetDownloadTokenCallBack() {
                                @Override
                                public void OnSuccess(String result, int statusCode) {
                                    if (statusCode == DownloadFirmImApkTool.GET_DOWNLOAD_TOKEN_SUCCESS_CODE) {
                                        String download_token = result;

                                        Log.i(TAG, "OnSuccess: download_token:" + download_token);

                                        myDialogFragmentProgress = MyDialogFragmentProgress.newInstance("下载更新");
                                        myDialogFragmentProgress.show(getSupportFragmentManager(), "loading");

                                        String baseDownloadAPKUrlPath = "http://download.fir.im/apps/%s/install?download_token=%s";
                                        String downloadAPKPathUrl = String.format(baseDownloadAPKUrlPath, CheckUpdateTool.ID_STR, download_token);

                                        DownloadFirmImApkTool.doDownloadApk(downloadAPKPathUrl, new DownloadApkCallBack() {
                                            @Override
                                            public void OnSuccess(String result, int statusCode) {

                                            }

                                            @Override
                                            public void OnSuccessNotifyUI(String result, int statusCode) {
                                                if (statusCode == DownloadFirmImApkTool.DOWNLOAD_APK_SUCCESS_CODE) {
                                                    //Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                                                    Log.i(TAG, "OnSuccessNotifyUI: 下载完成");
                                                    /*if (myDialogFragmentProgress != null) {
                                                        myDialogFragmentProgress.dismiss();
                                                    }*/
                                                    final String apkPath = result;
                                                    //安装
                                                    DownloadFirmImApkTool.installApk(mContext, apkPath);
                                                    myDialogFragmentProgress.setOnFinishClickListener(new MyDialogFragmentProgress.OnFinishClickListener() {
                                                        @Override
                                                        public void onFinishClick(View view) {
                                                            DownloadFirmImApkTool.installApk(mContext, apkPath);
                                                        }
                                                    });


                                                }
                                            }

                                            @Override
                                            public void OnError(String errorMsg, int statusCode) {
                                                Log.i(TAG, "OnError: " + statusCode + ":" + errorMsg);
                                            }

                                            @Override
                                            public void OnErrorNotifyUI(String errorMsg, int statusCode) {
                                                if (myDialogFragmentProgress != null) {
                                                    myDialogFragmentProgress.dismiss();
                                                }
                                                Toast.makeText(MainActivity.this, statusCode + ":" + errorMsg, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void OnProgressNotifyUI(int progress) {
                                                myDialogFragmentProgress.updateProgress(progress);
                                            }
                                        });


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
                        } else if (statusCode == CheckUpdateTool.NO_NEED_UPDATE_CODE) {
                            //do nothing
                        }
                    }

                    @Override
                    public void OnSuccessNotifyUI(String result, int statusCode) {
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
                CheckUpdateTool.doCheckOnline(mContext, checkUpdateCallBack);
            }
        });

    }
}
