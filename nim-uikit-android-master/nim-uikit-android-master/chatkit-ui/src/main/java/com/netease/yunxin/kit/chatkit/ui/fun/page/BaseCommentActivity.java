package com.netease.yunxin.kit.chatkit.ui.fun.page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.yunxin.kit.chatkit.ui.R;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.ui.widgets.BackTitleBar;
import com.netease.yunxin.kit.common.ui.widgets.CleanableEditText;
import com.netease.yunxin.kit.common.utils.NetworkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class BaseCommentActivity extends BaseActivity {

    public static final String REQUEST_COMMENT_NAME_KEY = "comment";

    private View rootView;
    protected CleanableEditText edtComment;
    protected BackTitleBar titleBar;

    protected abstract View initViewAndGetRootView(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = initViewAndGetRootView(savedInstanceState);
        checkViews();
        setContentView(rootView);
        initView();
    }

    protected void checkViews() {
        Objects.requireNonNull(rootView);
        Objects.requireNonNull(edtComment);
        Objects.requireNonNull(titleBar);
    }

    private void initView() {
        String comment = getIntent().getStringExtra(REQUEST_COMMENT_NAME_KEY);
        comment = comment == null ? "" : comment;
        edtComment.setText(comment);
        int textLength = edtComment.getBinding().editText.length();
        edtComment.getBinding().editText.setSelection(textLength);
        edtComment.getBinding().editText.requestFocus();
        configTitle(titleBar);
    }

    protected void configTitle(BackTitleBar titleBar) {
        titleBar
                .setTitle(R.string.comment_name)
                .setOnBackIconClickListener(v -> onBackPressed())
                .setActionText(R.string.save)
                .setActionListener(
                        v -> {
                            if (!NetworkUtils.isConnected()) {
                                Toast.makeText(this, R.string.contact_network_error_tip, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent();
                            if (!TextUtils.isEmpty(edtComment.getText())) {
                                intent.putExtra(REQUEST_COMMENT_NAME_KEY, edtComment.getText());
                            }
                            Map<FriendFieldEnum, Object> map = new HashMap<>();
                            map.put(FriendFieldEnum.ALIAS, edtComment.getText().toString());
                            NIMClient.getService(FriendService.class).updateFriendFields(getIntent().getStringExtra("accId"),map)
                                    .setCallback(new RequestCallbackWrapper<Void>() {
                                        @Override
                                        public void onResult(int code, Void result, Throwable exception) {
                                            Log.e("updateFriendFields",code+" ");
                                        }
                                    });
                            setResult(RESULT_OK, intent);
                            finish();
                        });
    }
}