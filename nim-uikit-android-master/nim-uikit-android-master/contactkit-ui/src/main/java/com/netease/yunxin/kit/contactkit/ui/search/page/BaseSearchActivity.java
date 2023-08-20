// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package com.netease.yunxin.kit.contactkit.ui.search.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgSearchOption;
import com.netease.yunxin.kit.chatkit.repo.ChatRepo;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.ui.viewholder.BaseBean;
import com.netease.yunxin.kit.common.ui.viewholder.ViewHolderClickListener;
import com.netease.yunxin.kit.common.ui.viewmodel.LoadStatus;
import com.netease.yunxin.kit.common.utils.KeyboardUtils;
import com.netease.yunxin.kit.contactkit.ui.ContactConstant;
import com.netease.yunxin.kit.contactkit.ui.fun.search.FunSearchDetailActivity;
import com.netease.yunxin.kit.contactkit.ui.model.P2PChatHistoryBean;
import com.netease.yunxin.kit.contactkit.ui.model.SearchMoreBean;
import com.netease.yunxin.kit.contactkit.ui.search.SearchAdapter;
import com.netease.yunxin.kit.contactkit.ui.search.SearchViewModel;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** search your friend or team */
public abstract class BaseSearchActivity extends BaseActivity {
  protected RecyclerView searchRv;

  private View rootView;

  protected View clearView;

  protected EditText searchEditText;

  protected View emptyView;

  protected View backView;

  protected SearchViewModel viewModel;
  protected SearchAdapter searchAdapter;
  protected Handler searchHandler;

  protected String routerFriend = RouterConstant.PATH_CHAT_P2P_PAGE;
  protected String routerTeam = RouterConstant.PATH_CHAT_TEAM_PAGE;



  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    rootView = initViewAndGetRootView();
    checkViews();
    setContentView(rootView);
    bindView();
    initData();
    showKeyBoard();
  }

  protected abstract View initViewAndGetRootView();

  protected void checkViews() {
    Objects.requireNonNull(rootView);
    Objects.requireNonNull(clearView);
    Objects.requireNonNull(searchEditText);
    Objects.requireNonNull(emptyView);
    Objects.requireNonNull(backView);
  }

  protected void bindView() {
    if (searchRv != null) {
      LinearLayoutManager layoutManager = new LinearLayoutManager(this);
      searchRv.setLayoutManager(layoutManager);
      searchAdapter = new SearchAdapter();
      searchAdapter.setViewHolderClickListener(
          new ViewHolderClickListener() {
            @Override
            public boolean onClick(View v, BaseBean data, int position) {
              if (!TextUtils.isEmpty(data.router)) {
                if(data instanceof P2PChatHistoryBean)
                {
                  P2PChatHistoryBean chatP2PSearchBean=(P2PChatHistoryBean)data;
                  XKitRouter.withKey(data.router)
                          .withParam(RouterConstant.CHAT_ID_KRY, chatP2PSearchBean.getImMessage().getSessionId())
                          .withParam(RouterConstant.KEY_MESSAGE,chatP2PSearchBean.getImMessage())
                          .withContext(BaseSearchActivity.this)
                          .navigate();

                }
                else
                {
                  XKitRouter.withKey(data.router)
                          .withParam(data.paramKey, data.param)
                          .withContext(BaseSearchActivity.this)
                          .navigate();
                }
              }
              else
              {
                if(data instanceof SearchMoreBean)
                {
                  SearchMoreBean searchMoreBean=(SearchMoreBean)data;
                  Intent intent=new Intent(BaseSearchActivity.this,FunSearchDetailActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  if(!TextUtils.isEmpty(searchMoreBean.getText()))
                  {
                    intent.putExtra("title",searchMoreBean.getText());
                  }
                  else
                  {
                    intent.putExtra("title",getString(searchMoreBean.getTextRes()));
                  }

                  intent.putExtra("type",searchMoreBean.getType());
                  intent.putExtra("keyWords",searchMoreBean.getKeyWords());
                  startActivity(intent);
                }
              }
              return true;
            }

            @Override
            public boolean onLongClick(View v, BaseBean data, int position) {
              return false;
            }
          });
      searchRv.setAdapter(searchAdapter);
    }

    if (clearView != null && searchEditText != null) {
      clearView.setOnClickListener(v -> searchEditText.setText(""));
    }

    if (searchEditText != null) {
      searchEditText.addTextChangedListener(
          new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
              searchHandler.removeCallbacksAndMessages(null);
              int types[]=new int[2];
              types[0]=ContactConstant.SearchViewType.USER;
              types[1]=ContactConstant.SearchViewType.ChatHistory;
              searchHandler.postDelayed(() -> viewModel.query(false,String.valueOf(s), types), 500);
              if (clearView != null) {
                if (TextUtils.isEmpty(String.valueOf(s))) {
                  clearView.setVisibility(View.GONE);
                } else {
                  clearView.setVisibility(View.VISIBLE);
                }
              }
            }
          });

      searchEditText.setOnEditorActionListener(
          (v, actionId, event) -> event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }
    if (backView != null) {
      backView.setOnClickListener(v -> onBackPressed());
    }
  }

  private void initData() {
    searchHandler = new Handler();
    viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    viewModel.setRouter(routerFriend, routerTeam);
    viewModel
        .getQueryLiveData()
        .observe(
            this,
            result -> {
              if (result.getLoadStatus() == LoadStatus.Success) {
                showEmpty(
                    (result.getData() == null || result.getData().size() < 1)
                        && !TextUtils.isEmpty(String.valueOf(searchEditText.getEditableText())));
                searchAdapter.setData(result.getData());
              }
            });
  }

  private void showKeyBoard() {
    searchHandler.postDelayed(
        () -> {
          if (searchEditText != null) {
            searchEditText.requestFocus();
            KeyboardUtils.showKeyboard(searchEditText);
          }
        },
        300);
  }

  private void showEmpty(boolean show) {
    if (emptyView != null) {
      emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
      if (searchRv != null) {
        searchRv.setVisibility(show ? View.GONE : View.VISIBLE);
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (searchHandler != null) {
      searchHandler.removeCallbacksAndMessages(null);
    }
  }
}
