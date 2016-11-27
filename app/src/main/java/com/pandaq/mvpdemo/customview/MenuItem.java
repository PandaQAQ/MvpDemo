package com.pandaq.mvpdemo.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pandaq.mvpdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PandaQ on 2016/11/25.
 * email : 767807368@qq.com
 */

public class MenuItem extends RelativeLayout {

    @BindView(R.id.icon_left)
    ImageView iconLeft;
    @BindView(R.id.menu_text)
    TextView menuText;
    @BindView(R.id.icon_right)
    ImageView iconRight;

    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.menuitem_layout, this);
        ButterKnife.bind(this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView);
        int textColor = ta.getColor(R.styleable.MenuItemView_textColor, Color.GRAY);
        menuText.setTextColor(textColor);
        float textSize = ta.getDimension(R.styleable.MenuItemView_textSize, getResources().getDimension(R.dimen.default_textsize));
        menuText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int leftIconId = ta.getResourceId(R.styleable.MenuItemView_leftIcon, R.drawable.image_reward);
        iconLeft.setImageDrawable(getResources().getDrawable(leftIconId, null));
        int rightIconId = ta.getResourceId(R.styleable.MenuItemView_rightIcon, R.drawable.arrow_right);
        iconRight.setImageDrawable(getResources().getDrawable(rightIconId, null));
        String text = ta.getString(R.styleable.MenuItemView_text);
        if (text != null)
            menuText.setText(text);
        ta.recycle();
    }

    public void setMenuText(String text) {
        menuText.setText(text);
    }

    public CharSequence getText() {
        return menuText.getText();
    }
}
