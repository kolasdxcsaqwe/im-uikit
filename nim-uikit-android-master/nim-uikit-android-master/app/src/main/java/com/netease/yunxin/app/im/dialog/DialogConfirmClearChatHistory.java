package com.netease.yunxin.app.im.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.netease.yunxin.kit.common.ui.dialog.BaseDialog;
import com.netease.yunxin.kit.common.utils.ScreenUtils;

public class DialogConfirmClearChatHistory extends BaseDialog {


    com.netease.yunxin.app.im.databinding.DialogReConfirmBinding binding;
    DialogConfirmClearChatHistory.OnClickConfirmListener onClickConfirmListener;

    public static DialogConfirmClearChatHistory showDialog(FragmentManager fragmentManager)
    {
        DialogConfirmClearChatHistory dialogConfirmClearChatHistory=new DialogConfirmClearChatHistory();
        dialogConfirmClearChatHistory.show(fragmentManager,dialogConfirmClearChatHistory.getClass().getName());
        return dialogConfirmClearChatHistory;
    }

    public void setOnClickConfirmListener(DialogConfirmClearChatHistory.OnClickConfirmListener onClickConfirmListener) {
        this.onClickConfirmListener = onClickConfirmListener;
    }

    @Nullable
    @Override
    protected View getRootView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        binding = com.netease.yunxin.app.im.databinding.DialogReConfirmBinding.inflate(layoutInflater, viewGroup, false);

        binding.tvTitle.setText("清除聊天记录");
        binding.tvContent.setText("确定清除所有聊天记录吗");

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if(onClickConfirmListener!=null)
                {
                    onClickConfirmListener.onCLick();
                }

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

    public interface OnClickConfirmListener
    {
        void onCLick();
    }

}
