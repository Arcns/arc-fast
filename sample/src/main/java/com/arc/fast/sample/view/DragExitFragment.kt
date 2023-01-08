package com.arc.fast.sample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.arc.fast.core.extensions.dp
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentDragExitBinding
import com.arc.fast.sample.databinding.ItemTestCardBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DragExitFragment : BaseFragment<FragmentDragExitBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDragExitBinding = FragmentDragExitBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        binding.dragExitLayout.enableDragExit {
            findNavController().navigateUp()
        }
        val data = arrayListOf<String>()
        for (i in 0..100) {
            data.add(i.toString())
        }
//        binding.rv.adapter =
//            object : BaseQuickAdapter<String, BaseViewHolder>(
//                R.layout.item_test_card,
//                data
//            ) {
//
//                override fun createBaseViewHolder(view: View): BaseViewHolder {
//                    ItemTestCardBinding.bind(view)
//                    return super.createBaseViewHolder(view)
//                }
//
//                override fun convert(holder: BaseViewHolder, item: String) {
//                    DataBindingUtil.getBinding<ItemTestCardBinding>(holder.itemView)?.apply {
//                        root.updateLayoutParams { height = LayoutParams.WRAP_CONTENT }
//                        title = item
//                    }
//                }
//            }
    }
}