// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.fun.page;

import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.LIB_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.event.EventSubscribeService;
import com.netease.nimlib.sdk.event.EventSubscribeServiceObserver;
import com.netease.nimlib.sdk.event.model.Event;
import com.netease.nimlib.sdk.event.model.EventSubscribeRequest;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.fun.page.fragment.FunChatFragment;
import com.netease.yunxin.kit.chatkit.ui.fun.page.fragment.FunChatP2PFragment;
import com.netease.yunxin.kit.chatkit.ui.page.ChatBaseActivity;
import com.netease.yunxin.kit.corekit.im.model.UserInfo;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunChatP2PActivity extends ChatBaseActivity {

  private static final String TAG = "ChatP2PFunActivity";
  private FunChatFragment chatFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    changeStatusBarColor(R.color.fun_chat_page_bg_color);
  }

  @Override
  public void initChat() {
    UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra(RouterConstant.CHAT_KRY);
    String accId = getIntent().getStringExtra(RouterConstant.CHAT_ID_KRY);
    ALog.e(LIB_TAG, TAG, "initChat:" + accId);
    if (userInfo == null && TextUtils.isEmpty(accId)) {
      ALog.e(LIB_TAG, TAG, "user info is null && accid is null:");
      finish();
      return;
    }
    //set fragment
    chatFragment = new FunChatP2PFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(RouterConstant.CHAT_ID_KRY, accId);
    bundle.putSerializable(RouterConstant.CHAT_KRY, userInfo);
    IMMessage message = (IMMessage) getIntent().getSerializableExtra(RouterConstant.KEY_MESSAGE);
    if (message != null) {
      bundle.putSerializable(RouterConstant.KEY_MESSAGE, message);
    }
    chatFragment.setArguments(bundle);

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().add(R.id.container, chatFragment).commitAllowingStateLoss();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    ALog.e(LIB_TAG, TAG, "onNewIntent");
    chatFragment.onNewIntent(intent);
  }
}
