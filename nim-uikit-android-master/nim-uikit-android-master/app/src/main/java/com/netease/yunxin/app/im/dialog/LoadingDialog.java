package com.netease.yunxin.app.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.yunxin.app.im.R;

public class LoadingDialog extends Dialog {



    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.loadingDialog);
        setContentView(R.layout.dialog_loadinng);
        setCanceledOnTouchOutside(false);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    private LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



}
