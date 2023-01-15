package com.arc.fast.sample.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arc.fast.sample.R
import com.arc.fast.sample.common.extension.applyFullScreen
import com.arc.fast.sample.common.extension.setLightSystemBar
import com.arc.fast.sample.databinding.ActivityDragExitBinding
import com.arc.fast.sample.databinding.ItemDragExitDataBinding
import com.arc.fast.sample.databinding.ItemDragExitDataHeaderBinding
import com.arc.fast.sample.databinding.ItemDragExitDataHeaderImageBinding
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator
import org.greenrobot.eventbus.EventBus

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
            DragExitDataAdapter(this, this, data)
        // 启用拖拽退出
        binding.dragExitLayout.enableDragExit(bindExitActivity = this)
    }

}

class DragExitDataAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    data: MutableList<String>
) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_drag_exit_data, data) {

    val header by lazy {
        ItemDragExitDataHeaderBinding.inflate(LayoutInflater.from(context)).apply {
            banner.addBannerLifecycleObserver(lifecycleOwner)
            banner.indicator = CircleIndicator(context)
            banner.setAdapter(
                DragExitDataImageAdapter(
                    listOf(
                        R.mipmap.s1,
                        R.mipmap.s2,
                        R.mipmap.s3,
                        R.mipmap.s4,
                    )
                )
            )
        }
    }

    init {

        addHeaderView(header.root)
    }

    override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
        super.onItemViewHolderCreated(viewHolder, viewType)
        if (viewHolder.itemView != header.root)
            ItemDragExitDataBinding.bind(viewHolder.itemView)
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        DataBindingUtil.getBinding<ItemDragExitDataBinding>(holder.itemView)?.apply {
            title = item
        }
    }
}

class DragExitDataImageAdapter(data: List<Int>) :
    BannerAdapter<Int, DragExitDataImageViewHolder>(data) {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): DragExitDataImageViewHolder {
        return DragExitDataImageViewHolder(
            ItemDragExitDataHeaderImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindView(
        holder: DragExitDataImageViewHolder,
        data: Int,
        position: Int,
        size: Int
    ) {
        holder.convert(data)
    }

}

class DragExitDataImageViewHolder(val binding: ItemDragExitDataHeaderImageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun convert(item: Int) {
        binding.ivImage.setImageResource(item)
    }
}