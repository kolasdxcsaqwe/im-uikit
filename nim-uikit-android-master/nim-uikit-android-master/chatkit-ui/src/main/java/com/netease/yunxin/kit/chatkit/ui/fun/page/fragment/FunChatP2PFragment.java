// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.fun.page.fragment;

import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.LIB_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.event.EventSubscribeServiceObserver;
import com.netease.nimlib.sdk.event.model.Event;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.model.IMMessageInfo;
import com.netease.yunxin.kit.chatkit.model.IMMessageReceiptInfo;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.common.ChatUserCache;
import com.netease.yunxin.kit.chatkit.ui.fun.view.MessageBottomLayout;
import com.netease.yunxin.kit.chatkit.ui.model.ChatMessageBean;
import com.netease.yunxin.kit.chatkit.ui.model.CloseChatPageEvent;
import com.netease.yunxin.kit.chatkit.ui.model.DeleteChatHistory;
import com.netease.yunxin.kit.chatkit.ui.page.viewmodel.ChatP2PViewModel;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.corekit.event.BaseEvent;
import com.netease.yunxin.kit.corekit.event.EventCenter;
import com.netease.yunxin.kit.corekit.event.EventNotify;
import com.netease.yunxin.kit.corekit.im.model.FriendInfo;
import com.netease.yunxin.kit.corekit.im.model.UserInfo;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

import java.util.List;

public class FunChatP2PFragment extends FunChatFragment {
  private static final String TAG = "ChatP2PFunFragment";

  private static final int TYPE_DELAY_TIME = 3000;

  public UserInfo userInfo;

  public FriendInfo friendInfo;

  public IMMessage anchorMessage;

  private final Handler handler = new Handler();

  private final Runnable stopTypingRunnable = () -> chatView.setTypeState(false);

  protected Observer<IMMessageReceiptInfo> p2pReceiptObserver;

  protected final EventNotify<DeleteChatHistory> closeEventNotify =
          new EventNotify<DeleteChatHistory>() {
            @Override
            public void onNotify(@NonNull DeleteChatHistory message) {
              if(chatView!=null)
              {
                chatView.clearMessageList();
              }
            }

            @NonNull
            @Override
            public String getEventType() {
              return DeleteChatHistory.EVENT_TYPE;
            }
          };

  @Override
  protected void initData(Bundle bundle) {

    ALog.d(LIB_TAG, TAG, "initData");
    sessionType = SessionTypeEnum.P2P;
    userInfo = (UserInfo) bundle.getSerializable(RouterConstant.CHAT_KRY);
    sessionID = (String) bundle.getSerializable(RouterConstant.CHAT_ID_KRY);
    if (userInfo == null && TextUtils.isEmpty(sessionID)) {
      requireActivity().finish();
      return;
    }
    if (TextUtils.isEmpty(sessionID)) {
      sessionID = userInfo.getAccount();
    }
    anchorMessage = (IMMessage) bundle.getSerializable(RouterConstant.KEY_MESSAGE);
    refreshView();
    EventCenter.registerEventNotify(closeEventNotify);


    NIMClient.getService(EventSubscribeServiceObserver.class).observeEventChanged(new com.netease.nimlib.sdk.Observer<List<Event>>() {
      @Override
      public void onEvent(List<Event> result) {
        // 处理
        if (result != null) {
          StringBuilder stringBuilder=new StringBuilder();
          for (int i = 0; i < result.size(); i++) {
            if(result.get(i).getPublisherAccount().equals(userInfo.getAccount()) && chatView!=null)
            {
              int type=result.get(i).getEventValue();
              String name = sessionID;
              if (friendInfo != null) {
                name = friendInfo.getName();
              } else if (userInfo != null) {
                name = userInfo.getName();
              }

              StringBuilder sb=new StringBuilder();
              sb.append(name).append("(");
              switch (type)
              {
                case 1://在线
                  sb.append("在线").append(")");
                  chatView.getTitleBar().setTitle(sb.toString());
                  break;
                case 2://离线
                  sb.append("离线").append(")");
                  chatView.getTitleBar().setTitle(sb.toString());
                  break;
                case 3://断开连接
                  sb.append("连接断开").append(")");
                  chatView.getTitleBar().setTitle(sb.toString());
                  break;
              }
            }
          }
        }
      }
    }, true);
  }

  @Override
  protected void initView() {
    super.initView();
    chatView
        .getTitleBar()
        .setOnBackIconClickListener(v -> requireActivity().onBackPressed())
        .setActionImg(R.drawable.ic_more_point)
        .setActionListener(
            v -> {
              chatView.hideCurrentInput();
              XKitRouter.withKey(RouterConstant.PATH_FUN_CHAT_SETTING_PAGE)
                  .withParam(RouterConstant.CHAT_ID_KRY, sessionID)
                  .withContext(requireActivity())
                  .navigate();
            });
  }

  public void refreshView() {
    String name = sessionID;
    if (friendInfo != null) {
      name = friendInfo.getName();
    } else if (userInfo != null) {
      name = userInfo.getName();
    }
    chatView.getTitleBar().setTitle(name);
  }

  @Override
  protected void initViewModel() {
    ALog.d(LIB_TAG, TAG, "initViewModel");
    viewModel = new ViewModelProvider(this).get(ChatP2PViewModel.class);
    viewModel.init(sessionID, SessionTypeEnum.P2P);

    if (chatConfig != null && chatConfig.chatListener != null) {
      chatConfig.chatListener.onSessionChange(sessionID, sessionType);
    }
    if (chatConfig != null && chatConfig.messageProperties != null) {
      viewModel.setShowReadStatus(chatConfig.messageProperties.showP2pMessageStatus);
    }
  }

  @Override
  protected void initToFetchData() {
    if (viewModel instanceof ChatP2PViewModel) {
      ((ChatP2PViewModel) viewModel).getFriendInfo(sessionID);
      viewModel.initFetch(anchorMessage, false);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ((ChatP2PViewModel) viewModel).getMessageReceiptLiveData().removeObserver(p2pReceiptObserver);
  }

  @Override
  protected void initDataObserver() {
    super.initDataObserver();
    ALog.d(LIB_TAG, TAG, "initDataObserver");
    p2pReceiptObserver =
        imMessageReceiptInfo ->
            chatView
                .getMessageListView()
                .setP2PReceipt(imMessageReceiptInfo.getMessageReceipt().getTime());
    ((ChatP2PViewModel) viewModel).getMessageReceiptLiveData().observeForever(p2pReceiptObserver);

    ((ChatP2PViewModel) viewModel)
        .getTypeStateLiveData()
        .observe(
            getViewLifecycleOwner(),
            isTyping -> {
              handler.removeCallbacks(stopTypingRunnable);
              chatView.setTypeState(isTyping);
              if (isTyping) {
                handler.postDelayed(stopTypingRunnable, TYPE_DELAY_TIME);
              }
            });
    ((ChatP2PViewModel) viewModel)
        .getFriendInfoLiveData()
        .observe(
            getViewLifecycleOwner(),
            result -> {
              if (result.getLoadStatus() == LoadStatus.Success) {
                friendInfo = result.getData();
                if (friendInfo != null) {
                  userInfo = friendInfo.getUserInfo();
                }
                refreshView();
              }
            });
  }

  @Override
  public void onNewIntent(Intent intent) {
    ALog.d(LIB_TAG, TAG, "onNewIntent");
    anchorMessage = (IMMessage) intent.getSerializableExtra(RouterConstant.KEY_MESSAGE);
    ChatMessageBean anchorMessageBean =
        (ChatMessageBean) intent.getSerializableExtra(RouterConstant.KEY_MESSAGE_BEAN);
    if (anchorMessageBean != null) {
      anchorMessage = anchorMessageBean.getMessageData().getMessage();
    } else if (anchorMessage != null) {
      anchorMessageBean = new ChatMessageBean(new IMMessageInfo(anchorMessage));
    }
    if (anchorMessage != null) {
      int position = chatView.getMessageListView().searchMessagePosition(anchorMessage.getUuid());
      if (position >= 0) {
        chatView
            .getMessageListView()
            .getViewTreeObserver()
            .addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                  @Override
                  public void onGlobalLayout() {
                    chatView
                        .getMessageListView()
                        .getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
                    chatView
                        .getRootView()
                        .post(() -> chatView.getMessageListView().scrollToPosition(position));
                  }
                });
        chatView.getMessageListView().scrollToPosition(position);
      } else {
        chatView.clearMessageList();
        // need to add anchor message to list panel
        chatView.appendMessage(anchorMessageBean);
        viewModel.initFetch(anchorMessage, false);
      }
    }
  }

  public MessageBottomLayout getMessageBottomLayout() {
    return viewBinding.chatView.getInputView();
  }

  @Override
  public void updateCurrentUserInfo() {
    UserInfo userInfo = ChatUserCache.getUserInfo(sessionID);
    if (userInfo != null) {
      this.userInfo = userInfo;
    }

    FriendInfo friendInfo = ChatUserCache.getFriendInfo(sessionID);
    if (friendInfo != null) {
      this.friendInfo = friendInfo;
    }

    refreshView();
  }
}
