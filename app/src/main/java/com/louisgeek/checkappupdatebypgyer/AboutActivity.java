package com.louisgeek.checkappupdatebypgyer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.louisgeek.checkappupdatelib.tool.SimpleCheckUpdateTool;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Button idbtn = (Button) findViewById(R.id.id_btn);
        idbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * update
                 */
                SimpleCheckUpdateTool.updateNormal(AboutActivity.this, Pgyer.APP_ID, Pgyer.API_KEY);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimpleCheckUpdateTool.updateNormalUnregister(this);
    }
}
