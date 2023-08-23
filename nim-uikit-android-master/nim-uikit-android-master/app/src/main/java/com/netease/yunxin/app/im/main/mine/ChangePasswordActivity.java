package com.netease.yunxin.app.im.main.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.netease.yunxin.app.im.BaseActivity;
import com.netease.yunxin.app.im.IMApplication;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.ActivityChangePasswordBinding;
import com.netease.yunxin.app.im.databinding.ActivityLoginBinding;
import com.netease.yunxin.app.im.login.LoginActivity;
import com.netease.yunxin.app.im.main.MainActivity;
import com.netease.yunxin.app.im.main.mine.setting.SettingActivity;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.app.im.utils.OkhttpCallBack;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.corekit.im.IMKitClient;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChangePasswordActivity extends BaseActivity {

    ActivityChangePasswordBinding acpb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acpb= ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(acpb.getRoot());



        acpb.tvConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword()
    {
        if(acpb.etOldPassword.getText().length()==0)
        {
            ToastX.showShortToast(acpb.etOldPassword.getHint().toString());
            return;
        }

        if(acpb.etPassword.getText().length()==0)
        {
            ToastX.showShortToast(acpb.etPassword.getHint().toString());
            return;
        }

        if(acpb.etRepeatPassword.getText().length()==0)
        {
            ToastX.showShortToast(acpb.etRepeatPassword.getHint().toString());
            return;
        }

        if(!acpb.etPassword.getText().toString().equals(acpb.etRepeatPassword.getText().toString()))
        {
            ToastX.showShortToast("新密码与确认密码不一致请重新输入");
            return;
        }

        RequestBody requestBody= new FormBody.Builder().add("newPwd",acpb.etPassword.getText().toString()).add("oldPwd",acpb.etOldPassword.getText().toString()).build();

        HttpRequest.post(HttpRequest.updatePwd, requestBody, new OkhttpCallBack(true,this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {

                if(isSuccess)
                {
                    String loginData = SPUtils.getInstance().get(SPUtils.loginData, "");
                    if (!TextUtils.isEmpty(loginData)) {
                        LoginIMResultBean loginIMResultBean = new Gson().fromJson(loginData, LoginIMResultBean.class);
                        loginIMResultBean.setPassword(acpb.etPassword.getText().toString());

                        logout();
                    }
                }
                else
                {
                    ToastX.showShortToast(msg);
                }

            }
        });
    }

    private void logout()
    {
        HttpRequest.post(HttpRequest.logout, new FormBody.Builder().build(), new OkhttpCallBack(true,this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {
                if(isSuccess)
                {
                    SPUtils.getInstance().remove(SPUtils.loginData);
                    IMKitClient.logoutIM(
                            new com.netease.yunxin.kit.corekit.im.login.LoginCallback<Void>() {
                                @Override
                                public void onError(int errorCode, @NonNull String errorMsg) {
                                    Toast.makeText(
                                                    ChangePasswordActivity.this,
                                                    "error code is " + errorCode + ", message is " + errorMsg,
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }

                                @Override
                                public void onSuccess(@Nullable Void data) {
                                    if (getApplicationContext() instanceof IMApplication) {
                                        ((IMApplication) getApplicationContext())
                                                .clearActivity(ChangePasswordActivity.this);
                                    }
                                    Intent intent = new Intent();
                                    intent.setClass(ChangePasswordActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    ChangePasswordActivity.this.startActivity(intent);
                                    finish();
                                }
                            });
                }
            }
        });


    }
}
