package com.wc.insertcode.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.victor.loading.rotate.RotateLoading;
import com.wc.insertcode.R;


/**
 * 网络加载加载弹出框
 * Created by RushKing on 2018/3/21 0021.
 */
public class LoadingDialog {
    private static Dialog sDialog;

    /**
     * 显示网络加载中弹出框
     *
     * @param context 上下文
     */
    public static void showLoadingDialog(Context context) {
        if (context == null || (sDialog != null && sDialog.isShowing())) {
            return;
        }
        dismissLoadingDialog();
        View contentView = View.inflate(context, R.layout.loading_mian_layout, null);
        RotateLoading loading = contentView.findViewById(R.id.rotate_loading);
        loading.start();
        sDialog = new AlertDialog.Builder(context).setView(contentView).create();
        sDialog.show();
    }

    /**
     * 取消网络加载弹出框
     */
    public static void dismissLoadingDialog() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
        sDialog = null;
    }
}
