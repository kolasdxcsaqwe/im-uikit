package com.netease.yunxin.app.im;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.netease.yunxin.app.im.dialog.LoadingDialog;

public class BaseActivity extends FragmentActivity {

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showLoading()
    {
        if(!isFinishing() && !isDestroyed()){
            if(loadingDialog==null)
            {
                loadingDialog=new LoadingDialog(this);
                loadingDialog.show();
            }
            else
            {
                if(!loadingDialog.isShowing())
                {
                    loadingDialog.show();
                }
            }
        }
    }

    public void dismissLoading()
    {
        if(loadingDialog!=null)
        {
            loadingDialog.dismiss();
            loadingDialog=null;
        }
    }
}
