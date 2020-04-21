package com.wc.insertcode;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.wc.insertcode.adapter.FileAdapter;
import com.wc.insertcode.base.BaseActivity;
import com.wc.insertcode.widget.LoadingDialog;
import com.wc.qiniu.FileListUtil;
import com.wc.widget.dialog.IosDialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends BaseActivity {
    private TextView tv_text, tv_delete;
    //文件服务地址+key
    public static final String BASE_URL = "http://q8x9t7cfr.bkt.clouddn.com/";
    private RecyclerView recycler_view;
    private FileAdapter mAdapter;
    private List<FileListUtil.FileBean> mData;

    private SmartRefreshLayout mRefreshLayout;

    @Override
    protected void initView() {
        tv_text = findViewById(R.id.tv_text);
        tv_delete = findViewById(R.id.tv_delete);
    }

    @Override
    protected void initData() {
        recycler_view = findViewById(R.id.recycler_view);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mData = new ArrayList<>();
        final int spanCount = 4;
        recycler_view.setLayoutManager(new GridLayoutManager(this, spanCount));
        mAdapter = new FileAdapter(this, mData);
        recycler_view.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override

            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(true, false);
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableAutoLoadMore(false);

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IosDialog.Builder(MainActivity.this).setTitle("提示").setMessage("确定删除？")
                        .setNegativeButton("取消", new IosDialog.OnClickListener() {
                            @Override
                            public void onClick(IosDialog dialog, View v) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("删除", new IosDialog.OnClickListener() {
                    @Override
                    public void onClick(IosDialog dialog, View v) {
                        dialog.dismiss();
                        LoadingDialog.showLoadingDialog(MainActivity.this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final List<FileListUtil.FileBean> tmpList = new ArrayList<>();
                                for (FileListUtil.FileBean fileBean : mData) {
                                    if (fileBean.isSelected) {
                                        tmpList.add(fileBean);
                                    }
                                }
                                String[] keyList = new String[tmpList.size()];
                                for (int i = 0; i < keyList.length; i++) {
                                    keyList[i] = tmpList.get(i).key;
                                }
                                FileListUtil.deleteFile(keyList, new FileListUtil.OnFileDeleteCallback() {
                                    @Override
                                    public void onDeleteSuccess(String key) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LoadingDialog.dismissLoadingDialog();
                                                mData.removeAll(tmpList);
                                                mAdapter.setSelecting(false);
                                                onChange(false);
                                            }
                                        });
                                    }
                                });
                            }
                        }).start();
                    }
                }).build().show();
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh, boolean isLoadMore) {
        super.getData(isRefresh, isLoadMore);
        LoadingDialog.showLoadingDialog(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileListUtil.getFileList(new FileListUtil.OnFileFindCallback() {
                    @Override
                    public void onFinish(final List<FileListUtil.FileBean> files) {
                        Collections.sort(files, new Comparator<FileListUtil.FileBean>() {
                            @Override
                            public int compare(FileListUtil.FileBean o1, FileListUtil.FileBean o2) {
                                return Double.compare(o2.putTime, o1.putTime);
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.dismissLoadingDialog();
                                mRefreshLayout.finishRefresh();
                                if (files != null) {
                                    tv_text.setText(MessageFormat.format("共\t{0}\t条数据", files.size()));
                                    mData.clear();
                                    mData.addAll(files);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public boolean isBlackStateTextColor() {
        return true;
    }

    @Override
    public boolean isNeedLoadData() {
        return true;
    }

    @Override
    public void onChange(boolean b) {
        tv_delete.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (tv_delete.getVisibility() == View.VISIBLE) {
            mAdapter.setSelecting(false);
            onChange(false);
        } else {
            super.onBackPressed();
        }
    }
}
