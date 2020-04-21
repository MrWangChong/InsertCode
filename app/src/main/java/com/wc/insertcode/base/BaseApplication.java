package com.wc.insertcode.base;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import com.liulishuo.filedownloader.FileDownloader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.wc.insertcode.MainActivity;
import com.wc.qiniu.db.OrmliteDbHelper;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;


public class BaseApplication extends Application {
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(android.R.color.white, android.R.color.black);//全局设置主题颜色
                layout.setHeaderHeight(101);
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private static BaseApplication mApplication;
    private DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private DateFormat mFormatOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private DateFormat mOriginalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private DecimalFormat mDecimalFormat = new DecimalFormat("#,###.##");
    private DecimalFormat mTwoDecimalFormat = new DecimalFormat("###################.##");

    public synchronized static BaseApplication getInstance() {
        return mApplication;
    }

    private LinkedList<BaseActivity> mActivityList = new LinkedList<>();
    private List<DialogFragment> mDialogFragments = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        FileDownloader.setup(this);//初始化下载器
    }


    public String formatTime(long time) {
        try {
//            return mFormat.format(new Date(time));
            return mOriginalFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "--";
    }


    public String formatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "--";
        }
        try {
            return formatTime(mOriginalFormat.parse(time).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public String formatTimeOnly(String time) {
        if (TextUtils.isEmpty(time)) {
            return "--";
        }
        try {
            return mFormatOnly.format(new Date(mOriginalFormat.parse(time).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public String formatTimeOnly(long time) {
        try {
            return mFormatOnly.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "--";
    }

    public String formatDouble(double d) {
        return mDecimalFormat.format(d);
    }

    public String formatTwoDecimal(float f) {
        int a = (int) (f * 100 + 0.5);
        return mTwoDecimalFormat.format(a / 100f);
    }

    public String formatTwoDecimal(double f) {
        int a = (int) (f * 100 + 0.5);
        return mTwoDecimalFormat.format(a / 100f);
    }

    public synchronized void addActivity(BaseActivity activity) {
        mActivityList.add(activity);
    }

    public synchronized void removeActivity(BaseActivity activity) {
        mActivityList.remove(activity);
    }

    public synchronized void clearActivity() {
        for (BaseActivity activity : mActivityList) {
            activity.finish();
        }
        mActivityList.clear();
    }

    public synchronized void finishActivity() {
        for (BaseActivity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        mActivityList.clear();
    }

    public synchronized MainActivity getMainActivity() {
        for (BaseActivity activity : mActivityList) {
            if (activity instanceof MainActivity) {
                return (MainActivity) activity;
            }
        }
        return null;
    }

    public synchronized BaseActivity getTopActivity() {
        if (mActivityList.size() > 0) {
            return mActivityList.get(mActivityList.size() - 1);
        }
        return null;
    }

    public void addDialogFragment(DialogFragment dialogFragment) {
        mDialogFragments.add(dialogFragment);
    }

    public void removeDialogFragment(DialogFragment dialogFragment) {
        mDialogFragments.remove(dialogFragment);
    }

    public void closeAllDialogFragment() {
        if (mDialogFragments.size() > 0) {
            for (DialogFragment dialogFragment : new CopyOnWriteArrayList<>(mDialogFragments)) {
                dialogFragment.dismiss();
            }
        }
    }
}
