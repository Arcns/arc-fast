package com.arc.fast.sample.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import com.arc.fast.sample.R
import com.arc.fast.sample.common.extension.applyFullScreen
import com.arc.fast.sample.common.extension.setLightSystemBar
import com.arc.fast.sample.databinding.ActivityDragExitBinding
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 */
class DragExitActivity : AppCompatActivity() {

    lateinit var binding: ActivityDragExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyFullScreen()
        setLightSystemBar(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drag_exit)
        binding.toolbar.apply {
            this.navigationIcon = DrawerArrowDrawable(this@DragExitActivity).apply { progress = 1f }
            this.setNavigationOnClickListener {
                finishAfterTransition()
            }
        }
        val data = arrayListOf<String>()
        for (i in 0..100) {
            data.add(i.toString())
        }
        binding.rv.adapter =
            object : BaseQuickAdapter<String, BaseViewHolder>(
                R.layout.item_test_card,
                data
            ) {

                override fun createBaseViewHolder(view: View): BaseViewHolder {
                    ItemTestCardBinding.bind(view)
                    return super.createBaseViewHolder(view)
                }

                override fun convert(holder: BaseViewHolder, item: String) {
                    DataBindingUtil.getBinding<ItemTestCardBinding>(holder.itemView)?.apply {
                        root.updateLayoutParams { height = ViewGroup.LayoutParams.WRAP_CONTENT }
                        title = item
                    }
                }
            }
        // 启用拖拽退出
        binding.dragExitLayout.enableDragExit(bindExitActivity = this)
    }

}