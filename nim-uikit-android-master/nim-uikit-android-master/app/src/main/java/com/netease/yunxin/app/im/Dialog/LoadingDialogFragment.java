package com.netease.yunxin.app.im.Dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.netease.yunxin.app.im.R;

import java.lang.ref.WeakReference;


/**
 * 等待 Dialog
 */
public class LoadingDialogFragment extends BaseDialogFragment {


    static int count;
    private boolean isAttachActivity=false;
    onAttachActivityListener onAttachActivityListener;
    int dimissSignal=2333;
    final MyHandler handler=new MyHandler(this,Looper.getMainLooper());
//    @BindView(R.id.loading_pro)
//    ImageView loadingPro;

    public void setOnAttachActivityListener(LoadingDialogFragment.onAttachActivityListener onAttachActivityListener) {
        this.onAttachActivityListener = onAttachActivityListener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
        Log.e("onSaveInstanceState","onSaveInstanceState");
    }

    @Override
    protected int getViewId() {
        return R.layout.loading_dialogfragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mIsOutCanback = false;
        mIsKeyCanback = false;
        AnimationsStyle = -1;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isAttachActivity=true;
        if(onAttachActivityListener!=null)
        {
            onAttachActivityListener.onAttachActivity();
        }
        if(getView()!=null)
        {
            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    protected void initViews(View view) {
        lottieLoading.loop(true);
        lottieLoading.setImageAssetsFolder("/");
        lottieLoading.setAnimation("loading.json");
        lottieLoading.playAnimation();
        lottieLoading.setAdjustViewBounds(true);
        count++;
//        Log.e("COUNT++",count+"");
//        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        //加载图片
//        Glide.with(getActivity())
//                .load(R.drawable.loading)
//                .apply(options)
//                .into(loadingPro);

        if( BoxApplication.isLineAvailable())
        {
            handler.sendEmptyMessageDelayed(dimissSignal,30000);//30秒后怎么都给他停了
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(handler!=null)
        {
            handler.removeMessages(dimissSignal);
        }

        count--;
//        Log.e("COUNT--",count+"");
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        if(handler!=null)
        {
            handler.removeMessages(dimissSignal);
        }
        count--;
//        Log.e("LoadingCOUNT--",count+"");
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lottieLoading != null) {
            lottieLoading.pauseAnimation();
        }
        if(handler!=null)
        {
            handler.removeMessages(dimissSignal);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (lottieLoading != null) {
            lottieLoading.pauseAnimation();
        }
        if(handler!=null)
        {
            handler.removeMessages(dimissSignal);
        }
    }

    public interface onAttachActivityListener
    {
        void onAttachActivity();
    }
    private static class MyHandler extends Handler
    {
        WeakReference<LoadingDialogFragment> weakthis;

        public MyHandler(LoadingDialogFragment fragment,Looper looper) {
            super(looper);
            weakthis=new WeakReference<LoadingDialogFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(weakthis.get().getActivity()!=null && !weakthis.get().getActivity().isFinishing() && !weakthis.get().getActivity().isDestroyed())
            {
                try
                {
                    weakthis.get().dismissAllowingStateLoss();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
