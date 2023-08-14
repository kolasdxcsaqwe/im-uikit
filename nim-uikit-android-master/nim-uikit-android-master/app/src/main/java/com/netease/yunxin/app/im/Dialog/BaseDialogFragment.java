package com.netease.yunxin.app.im.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import androidx.fragment.app.DialogFragment;


import com.netease.yunxin.app.im.R;

import java.lang.ref.WeakReference;

/**
 * <p>类描述： DialogFragment 基类
 * <p>创建人：Simon
 * <p>创建时间：2019-02-14
 * <p>修改人：Simon
 * <p>修改时间：2019-02-14
 * <p>修改备注：
 **/
public abstract class BaseDialogFragment extends DialogFragment  {
    //宽高比例 最高1 最低0
    protected double intScreenWProportion = 1; //宽比例
    protected double intScreenHProportion = 1; //高比例
    protected boolean mIsOutCanback = false; //是否点击 dialog外的地方dismiss
    protected boolean mIsKeyCanback = true; //是否点击 点击物理返回键可以取消
    protected int AnimationsStyle = R.style.BasedialogAnim;
    protected WeakReference<Context> mWeakContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public WeakReference<Context> getFragmentContext()
    {
        return mWeakContext;
    }

    public boolean isConditionOk()
    {
        return mWeakContext!=null && mWeakContext.get()!=null ;
    }

    protected abstract int getViewId();

    protected abstract void initViews(View view);

    protected abstract void initData();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(getViewId(), container, false);
        initViews(view);
        initData();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    getDialog().setCanceledOnTouchOutside(mIsKeyCanback);//键盘点击时是否可以取消--不需要设置了
                    return !mIsKeyCanback;//return true 不往上传递则关闭不了，默认是可以取消，即return false
                } else {
                    return false;
                }
            }
        });

        return view;
    }


    public void settingDialog() {
        if(getDialog()!=null)
        {
            Window mWindow = this.getDialog().getWindow();
            //状态栏
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            //退出,进入动画
            if (AnimationsStyle != -1) {
                mWindow.setWindowAnimations(AnimationsStyle);
            }
            //清理背景变暗
            //mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //点击window外的区域 是否消失
            getDialog().setCanceledOnTouchOutside(mIsOutCanback);
            //是否可以取消,会影响上面那条属性
            setCancelable(true);
            //window外可以点击,不拦截窗口外的事件
            //mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            //设置背景颜色,只有设置了这个属性,宽度才能全屏MATCH_PARENT
            mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

            final Display defaultDisplay = getActivity().getWindow().getWindowManager()
                    .getDefaultDisplay();
            int intScreenH = defaultDisplay.getHeight();
            int intScreenW = defaultDisplay.getWidth();

            WindowManager.LayoutParams mWindowAttributes = mWindow.getAttributes();
            if (intScreenWProportion < 0 || intScreenWProportion > 1) {
                mWindowAttributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                mWindowAttributes.width = (int) (intScreenW * intScreenWProportion);
            }
            if (intScreenHProportion < 0 || intScreenHProportion > 1) {
                mWindowAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                mWindowAttributes.height = (int) (intScreenH * intScreenHProportion);
            }

            //gravity
            mWindowAttributes.gravity = Gravity.CENTER;
            mWindow.setAttributes(mWindowAttributes);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        settingDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mWeakContext = new WeakReference<>(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("web", "横屏");
            // 横屏
            settingDialog();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("web", "竖屏");
            // 竖屏
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        DialogFramentManager.getInstance().removeDialog(this);
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        DialogFramentManager.getInstance().removeDialog(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DialogFramentManager.getInstance().removeDialog(this);
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showLongToast(message);
        }
    }

    public void setDialogViewRatio(float percent,View view)
    {
        int screenWidth= DensityUtil.getScreenSize(getFragmentContext().get()).x;
        float viewWidth=1.0f* DensityUtil.getScreenSize(getFragmentContext().get()).x*percent;
        float margin=(screenWidth-viewWidth)/2;
        ViewGroup.LayoutParams vl= view.getLayoutParams();
        vl.width=(int) viewWidth;

        if(vl instanceof RelativeLayout.LayoutParams)
        {
            RelativeLayout.LayoutParams rl=(RelativeLayout.LayoutParams)vl;
            rl.leftMargin=(int) margin;
            view.setLayoutParams(rl);
        }
        else if(vl instanceof LinearLayout.LayoutParams)
        {
            LinearLayout.LayoutParams ll=(LinearLayout.LayoutParams)vl;
            ll.leftMargin=(int) margin;
            view.setLayoutParams(ll);
        }
        else if(vl instanceof FrameLayout.LayoutParams)
        {
            FrameLayout.LayoutParams fl=(FrameLayout.LayoutParams)vl;
            fl.leftMargin=(int) margin;
            view.setLayoutParams(fl);
        }

    }
}
