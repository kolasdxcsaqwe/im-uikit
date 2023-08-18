package com.netease.yunxin.app.im.main.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.netease.yunxin.app.im.R;
import com.netease.yunxin.app.im.databinding.ActivityEditNicknameBinding;
import com.netease.yunxin.app.im.databinding.ActivityMyqrcodeBinding;
import com.netease.yunxin.kit.common.ui.activities.BaseActivity;
import com.netease.yunxin.kit.common.ui.utils.AvatarColor;
import com.netease.yunxin.kit.common.ui.widgets.ContactAvatarView;
import com.netease.yunxin.kit.contactkit.ui.utils.QrcodeGen;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.model.UserInfo;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import com.netease.yunxin.kit.corekit.im.repo.CommonRepo;

import java.io.File;

public class MyQrcodeActivity extends BaseActivity {

    ActivityMyqrcodeBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyqrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_ededed));


        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvName.setText(IMKitClient.account());

        Glide.with(this).load(new File(QrcodeGen.geInstance().getPersonalQrcode(this))).into(binding.ivQrcode);

        loadData(binding.avatarView, IMKitClient.account());
    }

    private void loadData(ContactAvatarView imageView,String account) {
        CommonRepo.getUserInfo(
                account,
                new FetchCallback<UserInfo>() {
                    @Override
                    public void onSuccess(@Nullable UserInfo userInfo) {
                        if(userInfo!=null && !TextUtils.isEmpty(userInfo.getAvatar()))
                        {
                            imageView.setData(userInfo.getAvatar(),
                                    userInfo.getName(),
                                    AvatarColor.avatarColor(IMKitClient.account()));
                        }
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(@Nullable Throwable exception) {

                    }
                });
    }
}
