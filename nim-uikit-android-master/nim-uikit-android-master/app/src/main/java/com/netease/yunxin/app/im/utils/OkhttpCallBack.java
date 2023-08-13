package com.netease.yunxin.app.im.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.netease.yunxin.app.im.BaseActivity;
import com.netease.yunxin.app.im.dialog.LoadingDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class OkhttpCallBack implements Callback {

    Activity context;
    Boolean isShow;

    public OkhttpCallBack(Boolean isShow,Activity context) {
        this.context=context;
        this.isShow=isShow;

        if(context instanceof BaseActivity)
        {
            ((BaseActivity)context).showLoading();
        }

        if(context instanceof com.netease.yunxin.kit.common.ui.activities.BaseActivity)
        {
            ((com.netease.yunxin.kit.common.ui.activities.BaseActivity)context).showLoading();
        }

    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        if(!context.isDestroyed() && !context.isFinishing())
        {
            if(context instanceof BaseActivity)
            {
                ((BaseActivity)context).dismissLoading();
            }

            if(context instanceof com.netease.yunxin.kit.common.ui.activities.BaseActivity)
            {
                ((com.netease.yunxin.kit.common.ui.activities.BaseActivity)context).dismissLoading();
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onHttpFailure(call,e);
                }
            });

        }
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if(!context.isDestroyed() && !context.isFinishing())
        {
            if(context instanceof BaseActivity)
            {
                ((BaseActivity)context).dismissLoading();
            }

            if(context instanceof com.netease.yunxin.kit.common.ui.activities.BaseActivity)
            {
                ((com.netease.yunxin.kit.common.ui.activities.BaseActivity)context).dismissLoading();
            }

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        onHttpResponse(call,response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }

    }

    public abstract void onHttpFailure(@NonNull Call call, @NonNull IOException e);
    public abstract void onHttpResponse(@NonNull Call call, @NonNull Response response) throws IOException;
}
