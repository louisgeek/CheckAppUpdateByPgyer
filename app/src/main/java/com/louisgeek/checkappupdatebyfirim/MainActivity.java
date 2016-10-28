package com.louisgeek.checkappupdatebyfirim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.louisgeek.checkappupdatelib.DownloadManagerCenter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public final static String ID_STR = "xxxxxxxxxxxxxxxxxxx";
    public final static String API_TOKEN = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static String packageName = "com.sunstar.xxxxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button idbtn = (Button) findViewById(R.id.id_btn);

        idbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManagerCenter.initApiConfig(ID_STR,API_TOKEN,packageName);
                DownloadManagerCenter.startDown(MainActivity.this,false);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时 解除绑定
        DownloadManagerCenter.unbindDownService();
    }
}
