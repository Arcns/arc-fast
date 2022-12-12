package com.arc.fast.sample.extension

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.arc.fast.immersive.applySystemWindowsInsetsMargin
import com.arc.fast.immersive.applySystemWindowsInsetsPadding
import com.arc.fast.sample.R
import com.arc.fast.sample.data.ApiStatus
import com.arc.fast.sample.data.entity.ApiResult
import com.arc.fast.sample.data.entity.Menu
import com.arc.fast.sample.databinding.ItemMenuBinding
import com.arc.fast.sample.main.MainViewModel
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun applySystemWindowsInsetsPadding(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    view.applySystemWindowsInsetsPadding(applyLeft, applyTop, applyRight, applyBottom)
}


@BindingAdapter(
    "marginLeftSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginRightSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun applySystemWindowsInsetsMargin(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    view.applySystemWindowsInsetsMargin(applyLeft, applyTop, applyRight, applyBottom)
}


@BindingAdapter("enabled")
fun View.setBindingEnabled(value: Boolean) {
    isEnabled = value
}

@BindingAdapter("url")
fun ImageView.setBindingUrl(value: String?) {
    if (value.isNullOrBlank()) setImageDrawable(null)
    Glide.with(this).load(value).into(this)
}

@BindingAdapter("menu")
fun TextView.setBindingMenu(menu: Menu) {
    text = menu.title
}

@BindingAdapter(
    value = [
        "viewModel",
        "data"
    ],
    requireAll = true
)
fun RecyclerView.setBindData(viewModel: MainViewModel, data: ApiResult<List<Menu>>?) {
    if (data?.status != ApiStatus.SUCCESS) return
    if (adapter == null) {
        itemAnimator?.changeDuration = 0;
        adapter = object : BaseQuickAdapter<Menu, BaseViewHolder>(R.layout.item_menu) {
            override fun createBaseViewHolder(view: View): BaseViewHolder {
                ItemMenuBinding.bind(view)
                return super.createBaseViewHolder(view)
            }

            override fun convert(holder: BaseViewHolder, item: Menu) {
                DataBindingUtil.getBinding<ItemMenuBinding>(holder.itemView)?.apply {
                    this.viewModel = viewModel
                    this.item = item
                }
            }
        }
    }
    (adapter as? BaseQuickAdapter<Menu, *>)?.setList(data.data)
}

@BindingAdapter("enterLinkClick")
fun EditText.setBindEnterLinkClick(linkClickView: View) {
    setOnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && linkClickView.isVisible && linkClickView.isEnabled) {
            linkClickView.performClick()
        }
        false
    };
}

