package com.pandaq.mvpdemo.ui.transanim

import android.os.Bundle
import com.pandaq.mvpdemo.R
import com.pandaq.mvpdemo.ui.BaseActivity


/**
 * Created by huxinyu on 2019/1/24.
 * Email : panda.h@foxmail.com
 * Description :
 */
class DetailInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)
        // 延迟共享动画的执行
//        postponeEnterTransition()
    }

    override fun onBackPressed() {
        finishAfterTransition()
//        supportFinishAfterTransition()
    }
}