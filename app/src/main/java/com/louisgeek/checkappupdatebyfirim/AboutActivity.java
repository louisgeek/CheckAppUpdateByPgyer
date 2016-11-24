package com.louisgeek.checkappupdatebyfirim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.louisgeek.checkappupdatelib.helper.CheckUpdateHelper;

public class AboutActivity extends AppCompatActivity {
    private static String PGYER_APP_ID = "6d404c0ad932f325097c4465aea177bb";//应用App组ID
    private static String PGYER_API_KEY = "c4adcf888e612b58a588c5431cde0ae4";//api_key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Button idbtn = (Button) findViewById(R.id.id_btn);
        idbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *
                 */
                CheckUpdateHelper.checkUpdate(AboutActivity.this, new CheckUpdateHelper.CheckUpdateCallBack() {
                    @Override
                    public void backHasUpdate(boolean hasUpdate) {
                        if (!hasUpdate) {
                            Toast.makeText(AboutActivity.this, "当前已经是最新版本！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CheckUpdateHelper.unregisterCheckUpdate(this);
    }
}
