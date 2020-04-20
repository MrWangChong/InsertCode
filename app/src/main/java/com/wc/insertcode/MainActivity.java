package com.wc.insertcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wc.qiniu.QiniuService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiniuService.init(this);
        QiniuService.startService(this);
    }
}
