package com.pandaq.mvpdemo.customview.expandingcard

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.pandaq.mvpdemo.R

/**
 * Created by huxinyu on 2019/1/25.
 * Email : panda.h@foxmail.com
 * Description :
 */
class ExpandingCard(context: Context, attrs: AttributeSet) : View(context) {

    companion object {
        const val STATE_COLLAPSE = 0
        const val STATE_EXPAND = 1
    }

    private var scrollFactor: Float
    private var backScale: Float
    private var autoExecute: Boolean
    private var content: FrameLayout
    private var cardCover: CardView

    // 一次触摸滑动距离
    private var distance = 0f
    private var downY = 0f
    // 覆盖View Y 轴初始偏移量
    private var initCoverTransY = 0f
    private lateinit var evaluator: FloatEvaluator
    private lateinit var collapseAnim: ValueAnimator
    private lateinit var expandAnim: ValueAnimator
    private var viewPause = false
    private var cardState = STATE_COLLAPSE

    private var mStateListener: ExpandStateListener? = null

    init {
        inflate(context, R.layout.layout_expanding_card, null)
        content = findViewById(R.id.fl_content)
        cardCover = findViewById(R.id.card_cover)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandingCard)
        val coverElevation = ta.getDimension(R.styleable.ExpandingCard_coverElevation, -1f)
        if (coverElevation != -1f) {
            cardCover.elevation = coverElevation
        }
        scrollFactor = ta.getFloat(R.styleable.ExpandingCard_scrollFactor, 0.2f)
        backScale = ta.getFloat(R.styleable.ExpandingCard_backScale, 0.4f)
        autoExecute = ta.getBoolean(R.styleable.ExpandingCard_autoExecute, false)
        ta.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewPause = false
                collapseAnim.cancel()
                expandAnim.cancel()
                downY = event.rawY
                initCoverTransY = cardCover.translationY
            }
            MotionEvent.ACTION_MOVE -> {
                distance = event.rawY - downY + initCoverTransY // 上滑距离
                if (-distance > cardCover.measuredHeight * scrollFactor) {
                    distance = -cardCover.measuredHeight * scrollFactor
                }
                if (distance > 0) {
                    distance = 0f
                }
                if (cardState == STATE_EXPAND && distance <= -cardCover.measuredHeight * scrollFactor) { // 展开状态拉到顶继续上拉
                    if (!viewPause) {
                        mStateListener?.onStateChanged(cardState)
                    }
                } else {
                    scrollCover(distance)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (cardState == STATE_COLLAPSE) { //当前还未到达展开状态
                    if (-distance >= cardCover.measuredHeight * scrollFactor / 2) {
                        cardState = STATE_EXPAND
                        // 动画回到展开状态
                        expand()
                    } else {
                        // 动画回到折叠状态
                        collapse()
                    }
                } else { //当前还未到达关闭状态
                    if (-distance < cardCover.measuredHeight * scrollFactor / 2) { // 下滑超过一半折叠
                        cardState = STATE_COLLAPSE
                        // 动画回到折叠状态
                        collapse()
                    } else {
                        // 动画回到展开状态
                        expand()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun scrollCover(distance: Float) {
        if (distance <= 0 && -distance < cardCover.measuredHeight * scrollFactor) { // 只允许上滑卡片
            cardCover.translationY = distance
            content.translationY = -distance
            content.scaleX = 1 + backScale * (-distance / (cardCover.measuredHeight * scrollFactor)) //最大扩大到 1.2 倍
            content.scaleY = 1 + backScale * (-distance / (cardCover.measuredHeight * scrollFactor))
        }
    }

    private fun expand() {
        val currentTransY = cardCover.translationY
        expandAnim.addUpdateListener { animation ->
            val float = animation.animatedValue as Float
            distance = evaluator.evaluate(float, currentTransY,
                    -cardCover.measuredHeight * scrollFactor)
            scrollCover(distance)
        }
        scrollCover(-cardCover.measuredHeight * scrollFactor)
        expandAnim.start()
    }

    private fun collapse() {
        val currentTransY = cardCover.translationY
        collapseAnim.addUpdateListener { animation ->
            val float = animation.animatedValue as Float
            val distance = evaluator.evaluate(float, currentTransY,
                    0)
            scrollCover(distance)
        }
        collapseAnim.start()
    }

    interface ExpandStateListener {
        fun onStateChanged(state: Int)
    }
}