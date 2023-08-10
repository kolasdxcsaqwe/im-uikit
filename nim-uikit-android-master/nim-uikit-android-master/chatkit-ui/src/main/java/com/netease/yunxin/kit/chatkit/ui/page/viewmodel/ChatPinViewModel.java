// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.page.viewmodel;

import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.LIB_TAG;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgPinSyncResponseOption;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.model.IMMessageInfo;
import com.netease.yunxin.kit.chatkit.repo.ChatObserverRepo;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.common.MessageHelper;
import com.netease.yunxin.kit.chatkit.ui.model.ChatMessageBean;
import com.netease.yunxin.kit.chatkit.ui.model.PinEvent;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.common.ui.viewmodel.BaseViewModel;
import com.netease.yunxin.kit.common.ui.viewmodel.FetchResult;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.corekit.event.EventCenter;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import com.netease.yunxin.kit.corekit.im.repo.SettingRepo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatPinViewModel extends BaseViewModel {

  public static final String TAG = "ChatPinViewModel";
  protected String mSessionId;
  private SessionTypeEnum mSessionType;
  protected boolean needACK = false;
  protected boolean showRead = true;

  private final MutableLiveData<FetchResult<List<ChatMessageBean>>> messageLiveData =
      new MutableLiveData<>();
  private final FetchResult<List<ChatMessageBean>> messageFetchResult =
      new FetchResult<>(LoadStatus.Finish);

  private final MutableLiveData<FetchResult<String>> removePinLiveData = new MutableLiveData<>();
  private final FetchResult<String> removePinResult = new FetchResult<>(LoadStatus.Finish);

  private final MutableLiveData<FetchResult<List<ChatMessageBean>>> addLiveData =
      new MutableLiveData<>();
  private final FetchResult<List<ChatMessageBean>> addFetchResult =
      new FetchResult<>(LoadStatus.Finish);

  public void init(String sessionId, SessionTypeEnum sessionType) {
    this.mSessionId = sessionId;
    this.mSessionType = sessionType;
    this.needACK = SettingRepo.getShowReadStatus();
    ChatObserverRepo.registerAddMessagePinObserve(addPinObserver);
    ChatObserverRepo.registerRemoveMessagePinObserve(removePinObserver);
  }

  public void setShowRead(boolean showRead) {
    this.showRead = showRead;
  }

  public MutableLiveData<FetchResult<List<ChatMessageBean>>> getMessageLiveData() {
    return messageLiveData;
  }

  public MutableLiveData<FetchResult<String>> getRemovePinLiveData() {
    return removePinLiveData;
  }

  public MutableLiveData<FetchResult<List<ChatMessageBean>>> getAddPinLiveData() {
    return addLiveData;
  }

  public void fetchPinMsg() {

    ChatRepo.fetchPinMessage(
        mSessionId,
        mSessionType,
        new FetchCallback<List<IMMessageInfo>>() {
          @Override
          public void onSuccess(@Nullable List<IMMessageInfo> param) {
            messageFetchResult.setLoadStatus(LoadStatus.Success);
            messageFetchResult.setData(convert(param));
            messageLiveData.setValue(messageFetchResult);
          }

          @Override
          public void onFailed(int code) {}

          @Override
          public void onException(@Nullable Throwable exception) {}
        });
  }

  public void removePin(IMMessageInfo messageInfo) {
    ChatRepo.removeMessagePin(
        messageInfo.getMessage(),
        new FetchCallback<Long>() {
          @Override
          public void onSuccess(@Nullable Long param) {
            removePinResult.setLoadStatus(LoadStatus.Success);
            removePinResult.setData(messageInfo.getMessage().getUuid());
            removePinLiveData.setValue(removePinResult);
            EventCenter.notifyEvent(new PinEvent(messageInfo.getMessage().getUuid(), true));
            ToastX.showShortToast(R.string.chat_remove_tips);
          }

          @Override
          public void onFailed(int code) {
            if (code == ChatKitUIConstant.ERROR_CODE_NETWORK) {
              ToastX.showShortToast(R.string.chat_network_error_tips);
            }
            //            else {
            //              ToastX.showShortToast(R.string.chat_remove_pin_error_tips);
            //            }
          }

          @Override
          public void onException(@Nullable Throwable exception) {
            ToastX.showShortToast(R.string.chat_remove_pin_error_tips);
          }
        });
  }

  public void sendForwardMessage(IMMessage message, String sessionId, SessionTypeEnum sessionType) {
    ALog.d(LIB_TAG, TAG, "sendForwardMessage:" + sessionId);
    IMMessage forwardMessage = MessageBuilder.createForwardMessage(message, sessionId, sessionType);
    if (needACK && showRead) {
      message.setMsgAck();
    }
    MessageHelper.clearAitAndReplyInfo(forwardMessage);
    ChatRepo.sendMessage(forwardMessage, true, null);
  }

  private List<ChatMessageBean> convert(List<IMMessageInfo> messageList) {
    if (messageList == null) {
      return null;
    }
    Collections.sort(
        messageList,
        (o1, o2) -> {
          long time = o1.getMessage().getTime() - o2.getMessage().getTime();
          return time == 0L ? 0 : (time > 0 ? -1 : 1);
        });
    ArrayList<ChatMessageBean> result = new ArrayList<>(messageList.size());
    for (IMMessageInfo message : messageList) {
      result.add(new ChatMessageBean(message));
    }
    return result;
  }

  private final Observer<MsgPinSyncResponseOption> addPinObserver = this::fillPinMessage;

  private final Observer<MsgPinSyncResponseOption> removePinObserver =
      msgPinOption -> {
        if (msgPinOption != null && inSameSession(msgPinOption)) {
          ALog.d(
              LIB_TAG,
              TAG,
              "removePinObserver:"
                  + msgPinOption.getKey().getToAccount()
                  + "sessionID:"
                  + mSessionId);
          String uuid = msgPinOption.getKey().getUuid();
          if (!TextUtils.isEmpty(uuid)) {
            removePinResult.setData(uuid);
            removePinLiveData.setValue(removePinResult);
          }
        }
      };

  private void fillPinMessage(MsgPinSyncResponseOption option) {
    if (option != null && inSameSession(option)) {
      List<MsgPinSyncResponseOption> pinList = new ArrayList<>();
      pinList.add(option);
      ChatRepo.fillPinMessage(
          pinList,
          new FetchCallback<List<IMMessageInfo>>() {
            @Override
            public void onSuccess(@Nullable List<IMMessageInfo> param) {
              addFetchResult.setLoadStatus(LoadStatus.Success);
              addFetchResult.setData(convert(param));
              addLiveData.setValue(addFetchResult);
            }

            @Override
            public void onFailed(int code) {}

            @Override
            public void onException(@Nullable Throwable exception) {}
          });
    }
  }

  private boolean inSameSession(MsgPinSyncResponseOption option) {
    if (option == null
        || mSessionType == null
        || TextUtils.isEmpty(mSessionId)
        || mSessionType != option.getKey().getSessionType()) {
      return false;
    }
    return (SessionTypeEnum.P2P == option.getKey().getSessionType()
            && TextUtils.equals(mSessionId, option.getKey().getFromAccount())
            && TextUtils.equals(IMKitClient.account(), option.getKey().getToAccount()))
        || (SessionTypeEnum.P2P == option.getKey().getSessionType()
            && TextUtils.equals(mSessionId, option.getKey().getToAccount())
            && TextUtils.equals(IMKitClient.account(), option.getKey().getFromAccount()))
        || (SessionTypeEnum.Team == option.getKey().getSessionType()
            && TextUtils.equals(mSessionId, option.getKey().getToAccount()));
  }
}
