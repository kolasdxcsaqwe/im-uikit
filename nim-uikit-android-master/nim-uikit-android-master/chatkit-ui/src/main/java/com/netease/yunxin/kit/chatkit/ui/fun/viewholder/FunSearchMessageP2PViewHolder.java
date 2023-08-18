package com.netease.yunxin.kit.chatkit.ui.fun.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.databinding.FunChatSearchViewHolderBinding;
import com.netease.yunxin.kit.chatkit.ui.model.ChatP2PSearchBean;
import com.netease.yunxin.kit.chatkit.ui.model.ChatSearchBean;
import com.netease.yunxin.kit.common.ui.utils.AvatarColor;
import com.netease.yunxin.kit.common.ui.utils.TimeFormatUtils;
import com.netease.yunxin.kit.common.ui.viewholder.BaseViewHolder;

public class FunSearchMessageP2PViewHolder extends BaseViewHolder<ChatP2PSearchBean> {

    private FunChatSearchViewHolderBinding viewBinding;

    public FunSearchMessageP2PViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public FunSearchMessageP2PViewHolder(@NonNull FunChatSearchViewHolderBinding viewBinding) {
        this(viewBinding.getRoot());
        this.viewBinding = viewBinding;
    }

    @Override
    public void onBindData(ChatP2PSearchBean data, int position) {
        if (data != null) {
            viewBinding.avatarView.setData(
                    data.getUserInfo().getAvatar(), data.getUserInfo().getName(), AvatarColor.avatarColor(data.getImMessage().getFromAccount()));

            viewBinding.nameTv.setText(data.getUserInfo().getName());

            viewBinding.messageTv.setText(
                    data.getImMessage().getContent());

            viewBinding.timeTv.setText(
                    TimeFormatUtils.formatMillisecond(viewBinding.getRoot().getContext(), data.getImMessage().getTime()));

            viewBinding.getRoot().setOnClickListener(v -> itemListener.onClick(v, data, position));
        }
    }
}

