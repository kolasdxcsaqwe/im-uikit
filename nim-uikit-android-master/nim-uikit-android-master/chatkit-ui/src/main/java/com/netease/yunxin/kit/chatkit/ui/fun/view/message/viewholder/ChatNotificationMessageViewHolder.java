// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.fun.view.message.viewholder;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachmentWithExtension;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.common.TeamNotificationHelper;
import com.netease.yunxin.kit.chatkit.ui.databinding.ChatBaseMessageViewHolderBinding;
import com.netease.yunxin.kit.chatkit.ui.databinding.FunChatMessageTextViewHolderBinding;
import com.netease.yunxin.kit.chatkit.ui.model.ChatMessageBean;
import com.netease.yunxin.kit.chatkit.ui.view.input.ActionConstants;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import java.util.List;

/** view holder for Text message */
public class ChatNotificationMessageViewHolder extends FunChatBaseMessageViewHolder {

  private static final String LOG_TAG = "ChatNotificationMessageViewHolder";

  FunChatMessageTextViewHolderBinding textBinding;

  public ChatNotificationMessageViewHolder(
      @NonNull ChatBaseMessageViewHolderBinding parent, int viewType) {
    super(parent, viewType);
  }

  @Override
  public void addViewToMessageContainer() {
    textBinding =
        FunChatMessageTextViewHolderBinding.inflate(
            LayoutInflater.from(parent.getContext()), getMessageContainer(), true);
  }

  @Override
  public void bindData(ChatMessageBean message, ChatMessageBean lastMessage) {
    super.bindData(message, lastMessage);
    loadData(message, lastMessage, true);
  }

  @Override
  public void bindData(ChatMessageBean message, int position, @NonNull List<?> payload) {
    super.bindData(message, position, payload);
    for (int i = 0; i < payload.size(); ++i) {
      String payloadItem = payload.get(i).toString();
      if (TextUtils.equals(payloadItem, ActionConstants.PAYLOAD_USERINFO)) {
        loadData(message, null, false);
      }
    }
  }

  @Override
  protected void onLayoutConfig(ChatMessageBean messageBean) {
    ConstraintLayout.LayoutParams messageContainerLayoutParams =
        (ConstraintLayout.LayoutParams) baseViewBinding.messageContainer.getLayoutParams();
    ConstraintLayout.LayoutParams messageTopLayoutParams =
        (ConstraintLayout.LayoutParams) baseViewBinding.messageTopGroup.getLayoutParams();
    ConstraintLayout.LayoutParams messageBottomLayoutParams =
        (ConstraintLayout.LayoutParams) baseViewBinding.messageBottomGroup.getLayoutParams();
    messageContainerLayoutParams.horizontalBias = 0.5f;
    messageTopLayoutParams.horizontalBias = 0.5f;
    messageBottomLayoutParams.horizontalBias = 0.5f;
  }

  @Override
  protected void onCommonViewVisibleConfig(ChatMessageBean messageBean) {
    super.onCommonViewVisibleConfig(messageBean);
    baseViewBinding.otherUsername.setVisibility(View.GONE);
  }

  private void loadData(ChatMessageBean message, ChatMessageBean lastMessage, boolean refreshTime) {
    if (message.getMessageData().getMessage().getAttachment()
        instanceof NotificationAttachmentWithExtension) {
      textBinding.messageText.setGravity(Gravity.CENTER);
      textBinding.messageText.setTextColor(
          IMKitClient.getApplicationContext().getResources().getColor(R.color.color_999999));
      textBinding.messageText.setTextSize(12);
      String content = TeamNotificationHelper.getTeamNotificationText(message.getMessageData());
      textBinding.messageText.setText(content);
      if (TextUtils.isEmpty(content)) {
        baseViewBinding.baseRoot.setVisibility(View.GONE);
      } else {
        baseViewBinding.baseRoot.setVisibility(View.VISIBLE);
        if (lastMessage == null && refreshTime) {
          setTime(message, null);
        }
      }
    } else {
      baseViewBinding.baseRoot.setVisibility(View.GONE);
    }
  }
}
