package com.wc.insertcode.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.wc.insertcode.R;


public class MainLayout extends FrameLayout {
    private boolean isCanRequestLayout;
    private boolean hasNetwork = true;
    private View mNoNetworkView;
    private OnRefreshClickListener mOnClickListener;

    public MainLayout(@NonNull Context context) {
        super(context);
    }

    public MainLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnRefreshClickListener(OnRefreshClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        isCanRequestLayout = true;
    }

    public void setCanRequestLayout(boolean b) {
        isCanRequestLayout = b;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    public void setNetwork(boolean hasNetwork) {
        if (this.hasNetwork == hasNetwork) {
            return;
        }
        this.hasNetwork = hasNetwork;
        if (hasNetwork) {
            if (mNoNetworkView != null) {
                mNoNetworkView.setVisibility(View.GONE);
            }
        } else {
            if (mNoNetworkView == null) {
                mNoNetworkView = inflate(getContext(), R.layout.network_layout, null);
                addView(mNoNetworkView);
                mNoNetworkView.findViewById(R.id.tv_refresh).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnClickListener != null) {
                            mOnClickListener.onRefreshClick();
                        }
                    }
                });
            } else {
                mNoNetworkView.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnRefreshClickListener{
        void onRefreshClick();
    }
}
