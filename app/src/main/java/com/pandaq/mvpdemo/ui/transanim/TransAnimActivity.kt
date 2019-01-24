package com.pandaq.mvpdemo.ui.transanim

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.view.MotionEvent
import com.pandaq.mvpdemo.R
import com.pandaq.mvpdemo.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_trans_anim.*


/**
 * Created by huxinyu on 2019/1/24.
 * Email : panda.h@foxmail.com
 * Description :
 */
class TransAnimActivity : BaseActivity() {

    var distance = 0f
    var downY = 0f
    var screenWidth = 900f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_anim)
        initView()
    }

    private fun initView() {
        card_cover.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downY = event.rawY
                    distance = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    distance = event.rawY - downY // 上滑距离
                    scrollCover(distance)
                }
                MotionEvent.ACTION_UP -> {
                    if (-distance >= card_cover.measuredHeight * 0.4f) { // 未滑动到触发位置，恢复界面
                        startDetail()
                        distance = 0f
                    }
                }
            }
            true
        }
    }

    private fun startDetail() {
        val intent = Intent(this, DetailInfoActivity::class.java)
        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,
                        card_cover, "imageHeader")
        startActivity(intent, options.toBundle())
    }

    private fun scrollCover(float: Float) {
        println(distance)
        if (-distance < card_cover.measuredHeight * 0.4f) { // 只允许上滑卡片
            card_cover.translationY = float
            card_content.translationY = -float
            val max = screenWidth / card_content.measuredWidth
            card_content.scaleX = 1 - ((max - 1) / (card_cover.measuredHeight * 0.4f)) * float
            card_content.scaleY = 1 - ((max - 1) / (card_cover.measuredHeight * 0.4f)) * float
        }
    }

    override fun onResume() {
        super.onResume()
        scrollCover(0f)
    }

}