package com.pandaq.mvpdemo.ui;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.pandaq.mvpdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PandaQ on 2017/7/17.
 * 底部弹出菜单的Demo
 */

public class BottomMenuActivity extends BaseActivity {
    @BindView(R.id.rl_parent)
    RelativeLayout mRlParent;
    private BottomSheetDialog mBottomSheetDialog;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);
        ButterKnife.bind(this);
        initBottomDialog();
        initPopupWindow();
    }

    @OnClick({R.id.tv_popup, R.id.tv_bottomSheet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_popup:
                if (mPopupWindow == null) {
                    return;
                }
                mPopupWindow.setFocusable(true);
                if (!mPopupWindow.isShowing()) {
                    int[] location = new int[2];
                    mRlParent.getLocationOnScreen(location);
                    mPopupWindow.showAtLocation(mRlParent, Gravity.START | Gravity.BOTTOM, 0, -location[1]);
                    backgroundAlpha(0.5f);
                } else {
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.tv_bottomSheet:
                if (mBottomSheetDialog == null) {
                    return;
                }
                if (mBottomSheetDialog.isShowing()) {
                    mBottomSheetDialog.dismiss();
                } else {
                    mBottomSheetDialog.show();
                }
                break;
        }
    }

    private void initBottomDialog() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null, false);
        mBottomSheetDialog.setContentView(view);
        setBehaviorCallback();
    }

    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null, false);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(view);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        //以上代码已经能够实现 popup 的弹出效果，但是为了体验更好应该添加动画
        mPopupWindow.setAnimationStyle(R.style.BottomPopupWindow);
        System.out.println(AnimationUtils.currentAnimationTimeMillis());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    private void setBehaviorCallback() {
        View view = mBottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        assert view != null;
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetDialog.dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 对 BottomSheetDialog 进行上下滑动时会调用此方法
            }
        });
    }

    /**
     * popup 弹出和收起时调整界面颜色
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
