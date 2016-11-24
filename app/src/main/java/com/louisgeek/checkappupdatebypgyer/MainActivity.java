package com.louisgeek.checkappupdatebypgyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.louisgeek.checkappupdatelib.tool.SimpleCheckUpdateTool;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * update
         */
        SimpleCheckUpdateTool.updateSilent(MainActivity.this, Pgyer.APP_ID, Pgyer.API_KEY);


        findViewById(R.id.id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(in);
            }
        });
    }
}
