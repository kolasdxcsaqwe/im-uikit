package com.netease.yunxin.kit.contactkit.ui.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.netease.yunxin.kit.common.ui.utils.AvatarColor;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.common.ui.widgets.ContactAvatarView;
import com.netease.yunxin.kit.common.utils.SizeUtils;
import com.netease.yunxin.kit.contactkit.ui.R;
import com.netease.yunxin.kit.contactkit.ui.utils.QrcodeGen;
import com.netease.yunxin.kit.corekit.im.IMKitClient;
import com.netease.yunxin.kit.corekit.im.model.UserInfo;
import com.netease.yunxin.kit.corekit.im.provider.FetchCallback;
import com.netease.yunxin.kit.corekit.im.repo.CommonRepo;

import java.io.File;

public class MyQrCodeDialog extends Dialog {

    String userName;
    public MyQrCodeDialog(@NonNull Context context,String userName) {
        super(context, R.style.TranslucentDialogTheme);
        this.userName=userName;
        initView();
    }

    public MyQrCodeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected MyQrCodeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView()
    {
        setContentView(R.layout.dialog_myqrcode);

        ContactAvatarView avatar_view=findViewById(R.id.avatar_view);

        TextView tv_name=findViewById(R.id.tv_name);
        tv_name.setText(userName);

        ImageView ivQrcode=findViewById(R.id.ivQrcode);
        Glide.with(getContext()).load(new File(QrcodeGen.geInstance().getPersonalQrcode(getContext()))).into(ivQrcode);

        loadData(avatar_view,IMKitClient.account());
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
