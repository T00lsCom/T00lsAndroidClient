package com.t00ls.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.t00ls.R;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 2000);

    }
}
