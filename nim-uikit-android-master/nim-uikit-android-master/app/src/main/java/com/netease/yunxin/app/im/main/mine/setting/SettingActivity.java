// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.app.im.main.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.netease.yunxin.app.im.AppSkinConfig;
import com.netease.yunxin.app.im.IMApplication;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.databinding.ActivityMineSettingBinding;
import com.netease.yunxin.app.im.login.LoginActivity;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.app.im.utils.OkhttpCallBack;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.chatkit.ui.custom.ChatConfigManager;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.utils.SizeUtils;
import com.netease.yunxin.kit.corekit.im.IMKitClient;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;

public class SettingActivity extends BaseActivity {

  private ActivityMineSettingBinding viewBinding;
  private SettingViewModel viewModel;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    changeStatusBarColor(R.color.color_e9eff5);
    super.onCreate(savedInstanceState);
    viewBinding = ActivityMineSettingBinding.inflate(getLayoutInflater());
    viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
    setContentView(viewBinding.getRoot());
    initView();
  }

  private void initView() {
    // delete alias

    // show read and unread status
    viewBinding.messageReadSc.setChecked(viewModel.getShowReadStatus());
    viewBinding.messageReadSc.setOnClickListener(
        v -> {
          boolean checked = viewBinding.messageReadSc.isChecked();
          viewModel.setShowReadStatus(checked);
          ChatConfigManager.showReadStatus = checked;
        });

    //audio play mode AUDIO_PLAY_EARPIECE or AUDIO_PLAY_OUTSIDE
    viewBinding.playModeSc.setChecked(viewModel.getAudioPlayMode());
    viewBinding.playModeSc.setOnClickListener(
        v -> {
          boolean checked = viewBinding.playModeSc.isChecked();
          viewModel.setAudioPlayMode(checked);
        });

    viewBinding.notifyFl.setOnClickListener(
        v -> startActivity(new Intent(SettingActivity.this, SettingNotifyActivity.class)));

    viewBinding.skinFl.setOnClickListener(
        v -> startActivity(new Intent(SettingActivity.this, SkinActivity.class)));

    viewBinding.clearFl.setOnClickListener(
        v -> startActivity(new Intent(SettingActivity.this, ClearCacheActivity.class)));

    viewBinding.tvLogout.setOnClickListener(
        v ->{
            // logout your own account here
            //...
            logout();
        });


    viewBinding.settingTitleBar.setOnBackIconClickListener(v -> onBackPressed());
    if (AppSkinConfig.getInstance().getAppSkinStyle() == AppSkinConfig.AppSkin.commonSkin) {
      changeStatusBarColor(R.color.fun_page_bg_color);
      viewBinding.clRoot.setBackgroundResource(R.color.fun_page_bg_color);
      viewBinding.nextGroupLl.setBackgroundResource(R.color.color_white);
      ViewGroup.MarginLayoutParams layoutParams =
          (ViewGroup.MarginLayoutParams) viewBinding.nextGroupLl.getLayoutParams();
      layoutParams.setMargins(0, 0, 0, 0);
      viewBinding.nextGroupLl.setLayoutParams(layoutParams);

      viewBinding.notifyMessageLl.setBackgroundResource(R.color.color_white);
      ViewGroup.MarginLayoutParams layoutParamsN =
          (ViewGroup.MarginLayoutParams) viewBinding.notifyMessageLl.getLayoutParams();
      layoutParamsN.setMargins(0, SizeUtils.dp2px(6), 0, 0);
      viewBinding.notifyMessageLl.setLayoutParams(layoutParamsN);

      viewBinding.tvLogout.setBackgroundResource(R.color.color_white);
      ViewGroup.MarginLayoutParams layoutParamsL =
          (ViewGroup.MarginLayoutParams) viewBinding.tvLogout.getLayoutParams();
      layoutParamsL.setMargins(0, SizeUtils.dp2px(6), 0, 0);
      viewBinding.tvLogout.setLayoutParams(layoutParamsL);

      updateCommonView(
          R.drawable.fun_setting_bg_switch_thumb_selector,
          R.drawable.fun_setting_bg_switch_track_selector);
    }
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
                                                        SettingActivity.this,
                                                        "error code is " + errorCode + ", message is " + errorMsg,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    @Override
                                    public void onSuccess(@Nullable Void data) {
                                        if (getApplicationContext() instanceof IMApplication) {
                                            ((IMApplication) getApplicationContext())
                                                    .clearActivity(SettingActivity.this);
                                        }
                                        SPUtils.getInstance().remove(SPUtils.loginData);
                                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                    }
          }
      });


  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private void updateCommonView(@DrawableRes int thumbRes, @DrawableRes int trackRes) {

    viewBinding.messageReadSc.setThumbResource(thumbRes);
    viewBinding.messageReadSc.setTrackResource(trackRes);

    viewBinding.playModeSc.setThumbResource(thumbRes);
    viewBinding.playModeSc.setTrackResource(trackRes);
  }
}
