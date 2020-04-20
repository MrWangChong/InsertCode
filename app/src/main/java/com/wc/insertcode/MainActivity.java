package com.wc.insertcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wc.qiniu.FileListUtil;
import com.wc.qiniu.Tools;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TextView tv_text;
    //文件服务地址+key
    private static final String BASE_URL = "http://q8x9t7cfr.bkt.clouddn.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_text = findViewById(R.id.tv_text);
//        QiniuService.init(this);
//        QiniuService.startService(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileListUtil.getFileList(new FileListUtil.OnFileFindCallback() {
                    @Override
                    public void onFinish(final List<FileListUtil.FileBean> files) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (files != null) {
                                    tv_text.append("共\t" + files.size() + "\t条数据");
                                    for (FileListUtil.FileBean file : files) {
                                        tv_text.append(file.key + "\t" + Tools.formatSize(file.fsize));
                                        tv_text.append("\n");
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
