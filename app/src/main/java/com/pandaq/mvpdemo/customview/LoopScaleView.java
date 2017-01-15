package com.pandaq.mvpdemo.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.pandaq.mvpdemo.R;

/**
 * Created by PandaQ on 2017/1/13.
 * email : 767807368@qq.com
 * 一个循环的刻度尺
 */

public class LoopScaleView extends View {
    //画底线的画笔
    private Paint paint;
    //屏幕宽度
    private int screenWidth;
    //尺子控件总宽度
    private float viewWidth;
    //尺子控件总宽度
    private float viewHeight;
    //中间的标识图片
    private Bitmap cursorMap;
    //未设置标识图片时默认绘制一条线作为标尺的线的颜色
    private int cursorColor = Color.RED;
    //大刻度线宽，默认为3
    private int cursorWidth = 3;
    //小刻度线宽，默认为2
    private int scaleWidth = 2;
    //设置屏幕宽度内最多显示的大刻度数，默认为3个
    private int showItemSize = 6;
    //标尺开始位置
    private int currLocation = 0;
    //刻度表的最大值，默认为250
    private int maxValue = 200;
    //一个刻度表示的值的大小
    private int oneItemValue=1;
    //设置刻度线间宽度,大小由 showItemSize确定
    private int scaleDistance;
    //刻度高度，默认值为40
    private float scaleHeight = 40;
    //已选择的刻度区间的刻度色
    private int scaleSelectColor = Color.BLUE;
    //未选择的区间的刻度色
    private int scaleUnSelectColor = Color.GRAY;
    //刻度文字的颜色
    private int scaleTextColor = Color.BLUE;
    //刻度文字的大小,默认为24px
    private int scaleTextSize = 24;
    //手势解析器
    private GestureDetector mGestureDetector;

    public LoopScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoopScaleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoopScaleView);
        showItemSize = ta.getInteger(R.styleable.LoopScaleView_maxShowItem, 3);
        ta.recycle();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        //一个小刻度的宽度（十进制，每5个小刻度为一个大刻度）
        scaleDistance = (screenWidth / (showItemSize * 5));
        //尺子长度总的个数*一个的宽度
        viewWidth = maxValue / oneItemValue * scaleDistance+screenWidth/2;
        mGestureDetector = new GestureDetector(context,gestureListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLine(canvas);
        drawScale(canvas);
        drawCursor(canvas);
    }

    /**
     * 绘制主线
     *
     * @param canvas 绘制的画布
     */
    private void drawLine(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        paint.setColor(scaleUnSelectColor);
        canvas.drawLine(0, viewHeight, viewWidth, viewHeight, paint);
    }

    private void drawCursor(Canvas canvas) {
        if (cursorMap == null) { //绘制一条红色的竖线线
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(cursorWidth);
            paint.setColor(cursorColor);
            canvas.drawLine(screenWidth / 2, 0, screenWidth / 2, 2 * viewHeight / 5, paint);
        } else { //绘制标识图片
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            float left = (screenWidth - cursorMap.getWidth()) / 2;
            float top = 0;
            float right = (screenWidth + cursorMap.getWidth()) / 2;
            float bottom = 2 * viewHeight / 5;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawBitmap(cursorMap, null, rectF, paint);
        }
    }

    // 拦截屏幕滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 滑动手势处理
     */
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
//            if (!isScrollingPerformed) {
//                isScrollingPerformed = true;
//            }
//            doScroll(-distanceX);
//            invalidate();
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            lastScrollX = getCurrentItem() * getItemWidth() + scrollingOffset;
//            int maxX = getItemsCount()
//                    * getItemWidth();
//            int minX = 0;
//            scroller.fling(lastScrollX, 0, (int) (-velocityX / 1.5), 0, minX, maxX, 0, 0);
//            setNextMessage(MESSAGE_SCROLL);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return super.onSingleTapUp(e);
        }
    };

    /**
     * 绘制刻度线
     *
     * @param canvas 绘制的画布
     */
    private void drawScale(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(scaleWidth);
        //计算游标开始绘制的位置
        float startLocation = (screenWidth / 2) - ((scaleDistance * (currLocation / oneItemValue)));
        for (int i = 0; i < maxValue / oneItemValue; i++) {
            //判断当前刻度是否小于当前刻度
            if (i * oneItemValue <= currLocation) {
                paint.setColor(scaleSelectColor);
            } else {
                paint.setColor(scaleUnSelectColor);
            }
            float location = startLocation + i * scaleDistance;
            if (i % 10 == 0) {
                canvas.drawLine(location, viewHeight - scaleHeight, location, viewHeight, paint);
                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setTextSize(scaleTextSize);
                if (i * oneItemValue <= currLocation) {
                    paintText.setColor(scaleSelectColor);
                } else {
                    paintText.setColor(scaleTextColor);
                }
                String drawStr = oneItemValue * i + "";
                Rect bounds = new Rect();
                paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);
                canvas.drawText(drawStr, location - bounds.width() / 2, viewHeight - (scaleHeight + 5), paintText);
            } else {
                canvas.drawLine(location, viewHeight - scaleHeight / 2, location, viewHeight, paint);
            }
            //绘制选中的背景
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.trans_green_5b00796b));
            canvas.drawRect(startLocation, viewHeight - scaleHeight / 3, startLocation + currLocation / oneItemValue * scaleDistance, viewHeight, paint);
        }
    }

    /**
     * 设置标识的颜色
     *
     * @param color 颜色id
     */
    public void setCursorColor(int color) {
        this.cursorColor = color;
        invalidate();
    }

    /**
     * 设置标识的宽度
     *
     * @param width 宽度
     */
    public void setCursorWidth(int width) {
        this.cursorWidth = width;
        invalidate();
    }

    /**
     * 设置游标的bitmap位图
     *
     * @param cursorMap 位图
     */
    public void setCursorMap(Bitmap cursorMap) {
        this.cursorMap = cursorMap;
        invalidate();
    }

    /**
     * 设置刻度线的宽度
     * @param scaleWidth 刻度线的宽度
     */
    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
        invalidate();
    }

    /**
     * 设置屏幕宽度内大Item的数量
     * @param showItemSize 屏幕宽度内显示的大 item数量
     */
    public void setShowItemSize(int showItemSize) {
        this.showItemSize = showItemSize;
        invalidate();
    }

    /**
     * 设置当前游标所在的值
     * @param currLocation 当前游标所在的值
     */
    public void setCurrLocation(int currLocation) {
        this.currLocation = currLocation;
        invalidate();
    }

    /**
     * 设置刻度线的高度
     * @param scaleHeight
     */
    public void setScaleHeight(float scaleHeight) {
        this.scaleHeight = scaleHeight;
        invalidate();
    }
    /**
     * 设置已选择区域的背景颜色
     * @param scaleSelectColor 已选区域的背景色
     */
    public void setScaleSelectColor(int scaleSelectColor) {
        this.scaleSelectColor = scaleSelectColor;
        invalidate();
    }

    /**
     * 设置未选择区域的背景颜色
     * @param scaleUnSelectColor 未选区域的背景色
     */
    public void setScaleUnSelectColor(int scaleUnSelectColor) {
        this.scaleUnSelectColor = scaleUnSelectColor;
        invalidate();
    }

    /**
     * 设置刻度表上文字的颜色
     * @param scaleTextColor 文字颜色id
      */
    public void setScaleTextColor(int scaleTextColor) {
        this.scaleTextColor = scaleTextColor;
        invalidate();
    }

    /**
     * 设置刻度标上的文字的大小
     * @param scaleTextSize 文字大小
     */
    public void setScaleTextSize(int scaleTextSize) {
        this.scaleTextSize = scaleTextSize;
        invalidate();
    }

    /**
     * 设置刻度的最大值
     * @param maxValue 刻度的最大值
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
    }

    /**
     * 设置 一刻度所代表的的值的大小
     * @param oneItemValue
     */
    public void setOneItemValue(int oneItemValue) {
        this.oneItemValue = oneItemValue;
        invalidate();
    }
}
