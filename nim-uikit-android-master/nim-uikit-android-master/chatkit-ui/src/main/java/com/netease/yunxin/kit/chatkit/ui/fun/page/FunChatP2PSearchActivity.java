package com.netease.yunxin.kit.chatkit.ui.fun.page;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.yunxin.kit.chatkit.ui.databinding.ActivityP2pChatSearchBinding;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;

public class FunChatP2PSearchActivity extends BaseActivity {

    ActivityP2pChatSearchBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityP2pChatSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchEt.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (binding.clearIv != null) {
                            if (TextUtils.isEmpty(String.valueOf(s))) {
                                binding.clearIv.setVisibility(View.GONE);
                            } else {
                                binding.clearIv.setVisibility(View.VISIBLE);

                                
                            }
                        }
                    }
                });
    }
}
