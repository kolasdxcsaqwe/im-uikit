package com.netease.yunxin.app.im.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.netease.yunxin.app.im.bean.LoginIMResultBean;
import com.netease.yunxin.app.im.databinding.FragmentTaskBinding;
import com.netease.yunxin.app.im.utils.HttpRequest;
import com.netease.yunxin.app.im.utils.OkhttpCallBack;
import com.netease.yunxin.app.im.utils.SPUtils;
import com.netease.yunxin.kit.common.ui.fragments.BaseFragment;
import com.netease.yunxin.kit.common.ui.utils.ToastX;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;

public class TaskFragment extends BaseFragment {

    String url;

    FragmentTaskBinding fragmentTaskBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTaskBinding=FragmentTaskBinding.inflate(inflater,container,false);
        return fragmentTaskBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!TextUtils.isEmpty(url))
        {
            fragmentTaskBinding.webview.loadUrl(url);
        }
        else
        {
            getUrl();
        }
    }

    private void getUrl()
    {
        if(getActivity()==null || getActivity().isDestroyed() || getActivity().isFinishing())
        {
            return;
        }

        HttpRequest.get(HttpRequest.systemConfig, new OkhttpCallBack(true,getActivity()) {
            @Override
            public void onHttpFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onHttpResponse(@NonNull Call call, @NonNull JSONObject jsonObject, boolean isSuccess, String msg) throws IOException {
                if(isSuccess)
                {
                    SPUtils.getInstance().save(SPUtils.ConfigData,jsonObject.optJSONObject("data").toString());
                    url=jsonObject.optJSONObject("data").optString("webDomain","");
                    fragmentTaskBinding.webview.loadUrl(url);
                }
                else
                {
                    ToastX.showShortToast(msg);
                }
            }
        });
    }


}
