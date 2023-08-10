package com.netease.yunxin.app.im.login;

import static javax.xml.transform.OutputKeys.MEDIA_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.ActivityLoginBinding;
import com.netease.yunxin.app.im.databinding.ActivityRegisterBinding;
import com.netease.yunxin.app.im.main.MainActivity;
import com.netease.yunxin.app.im.utils.DataUtils;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.login.LoginCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends FragmentActivity {

    ActivityLoginBinding alb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alb= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(alb.getRoot());
        alb.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        alb.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alb.etLoginName.getText().length()==0)
                {
                    ToastX.showShortToast("请输入用户名");
                    return;
                }
                if(alb.etPassword.getText().length()==0)
                {
                    ToastX.showShortToast("请输入密码");
                    return;
                }

                login(alb.etLoginName.getText().toString(),alb.etPassword.getText().toString());
            }
        });
    }


    private void login()
    {

    }

    public static void okHttpPost(){


    }

    private void login(String userName,String password)
    {
        RequestBody requestBody= new FormBody.Builder().add("userName",userName).add("loginPwd",password).build();

        HttpRequest.post(HttpRequest.login, requestBody, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response!=null)
                {
                    String json=response.body().string();
                    Log.e("login",json);
                    LoginIMResultBean loginIMResultBean=new Gson().fromJson(json,LoginIMResultBean.class);
                    loginIM(loginIMResultBean.getAccid(),loginIMResultBean.getImToken());
                }
            }
        });
    }


    private void loginIM(String account, String token) {
        LoginInfo loginInfo =
                LoginInfo.LoginInfoBuilder.loginInfoDefault(account, token)
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
