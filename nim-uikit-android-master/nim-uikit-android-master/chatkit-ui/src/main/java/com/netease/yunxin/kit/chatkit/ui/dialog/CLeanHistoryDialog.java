package com.netease.yunxin.kit.chatkit.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.event.EventSubscribeService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.DeleteTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.ui.databinding.ChatMessageForwardConfirmLayoutBinding;
import com.netease.yunxin.kit.chatkit.ui.databinding.DialogChatHistoryCleanBinding;
import com.netease.yunxin.kit.chatkit.ui.model.DeleteChatHistory;
import com.netease.yunxin.kit.common.ui.dialog.BaseDialog;
import com.netease.yunxin.kit.common.utils.ScreenUtils;
import com.netease.yunxin.kit.corekit.event.BaseEvent;
import com.netease.yunxin.kit.corekit.event.EventCenter;

public class CLeanHistoryDialog extends BaseDialog {

    Friend friend;
    public CLeanHistoryDialog(Friend friend) {
        this.friend=friend;
    }

    DialogChatHistoryCleanBinding binding;

    @Nullable
    @Override
    protected View getRootView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        binding = DialogChatHistoryCleanBinding.inflate(layoutInflater, viewGroup, false);

        if(!TextUtils.isEmpty(friend.getAlias()))
        {
            binding.tvContent.setText(String.format("确定与%s清空历史记录吗?",friend.getAlias()));
        }
        else
        {
            NimUserInfo nimUserInfo= NIMClient.getService(UserService.class).getUserInfo(friend.getAccount());
            if(nimUserInfo!=null)
            {
                if(!TextUtils.isEmpty(nimUserInfo.getName()))
                {
                    binding.tvContent.setText(String.format("确定与%s清空历史记录吗?",nimUserInfo.getName()));
                }
                else
                {
                    binding.tvContent.setText(String.format("确定与%s清空历史记录吗?",nimUserInfo.getAccount()));
                }
            }
        }


        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NIMClient.getService(MsgService.class).clearChattingHistory(friend.getAccount(),SessionTypeEnum.P2P);
                DeleteChatHistory deleteChatHistory=new DeleteChatHistory();
                EventCenter.notifyEvent(deleteChatHistory);
                dismissAllowingStateLoss();

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int width=(int)(ScreenUtils.getDisplayWidth()*0.8f);
        binding.llMain.getLayoutParams().width=width;
    }
}
