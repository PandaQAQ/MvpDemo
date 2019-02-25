package com.pandaq.mvpdemo.customview.expandingcard

import android.animation.Animator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.pandaq.mvpdemo.R

/**
 * Created by huxinyu on 2019/1/25.
 * Email : panda.h@foxmail.com
 * Description :
 */
class ExpandingCard(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    companion object {
        const val STATE_COLLAPSE = 0
        const val STATE_EXPAND = 1
    }

    private var scrollFactor: Float = 0.2f
    private var backScale: Float = 1.1f
    private var autoExecute: Boolean = false
    private lateinit var content: FrameLayout
    private lateinit var cardCover: CardView
    private var contentWidth: Int = -1
    private var contentHeight: Int = -1

    // 一次触摸滑动距离
    private var distance = 0f
    private var downY = 0f
    // 覆盖View Y 轴初始偏移量
    private var initCoverTransY = 0f
    private var evaluator: FloatEvaluator = FloatEvaluator()
    private var expandAnim: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1f)
    private var collapseAnim: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1f)
    private var viewPause = false
    private var cardState = STATE_COLLAPSE

    private var mStateListener: ExpandStateListener? = null
    private var mAnimator = StateAnimatorListener()

    init {
        collapseAnim.duration = 200
        expandAnim.duration = 200
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.layout_expanding_card, this, true)
        content = findViewById(R.id.fl_content)
        cardCover = findViewById(R.id.card_cover)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandingCard)
        val coverElevation = ta.getDimension(R.styleable.ExpandingCard_coverElevation, 8f)
        if (coverElevation != 8f) {
            cardCover.elevation = coverElevation
        }
        scrollFactor = ta.getFloat(R.styleable.ExpandingCard_scrollFactor, 0.2f)
        backScale = ta.getFloat(R.styleable.ExpandingCard_contentScale, 1.1f)
        autoExecute = ta.getBoolean(R.styleable.ExpandingCard_autoExecute, false)
        val width = ta.getDimension(R.styleable.ExpandingCard_cardWidth, -1f)
        if (width != -1f) {
            content.layoutParams.width = width.toInt()
            cardCover.layoutParams.width = width.toInt()
        }
        val height = ta.getDimension(R.styleable.ExpandingCard_cardHeight, -1f)
        if (height != -1f) {
            content.layoutParams.height = height.toInt()
            cardCover.layoutParams.height = height.toInt()
        }
        val coverTransName = ta.getString(R.styleable.ExpandingCard_coverTransName)
        val contentTransName = ta.getString(R.styleable.ExpandingCard_contentTransName)
        cardCover.transitionName = coverTransName
        content.transitionName = contentTransName
        ta.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        contentWidth = cardCover.measuredWidth
        contentHeight = cardCover.measuredHeight
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
                if (cardState != STATE_EXPAND || distance > -cardCover.measuredHeight * 0.18f) { // 展开状态拉到顶继续上拉
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
                        if (distance > -cardCover.measuredHeight * 0.18f) {
                            expand()
                        }
                    }
                }
            }
        }
        return true
    }

    fun setContentView(view: View) {
        content.removeAllViews()
        content.addView(view)
    }

    fun setCoverView(view: View) {
        cardCover.removeAllViews()
        cardCover.addView(view)
    }

    private fun scrollCover(distance: Float) {
        if (distance <= 0 && -distance < cardCover.measuredHeight * scrollFactor) { // 只允许上滑卡片
            cardCover.translationY = distance
            content.translationY = -distance
            val layoutParams = content.layoutParams
            val scaleX = contentWidth * (1 + (backScale - 1) * (-distance / (cardCover.measuredHeight * scrollFactor)))
            val scaleY = contentHeight * (1 + (backScale - 1) * (-distance / (cardCover.measuredHeight * scrollFactor)))
            layoutParams.width = scaleX.toInt()
            layoutParams.height = scaleY.toInt()
            content.layoutParams = layoutParams
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
        if (expandAnim.listeners == null || !expandAnim.listeners.contains(mAnimator)) {
            expandAnim.addListener(mAnimator)
        }
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
        if (collapseAnim.listeners == null || !collapseAnim.listeners.contains(mAnimator)) {
            collapseAnim.addListener(mAnimator)
        }
        collapseAnim.start()
    }

    fun setExpandStateListener(listener: ExpandStateListener) {
        this.mStateListener = listener
    }

    interface ExpandStateListener {
        fun onStateChanged(state: Int)
    }

    inner class StateAnimatorListener : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            mStateListener?.onStateChanged(cardState)
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationStart(animation: Animator?) {

        }

    }
}