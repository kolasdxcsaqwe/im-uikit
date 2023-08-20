// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.app.im.welcome;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.app.im.AppSkinConfig;
import com.netease.yunxin.app.im.BaseActivity;
import com.netease.yunxin.app.im.BuildConfig;
import com.netease.yunxin.app.im.IMApplication;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.ActivityWelcomeBinding;
import com.netease.yunxin.app.im.login.LoginActivity;
import com.netease.yunxin.app.im.main.MainActivity;
import com.netease.yunxin.app.im.utils.Constant;
import com.netease.yunxin.app.im.utils.DataUtils;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.app.im.utils.OkhttpCallBack;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.login.LoginCallback;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Welcome Page is launch page
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";
    private ActivityWelcomeBinding activityWelcomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.d(Constant.PROJECT_TAG, TAG, "onCreateView");
        IMApplication.setColdStart(true);
        activityWelcomeBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(activityWelcomeBinding.getRoot());

        AppSkinConfig.getInstance().setAppSkinStyle(AppSkinConfig.AppSkin.commonSkin);
        String loginData = SPUtils.getInstance().get(SPUtils.loginData, "");
        if (!TextUtils.isEmpty(loginData)) {
            LoginIMResultBean loginIMResultBean = new Gson().fromJson(loginData, LoginIMResultBean.class);
            login(loginIMResultBean);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void showMainActivityAndFinish() {
        ALog.d(Constant.PROJECT_TAG, TAG, "showMainActivityAndFinish");
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        finish();
    }


    private void showLoginView() {
        ALog.d(Constant.PROJECT_TAG, TAG, "showLoginView");
        activityWelcomeBinding.appDesc.setVisibility(View.GONE);
        activityWelcomeBinding.loginButton.setVisibility(View.VISIBLE);
        activityWelcomeBinding.appBottomIcon.setVisibility(View.GONE);
        activityWelcomeBinding.appBottomName.setVisibility(View.GONE);
        activityWelcomeBinding.tvEmailLogin.setVisibility(View.VISIBLE);
        activityWelcomeBinding.tvServerConfig.setVisibility(View.VISIBLE);
        activityWelcomeBinding.vEmailLine.setVisibility(View.VISIBLE);
        activityWelcomeBinding.loginButton.setOnClickListener(
                view -> {
                    startActivity(new Intent(this, LoginActivity.class));
                });
        activityWelcomeBinding.tvEmailLogin.setOnClickListener(
                view -> {
                    startActivity(new Intent(this, LoginActivity.class));
                });
        activityWelcomeBinding.tvServerConfig.setOnClickListener(
                view -> {
                    Intent intent = new Intent(WelcomeActivity.this, ServerActivity.class);
                    startActivity(intent);
                });
    }

    /**
     * launch login activity
     */
    private void launchLoginPage() {
        ALog.d(Constant.PROJECT_TAG, TAG, "launchLoginPage");

    }

    /**
     * when your own page login success, you should login IM SDK
     */
    private void loginIM(LoginIMResultBean loginIMResultBean) {
        ALog.d(Constant.PROJECT_TAG, TAG, "loginIM");
        activityWelcomeBinding.getRoot().setVisibility(View.GONE);
        LoginInfo loginInfo =
                LoginInfo.LoginInfoBuilder.loginInfoDefault(loginIMResultBean.getAccid(), loginIMResultBean.getImToken())
                        .withAppKey(DataUtils.readAppKey(this))
                        .build();
        IMKitClient.loginIM(
                loginInfo,
                new LoginCallback<LoginInfo>() {
                    @Override
                    public void onError(int errorCode, @NonNull String errorMsg) {
                        ToastX.showShortToast(
                                String.format(getResources().getString(R.string.login_fail), errorCode));
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onSuccess(@Nullable LoginInfo data) {
                        showMainActivityAndFinish();
                    }
                });
    }

    private void login(LoginIMResultBean loginIMResultBean) {
        RequestBody requestBody = new FormBody.Builder().add("userName", loginIMResultBean.getUsername()).add("loginPwd", loginIMResultBean.getPassword()).build();

        HttpRequest.post(HttpRequest.login, requestBody, new OkhttpCallBack(true, this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {

                LoginIMResultBean loginIMResultBean=new Gson().fromJson(jsonObject.toString(),LoginIMResultBean.class);
                if(loginIMResultBean.getCode().equals("0"))
                {
                    loginIM(loginIMResultBean);
                }
                else
                {
                    ToastX.showShortToast(msg);
                }
            }
        });
    }
}
