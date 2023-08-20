package com.netease.yunxin.kit.chatkit.ui.fun.page;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.databinding.FunCommentActivityLayoutBinding;
import com.netease.yunxin.kit.common.ui.widgets.BackTitleBar;

public class FunCommentActivity extends BaseCommentActivity {

    @Override
    protected View initViewAndGetRootView(Bundle savedInstanceState) {
        FunCommentActivityLayoutBinding binding =
                FunCommentActivityLayoutBinding.inflate(getLayoutInflater());
        titleBar = binding.title;
        titleBar.setActionTextColor(getResources().getColor(R.color.color_58be6b));
        edtComment = binding.edtComment;
        return binding.getRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        changeStatusBarColor(R.color.color_ededed);
        super.onCreate(savedInstanceState);
    }

    protected void configTitle(BackTitleBar titleBar) {
        super.configTitle(titleBar);
        titleBar.getTitleTextView().setTextSize(17);
        titleBar.getTitleTextView().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }
}
