package com.wc.insertcode.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.wc.insertcode.R;
import com.wc.insertcode.utils.NetworkUtils;
import com.wc.insertcode.widget.MainLayout;
import com.wc.utils.DisplayUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 澄清式状态栏基类Activity
 * Created by RushKing on 2018/3/21 0021.
 */

public abstract class BaseActivity extends AppCompatActivity {
    //    private DisplayUtils.NavigationBarContentObserver mObserver;
//    private View mNavigationBar;
    //    private DisplayUtil.NavigationBarContentObserver mObserver;
//    private View mNavigationBar;
    private boolean mIsShowing = false;
    private MainLayout mContentView;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && mContentView != null) {
            mContentView.setCanRequestLayout(false);
            setContentView(mContentView);
            return;
        }
        super.onCreate(savedInstanceState);
        BaseApplication.getInstance().addActivity(this);
        int layoutResID = getLayoutResID();
        View v = getContentView();
        if (layoutResID != 0 || v != null) {
            boolean isBlackStateTextColor = isBlackStateTextColor();
            if (!isBlackStateTextColor) {
                setWindowInfo(false);
            }
            //将window扩展至全屏，并且不会覆盖状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //避免在状态栏的显示状态发生变化时重新布局
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            if (layoutResID == 0) {
                mContentView = new MainLayout(this);
                mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mContentView.addView(v);
                setContentView(mContentView);
            } else {
                mContentView = new MainLayout(this);
                mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                View.inflate(this, layoutResID, mContentView);
                setContentView(mContentView);
            }
            if (isBlackStateTextColor) {
                setWindowInfo(true);
            }
//            final int navigationBarHeight = DisplayUtil.getNavigationBarHeight();
//            if (navigationBarHeight != 0) {
//                LinearLayout contentView = new LinearLayout(this);
//                contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                contentView.setOrientation(LinearLayout.VERTICAL);
//                mNavigationBar = new View(this);
//                mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
//                mNavigationBar.setBackground(new ColorDrawable(Color.BLACK));
//                View view = layoutResID == 0 ? v : View.inflate(this, layoutResID, null);
//                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
//                contentView.addView(view);
//                contentView.addView(mNavigationBar);
//                mObserver = new DisplayUtil.NavigationBarContentObserver(new Handler(Looper.getMainLooper()), this);
//                mObserver.setOnNavigationBarChangedListener(isShow -> {
//                    if (isShow) {
//                        mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
//                    } else {
//                        mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
//                    }
//                });
//                if (DisplayUtil.isNavigationBarShowing(this)) {
//                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
//                }
////                if (mObserver.isNavigationBarShowing()) {
////                    mNavigationBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, navigationBarHeight));
////                }
//                setContentView(contentView);
//            } else {
//                if (layoutResID == 0) {
//                    setContentView(v);
//                } else {
//                    setContentView(layoutResID);
//                }
//            }
        }
    }

    private void setWindowInfo(boolean isBlackStateTextColor) {
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.TRANSPARENT);
                int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                View v_state_bar = findViewById(R.id.status_view);
                if (v_state_bar != null) {
                    int statusHeight = DisplayUtils.getStatusHeight();
                    if (statusHeight > DisplayUtils.dip2px(24)) {
                        ViewGroup.LayoutParams params = v_state_bar.getLayoutParams();
                        params.height = statusHeight;
                        v_state_bar.setLayoutParams(params);
                    }
                    if (isBlackStateTextColor) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            v_state_bar.setVisibility(View.INVISIBLE);
                            visibility = visibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                        } else {
                            v_state_bar.setVisibility(View.VISIBLE);
                        }
                    }
                }
                window.getDecorView().setSystemUiVisibility(visibility);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initData();
        loadNetInfo();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
        loadNetInfo();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        initData();
        loadNetInfo();
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsShowing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsShowing = false;
    }

    protected void initView() {

    }

    protected void initData() {
    }

    private synchronized void loadNetInfo() {
        if (isNeedLoadData()) {
            if (!NetworkUtils.isNetAvailable(this)) {
                getMainLayout().setNetwork(false);
            } else {
                getMainLayout().setNetwork(true);
                getData(false, false);
            }
            getMainLayout().setOnRefreshClickListener(new MainLayout.OnRefreshClickListener() {
                @Override
                public void onRefreshClick() {
                    loadNetInfo();
                }
            });
        }
    }

    protected void getData(final boolean isRefresh, final boolean isLoadMore) {

    }

    protected View getContentView() {
        return null;
    }

    protected int getLayoutResID() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().removeActivity(this);
//        if (mObserver != null) {
//            mObserver.destroy();
//        }
    }

    public void backActivity() {
        View view = findViewById(R.id.default_title_back);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelInputMethod();
                    finish();
                }
            });
        }
    }

    protected synchronized void cancelInputMethod() {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (mInputMethodManager != null && mInputMethodManager.isActive()) {
            View focus = getCurrentFocus();
            if (focus != null) {
                focus.setFocusable(true);
                focus.setFocusableInTouchMode(true);
                focus.requestFocus();
                mInputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    //是否设置黑色状态栏字体
    public boolean isBlackStateTextColor() {
        return false;
    }

    public boolean isNeedLoadData() {
        return false;
    }

    /**
     * 设置标题名字
     *
     * @param strId 资源ID
     */
    public void setTitleName(int strId) {
        TextView title = findViewById(R.id.default_title_text);
        if (title != null) {
            title.setText(strId);
        }
    }

    public void setTitleName(String s) {
        TextView title = findViewById(R.id.default_title_text);
        if (title != null) {
            title.setText(s);
        }
    }

    public MainLayout getMainLayout() {
        return mContentView;
    }

    /**
     * 跳转界面，带动画
     *
     * @param cls 需要跳转进入的Class
     * @param map 传递的参数目前只支持String,Serializable,Integer,Double,Boolean
     */
    public void jumpActivity(Class cls, Map<String, Object> map) {
        Intent intent = new Intent(this, cls);
        if (map != null) {
            for (String name : map.keySet()) {
                Object obj = map.get(name);
                if (obj instanceof String) {
                    intent.putExtra(name, (String) obj);
                } else if (obj instanceof Integer) {
                    intent.putExtra(name, (Integer) obj);
                } else if (obj instanceof Double) {
                    intent.putExtra(name, (Double) obj);
                } else if (obj instanceof Boolean) {
                    intent.putExtra(name, (Boolean) obj);
                } else if (obj instanceof Serializable) {
                    intent.putExtra(name, (Serializable) obj);
                }
            }
        }
        startActivity(intent);
    }

    public void jumpActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void jumpActivity(Class cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    public void onChange(boolean b) {

    }
}
