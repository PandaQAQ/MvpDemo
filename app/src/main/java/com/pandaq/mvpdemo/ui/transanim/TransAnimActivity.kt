package com.pandaq.mvpdemo.ui.transanim

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation
import android.support.v4.util.Pair
import android.view.MotionEvent
import android.view.View
import com.pandaq.mvpdemo.R
import com.pandaq.mvpdemo.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_trans_anim.*


/**
 * Created by huxinyu on 2019/1/24.
 * Email : panda.h@foxmail.com
 * Description :
 */
class TransAnimActivity : BaseActivity() {

    private val STATE_COLLAPSE = 0
    private val STATE_EXPAND = 1
    private var viewPause = false

    private var distance = 0f
    private var downY = 0f
    // 覆盖View Y 轴初始偏移量
    private var initCoverTransY = 0f
    private lateinit var evaluator: FloatEvaluator
    private lateinit var collapseAnim: ValueAnimator
    private lateinit var expandAnim: ValueAnimator
    private var cardState = STATE_COLLAPSE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_anim)
        initView()
    }

    private fun initView() {
        evaluator = FloatEvaluator()
        collapseAnim = ValueAnimator.ofFloat(0.0f, 1f)
        collapseAnim.duration = 200
        expandAnim = ValueAnimator.ofFloat(0.0f, 1f)
        expandAnim.duration = 200

        card_cover.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewPause = false
                    collapseAnim.cancel()
                    expandAnim.cancel()
                    downY = event.rawY
                    initCoverTransY = card_cover.translationY
                }
                MotionEvent.ACTION_MOVE -> {
                    distance = event.rawY - downY + initCoverTransY // 上滑距离
                    if (-distance > card_cover.measuredHeight * 0.18f) {
                        distance = -card_cover.measuredHeight * 0.18f
                    }
                    if (distance > 0) {
                        distance = 0f
                    }
                    if (cardState != STATE_EXPAND || distance > -card_cover.measuredHeight * 0.18f) { // 展开状态拉到顶继续上拉
                        scrollCover(distance)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (cardState == STATE_COLLAPSE) { //当前还未到达展开状态
                        if (-distance >= card_cover.measuredHeight * 0.18f / 2) {
                            cardState = STATE_EXPAND
                            // 动画回到展开状态
                            expand()
                        } else {
                            // 动画回到折叠状态
                            collapse()
                        }
                    } else { //当前还未到达关闭状态
                        if (-distance < card_cover.measuredHeight * 0.18f / 2) { // 下滑超过一半折叠
                            cardState = STATE_COLLAPSE
                            // 动画回到折叠状态
                            collapse()
                        } else {
                            if (distance <= -card_cover.measuredHeight * 0.18f) {
                                startDetail()
                            } else {
                                // 动画回到展开状态
                                expand()
                            }
                        }
                    }
                }
            }
            true
        }
    }

    private fun startDetail() {
        viewPause = true
        val intent = Intent(this, DetailInfoActivity::class.java)
        val options = makeSceneTransitionAnimation(this,
                Pair<View, String>(findViewById(R.id.card_cover), "imageHeader"),
                Pair<View, String>(findViewById(R.id.card_content), "textContent"))
        startActivity(intent, options.toBundle())
    }

    private fun scrollCover(distance: Float) {
        if (distance <= 0 && -distance < card_cover.measuredHeight * 0.18f) { // 只允许上滑卡片
            card_cover.translationY = distance
            card_content.translationY = -distance
            card_content.scaleX = 1 + 0.4f * (-distance / (card_cover.measuredHeight * 0.18f)) //最大扩大到 1.2 倍
            card_content.scaleY = 1 + 0.4f * (-distance / (card_cover.measuredHeight * 0.18f))
        }
    }

    private fun expand() {
        val currentTransY = card_cover.translationY
        expandAnim.addUpdateListener { animation ->
            val float = animation.animatedValue as Float
            distance = evaluator.evaluate(float, currentTransY,
                    -card_cover.measuredHeight * 0.18f)
            scrollCover(distance)
        }
        scrollCover(-card_cover.measuredHeight * 0.18f)
        expandAnim.start()
    }

    private fun collapse() {
        val currentTransY = card_cover.translationY
        collapseAnim.addUpdateListener { animation ->
            val float = animation.animatedValue as Float
            val distance = evaluator.evaluate(float, currentTransY,
                    0)
            scrollCover(distance)
        }
        collapseAnim.start()
    }

}