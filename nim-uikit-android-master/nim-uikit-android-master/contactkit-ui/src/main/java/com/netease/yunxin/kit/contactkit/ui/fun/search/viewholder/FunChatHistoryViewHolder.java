package com.netease.yunxin.kit.contactkit.ui.fun.search.viewholder;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.netease.yunxin.kit.common.ui.utils.AvatarColor;
import com.netease.yunxin.kit.common.ui.viewholder.BaseViewHolder;
import com.netease.yunxin.kit.contactkit.ui.databinding.FunChatHistoryItemBinding;
import com.netease.yunxin.kit.contactkit.ui.model.P2PChatHistoryBean;

public class FunChatHistoryViewHolder extends BaseViewHolder<P2PChatHistoryBean> {

    FunChatHistoryItemBinding binding;

    public FunChatHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public FunChatHistoryViewHolder(@NonNull FunChatHistoryItemBinding viewBinding) {
        super(viewBinding);
        this.binding=viewBinding;
    }

    @Override
    public void onBindData(P2PChatHistoryBean p2PChatHistoryBean, int i) {
        binding.cavUserIcon.setData(
                p2PChatHistoryBean.getNimUserInfo().getAvatar(),
                p2PChatHistoryBean.getNimUserInfo().getName(),
                AvatarColor.avatarColor(p2PChatHistoryBean.getNimUserInfo().getAccount()));

        if(p2PChatHistoryBean.getFriend()!=null)
        {
            if(!TextUtils.isEmpty(p2PChatHistoryBean.getFriend().getAlias()))
            {
                binding.nickTv.setText(p2PChatHistoryBean.getFriend().getAlias());
            }
            else
            {
                binding.nickTv.setText(p2PChatHistoryBean.getNimUserInfo().getName());
            }
        }
        else
        {
            if(TextUtils.isEmpty(p2PChatHistoryBean.getNimUserInfo().getName()))
            {
                binding.nickTv.setText(p2PChatHistoryBean.getNimUserInfo().getAccount());
            }
            else
            {
                binding.nickTv.setText(p2PChatHistoryBean.getNimUserInfo().getName());
            }
        }

        binding.tvChat.setText(p2PChatHistoryBean.getImMessage().getContent());
        binding.getRoot().setOnClickListener(v -> itemListener.onClick(v, p2PChatHistoryBean, position));
    }
}
