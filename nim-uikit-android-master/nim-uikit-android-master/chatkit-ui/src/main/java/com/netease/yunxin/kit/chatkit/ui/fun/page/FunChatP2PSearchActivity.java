package com.netease.yunxin.kit.chatkit.ui.fun.page;

import static com.netease.yunxin.kit.chatkit.ui.ChatKitUIConstant.LIB_TAG;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NIMSDK;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgSearchOption;
import com.netease.yunxin.kit.alog.ALog;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.chatkit.ui.databinding.ActivityP2pChatSearchBinding;
import com.netease.yunxin.kit.chatkit.ui.databinding.FunChatSearchViewHolderBinding;
import com.netease.yunxin.kit.chatkit.ui.fun.viewholder.FunSearchMessageP2PViewHolder;
import com.netease.yunxin.kit.chatkit.ui.fun.viewholder.FunSearchMessageViewHolder;
import com.netease.yunxin.kit.chatkit.ui.model.ChatP2PSearchBean;
import com.netease.yunxin.kit.chatkit.ui.page.adapter.SearchMessageAdapter;
import com.netease.yunxin.kit.chatkit.ui.page.adapter.SearchMessageP2PAdapter;
import com.netease.yunxin.kit.chatkit.ui.page.viewmodel.SearchMessageP2PViewModel;
import com.netease.yunxin.kit.chatkit.ui.page.viewmodel.SearchMessageViewModel;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.common.ui.viewholder.ViewHolderClickListener;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.common.utils.KeyboardUtils;
import com.netease.yunxin.kit.common.utils.SizeUtils;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

import java.util.List;

public class FunChatP2PSearchActivity extends BaseActivity {

    ActivityP2pChatSearchBinding binding;

    SearchMessageP2PViewModel searchMessageP2PViewModel;

    SearchMessageP2PAdapter searchMessageP2PAdapter;

    String accId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityP2pChatSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeStatusBarColor(R.color.fun_chat_secondary_page_bg_color);

        initView();
    }

    private void initView() {
        accId=getIntent().getStringExtra(RouterConstant.CHAT_ID_KRY);
        searchMessageP2PViewModel = new ViewModelProvider(this).get(SearchMessageP2PViewModel.class);
        binding.searchTitleBar.setOnBackIconClickListener(v -> onBackPressed());

        binding.clearIv.setOnClickListener(v -> binding.searchEt.setText(""));
        binding.searchRv.addItemDecoration(getItemDecoration());
        binding.searchEt.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (binding.clearIv != null) {
                            if (TextUtils.isEmpty(String.valueOf(s))) {
                                binding.clearIv.setVisibility(View.GONE);
                                searchMessageP2PViewModel.clearData();
                            } else {
                                binding.clearIv.setVisibility(View.VISIBLE);
                                searchMessageP2PViewModel.searchWords(accId,s.toString());
                            }
                        }
                    }
                });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.searchRv.setLayoutManager(layoutManager);
        searchMessageP2PAdapter = new SearchMessageP2PAdapter();

        searchMessageP2PAdapter.setViewHolderClickListener(
                new ViewHolderClickListener() {
                    @Override
                    public boolean onClick(View v, BaseBean data, int position) {
                        KeyboardUtils.hideKeyboard(FunChatP2PSearchActivity.this);
                        if(data instanceof ChatP2PSearchBean)
                        {
                            ChatP2PSearchBean chatP2PSearchBean=(ChatP2PSearchBean)data;
                            XKitRouter.withKey(RouterConstant.PATH_FUN_CHAT_P2P_PAGE)
                                    .withParam(RouterConstant.CHAT_ID_KRY, accId)
                                    .withParam(RouterConstant.KEY_MESSAGE,chatP2PSearchBean.getImMessage())
                                    .withContext(FunChatP2PSearchActivity.this)
                                    .navigate();
                        }

                        return true;
                    }

                    @Override
                    public boolean onLongClick(View v, BaseBean data, int position) {
                        return false;
                    }
                });
        searchMessageP2PAdapter.setViewHolderFactory(
                (parent, viewType) ->
                        new FunSearchMessageP2PViewHolder(
                                FunChatSearchViewHolderBinding.inflate(
                                        LayoutInflater.from(parent.getContext()), parent, false)));

        binding.searchRv.setAdapter(searchMessageP2PAdapter);



        searchMessageP2PViewModel
                .getSearchMessageLiveData()
                .observe(
                        this,
                        result -> {
                            if (result.getLoadStatus() == LoadStatus.Success) {
                                if ((result.getData() == null || result.getData().size() < 1)
                                        && !TextUtils.isEmpty(String.valueOf(binding.searchEt.getEditableText()))) {
                                    showEmpty(true);
                                } else {
                                    showEmpty(false);
                                }
                                searchMessageP2PAdapter.setData(result.getData());
                            }
                        });
    }

    protected void showEmpty(boolean show) {
        if (show) {
            binding.emptyLayout.setVisibility(View.VISIBLE);
            binding.searchRv.setVisibility(View.GONE);
        } else {
            binding.emptyLayout.setVisibility(View.GONE);
            binding.searchRv.setVisibility(View.VISIBLE);
        }
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            final int topPadding = SizeUtils.dp2px(1);

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
}
