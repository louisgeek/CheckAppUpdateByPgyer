package com.louisgeek.checkappupdatebypgyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.louisgeek.checkappupdatelib.bean.PgyerGroupBean;
import com.louisgeek.checkappupdatelib.helper.CheckUpdateHelper;

public class MainActivity extends AppCompatActivity {
    private static String PGYER_APP_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";//应用App组ID
    private static String PGYER_API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";//api_key


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckUpdateHelper.initFirst(PGYER_APP_ID, PGYER_API_KEY);
        CheckUpdateHelper.checkUpdateSilent(this, new CheckUpdateHelper.CheckUpdateSilentCallBack() {
            @Override
            public void backUpdateInfo(PgyerGroupBean pgyerGroupBean, String savedApkPath) {
                CheckUpdateHelper.showApkIsInstallDialog(MainActivity.this, pgyerGroupBean,savedApkPath);
            }
        });


        findViewById(R.id.id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(in);
            }
        });
    }
}
