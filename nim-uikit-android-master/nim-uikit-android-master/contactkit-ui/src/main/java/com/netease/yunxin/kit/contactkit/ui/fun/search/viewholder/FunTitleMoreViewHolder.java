package com.netease.yunxin.kit.contactkit.ui.fun.search.viewholder;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.netease.yunxin.kit.common.ui.viewholder.BaseViewHolder;
import com.netease.yunxin.kit.contactkit.ui.databinding.FunSearchMoreItemBinding;
import com.netease.yunxin.kit.contactkit.ui.databinding.FunSearchTitleViewHolderBinding;
import com.netease.yunxin.kit.contactkit.ui.databinding.SearchTitleItemLayoutBinding;
import com.netease.yunxin.kit.contactkit.ui.model.SearchMoreBean;

public class FunTitleMoreViewHolder extends BaseViewHolder<SearchMoreBean> {

    private FunSearchMoreItemBinding viewBinding;

    public FunTitleMoreViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public FunTitleMoreViewHolder(@NonNull FunSearchMoreItemBinding viewBinding) {
        this(viewBinding.getRoot());
        this.viewBinding = viewBinding;
    }

    @Override
    public void onBindData(SearchMoreBean data, int position) {
        if (TextUtils.isEmpty(data.getText())) {
            viewBinding.tvTitle.setText(data.getTextRes());
        } else {
            viewBinding.tvTitle.setText(data.getText());
        }
        viewBinding.getRoot().setOnClickListener(v -> itemListener.onClick(v, data, position));

    }
}