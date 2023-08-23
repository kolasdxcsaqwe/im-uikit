package com.netease.yunxin.app.im.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.yunxin.app.im.databinding.FragmentTaskBinding;
import com.netease.yunxin.kit.common.ui.fragments.BaseFragment;

public class TaskFragment extends BaseFragment {

    FragmentTaskBinding fragmentTaskBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTaskBinding=FragmentTaskBinding.inflate(inflater,container,false);
        return fragmentTaskBinding.getRoot();
    }
}
