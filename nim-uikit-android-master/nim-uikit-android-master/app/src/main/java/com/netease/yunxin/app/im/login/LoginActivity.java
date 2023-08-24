package com.netease.yunxin.app.im.login;

import static javax.xml.transform.OutputKeys.MEDIA_TYPE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.yunxin.app.im.AppSkinConfig;
import com.netease.yunxin.app.im.BaseActivity;
import com.netease.yunxin.app.im.IMApplication;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.ActivityLoginBinding;
import com.netease.yunxin.app.im.databinding.ActivityRegisterBinding;
import com.netease.yunxin.app.im.dialog.DialogUpgradeVersionConfirm;
import com.netease.yunxin.app.im.dialog.LoadingDialog;
import com.netease.yunxin.app.im.main.MainActivity;
import com.netease.yunxin.app.im.utils.AppUtils;
import com.netease.yunxin.app.im.utils.DataUtils;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.app.im.utils.OkhttpCallBack;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.login.LoginCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding alb;
    long currentTime=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMApplication.setColdStart(true);
        alb= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(alb.getRoot());

        try {
            alb.version.setText(getString(R.string.mine_version)+":"+getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        alb.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.h_enter, R.anim.donothing);
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

        getVersion();

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentTime=0;
    }

    private void login()
    {

    }

    public static void okHttpPost(){


    }

    private void login(String userName,String password)
    {
        RequestBody requestBody= new FormBody.Builder().add("userName",userName).add("loginPwd",password).build();

        HttpRequest.post(HttpRequest.login, requestBody, new OkhttpCallBack(true,this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {
                LoginIMResultBean loginIMResultBean=new Gson().fromJson(jsonObject.toString(),LoginIMResultBean.class);
                if(loginIMResultBean.getCode().equals("0"))
                {
                    loginIMResultBean.setPassword(password);
                    loginIMResultBean.setUsername(userName);
                    loginIM(loginIMResultBean);
                }
                else
                {
                    ToastX.showShortToast(msg);
                    alb.rlSKV.setVisibility(View.GONE);
                }
            }
        });
    }


    private void loginIM(LoginIMResultBean bean) {
        alb.rlSKV.setVisibility(View.VISIBLE);
        LoginInfo loginInfo =
                LoginInfo.LoginInfoBuilder.loginInfoDefault(bean.getAccid(), bean.getImToken())
                        .withAppKey(DataUtils.readAppKey(this))
                        .build();
        IMKitClient.loginIM(
                loginInfo,
                new LoginCallback<LoginInfo>() {
                    @Override
                    public void onError(int errorCode, @NonNull String errorMsg) {
                        alb.rlSKV.setVisibility(View.GONE);
                        ToastX.showShortToast(
                                String.format(getResources().getString(R.string.login_fail), errorCode));

                    }

                    @Override
                    public void onSuccess(@Nullable LoginInfo data) {
                        SPUtils.getInstance().save(SPUtils.loginData,new Gson().toJson(bean));
                        showMainActivityAndFinish();
                    }
                });
    }

    private void showMainActivityAndFinish() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        overridePendingTransition(R.anim.h_exit, R.anim.donothing);
        finish();
    }

    @Override
    public void onBackPressed() {

       if(System.currentTimeMillis()-currentTime<2000)
       {
           super.onBackPressed();
       }
       else
       {
           ToastX.showShortToast("再按一次退出");
           currentTime=System.currentTimeMillis();
       }

    }


    private void getVersion()
    {
        HttpRequest.get(HttpRequest.systemVersion, new OkhttpCallBack(true,this) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {
                if(isSuccess)
                {
                    JSONObject data=jsonObject.optJSONObject("data");
                    JSONObject android=data.optJSONObject("android");
                    String version=android.optString("version","");
                    String url=android.optString("url","");

                    String onlineCode = version.replaceAll("\\.", "");
                    String NativeCode = AppUtils.getAppVersionName(LoginActivity.this).replaceAll("\\.", "");
                    try {
                        if (Integer.valueOf(onlineCode) > Integer.valueOf(NativeCode)) {
                            DialogUpgradeVersionConfirm.showDialog(getSupportFragmentManager()).setOnClickConfirmListener(new DialogUpgradeVersionConfirm.OnClickConfirmListener() {
                                @Override
                                public void onCLick(boolean isConfirm) {
                                    if(isConfirm)
                                    {
                                        AppUtils.skipWebpage(LoginActivity.this,url);
                                    }
                                    else
                                    {
                                        finish();
                                    }
                                }
                            });
                        }
                        else
                        {
                            AppSkinConfig.getInstance().setAppSkinStyle(AppSkinConfig.AppSkin.commonSkin);
                            String loginData = SPUtils.getInstance().get(SPUtils.loginData, "");
                            if (!TextUtils.isEmpty(loginData)) {
                                LoginIMResultBean loginIMResultBean = new Gson().fromJson(loginData, LoginIMResultBean.class);
                                login(loginIMResultBean.getUsername(),loginIMResultBean.getPassword());
                            }
                            else
                            {
                                alb.rlSKV.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                else
                {
                    ToastX.showShortToast(msg);
                }
            }
        });
    }

}
