package com.pandaq.mvpdemo.ui.transanim

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.widget.Toast
import com.pandaq.mvpdemo.R
import com.pandaq.mvpdemo.customview.expandingcard.ExpandingCard
import com.pandaq.mvpdemo.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_trans_card.*


/**
 * Created by huxinyu on 2019/1/24.
 * Email : panda.h@foxmail.com
 * Description :
 */
class TransCardActivity : BaseActivity(), ExpandingCard.ExpandStateListener {
    override fun onStateChanged(state: Int) {
        when (state) {
            ExpandingCard.STATE_EXPAND -> {
                Toast.makeText(this, "展开", Toast.LENGTH_SHORT).show()
                startDetail()
            }

            ExpandingCard.STATE_COLLAPSE -> {
                Toast.makeText(this, "折叠", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_card)
        initView()
    }

    override fun onResume() {
        super.onResume()
        val coverView = layoutInflater.inflate(R.layout.layout_card_cover, null)
        val backgroundView = layoutInflater.inflate(R.layout.layout_background, null)
        ecd_item.elevation = -10f
        ecd_item.setContentView(backgroundView)
        ecd_item.setCoverView(coverView)
        ecd_item.setExpandStateListener(this)
    }

    private fun initView() {

        val coverView1 = layoutInflater.inflate(R.layout.layout_card_cover, null)
        val backgroundView1 = layoutInflater.inflate(R.layout.layout_background, null)


        ecd_item1.setContentView(backgroundView1)
        ecd_item1.setCoverView(coverView1)
    }

    private fun startDetail() {
        val intent = Intent(this, DetailInfoActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair<View, String>(findViewById(R.id.card_cover), "imageHeader"),
                Pair<View, String>(findViewById(R.id.fl_content), "textContent"))
        startActivity(intent, options.toBundle())
//        startActivity(intent)
    }
}