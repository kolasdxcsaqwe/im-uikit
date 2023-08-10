// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.chatkit.ui.fun.page;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.common.ChatUtils;
import com.netease.yunxin.kit.chatkit.ui.dialog.ChatBaseForwardSelectDialog;
import com.netease.yunxin.kit.chatkit.ui.fun.FunChatForwardSelectDialog;
import com.netease.yunxin.kit.chatkit.ui.fun.FunChoiceDialog;
import com.netease.yunxin.kit.chatkit.ui.fun.factory.FunPinViewHolderFactory;
import com.netease.yunxin.kit.chatkit.ui.model.ChatMessageBean;
import com.netease.yunxin.kit.chatkit.ui.page.ChatPinBaseActivity;
import com.netease.yunxin.kit.common.ui.dialog.BaseBottomChoiceDialog;
import com.netease.yunxin.kit.common.utils.SizeUtils;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

public class FunChatPinActivity extends ChatPinBaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    changeStatusBarColor(R.color.fun_chat_secondary_page_bg_color);
    viewBinding.getRoot().setBackgroundResource(R.color.fun_chat_secondary_page_bg_color);
    viewBinding.emptyIv.setImageResource(R.drawable.fun_ic_chat_empty);
  }

  @Override
  protected void initView() {
    super.initView();
    pinAdapter.setViewHolderFactory(new FunPinViewHolderFactory());
  }

  @Override
  public RecyclerView.ItemDecoration getItemDecoration() {
    return new RecyclerView.ItemDecoration() {
      final int topPadding = SizeUtils.dp2px(8);

      @Override
      public void getItemOffsets(
          @NonNull Rect outRect,
          @NonNull View view,
          @NonNull RecyclerView parent,
          @NonNull RecyclerView.State state) {
        outRect.set(0, topPadding, 0, 0);
      }
    };
  }

  @Override
  public BaseBottomChoiceDialog getMoreActionDialog(ChatMessageBean messageInfo) {
    return new FunChoiceDialog(this, assembleActions(messageInfo));
  }

  @Override
  public void jumpToChat(ChatMessageBean messageInfo) {
    String router = RouterConstant.PATH_FUN_CHAT_TEAM_PAGE;
    if (mSessionType == SessionTypeEnum.P2P) {
      router = RouterConstant.PATH_FUN_CHAT_P2P_PAGE;
    }

    XKitRouter.withKey(router)
        .withParam(RouterConstant.KEY_MESSAGE_BEAN, messageInfo)
        .withParam(RouterConstant.CHAT_KRY, mSessionId)
        .withContext(FunChatPinActivity.this)
        .navigate();
    finish();
  }

  @Override
  protected ChatBaseForwardSelectDialog getForwardSelectDialog() {
    ChatBaseForwardSelectDialog dialog = new FunChatForwardSelectDialog();
    dialog.setSelectedCallback(
        new ChatBaseForwardSelectDialog.ForwardTypeSelectedCallback() {
          @Override
          public void onTeamSelected() {
            ChatUtils.startTeamList(
                FunChatPinActivity.this, RouterConstant.PATH_FUN_MY_TEAM_PAGE, forwardTeamLauncher);
          }

          @Override
          public void onP2PSelected() {
            ChatUtils.startP2PSelector(
                FunChatPinActivity.this,
                RouterConstant.PATH_FUN_CONTACT_SELECTOR_PAGE,
                mSessionId,
                forwardP2PLauncher);
          }
        });
    return dialog;
  }
}
