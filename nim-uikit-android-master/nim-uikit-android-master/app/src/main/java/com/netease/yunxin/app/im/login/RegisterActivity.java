package com.netease.yunxin.app.im.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.app.im.BaseActivity;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.ActivityRegisterBinding;
import com.netease.yunxin.app.im.databinding.ActivityWelcomeBinding;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    ActivityRegisterBinding arb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arb= ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(arb.getRoot());
        arb.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arb.etLoginName.getText().length()==0)
                {
                    ToastX.showShortToast("请输入用户名");
                    return;
                }
                if(arb.etPassword.getText().length()==0)
                {
                    ToastX.showShortToast("请输入密码");
                    return;
                }

                register(arb.etLoginName.getText().toString(),arb.etPassword.getText().toString());
            }
        });

        arb.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });


    }


    private void register(String userName,String password)
    {
        RequestBody requestBody= new FormBody.Builder().add("userName",userName).add("loginPwd",password).build();

        HttpRequest.post(HttpRequest.register, requestBody, new OkhttpCallBack(true,this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response!=null)
                {
                    String json=response.body().string();
                    LoginIMResultBean loginIMResultBean=new Gson().fromJson(json,LoginIMResultBean.class);
                    loginIMResultBean.setPassword(password);
                    loginIMResultBean.setUsername(userName);
                    loginIM(loginIMResultBean);
                }
            }
        });
    }


    private void loginIM(LoginIMResultBean loginIMResultBean) {
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
                    }

                    @Override
                    public void onSuccess(@Nullable LoginInfo data) {
                        SPUtils.getInstance().save(SPUtils.loginData,new Gson().toJson(loginIMResultBean));
                        showMainActivityAndFinish();
                    }
                });
    }

    private void showMainActivityAndFinish() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        finish();
    }
}
