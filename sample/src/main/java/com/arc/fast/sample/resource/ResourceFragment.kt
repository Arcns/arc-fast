package com.arc.fast.sample.resource

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arc.fast.core.extensions.*
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentResourceBinding
import com.arc.fast.span.FastSpan
import com.arc.fast.span.appendFastTextStyle

class ResourceFragment : BaseFragment<FragmentResourceBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentResourceBinding = FragmentResourceBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContent.text = """（1）dp2px、px2dp
| Float.dpToPx | 把dp转换为px(Float格式) | ${100f.dpToPx} |
| Int.dpToPx | 把dp转换为px(Int格式) | ${100.dpToPx} |
| Float.pxToDp | 把px转换为dp(Float格式) | ${100f.pxToDp} |
| Int.pxToDp | 把px转换为dp(Int格式) | ${100.pxToDp} |

| （2）sp2px、pxToSp
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Float.spToPx | 把sp转换为px(Float格式) | ${100f.spToPx} |
| Int.spToPx | 把sp转换为px(Int格式) | ${100.spToPx} |
| Float.pxToSp | 把px转换为sp(Float格式) | ${100f.pxToSp} |
| Int.pxToSp | 把px转换为sp(Int格式) | ${100.pxToSp} |

（3）获取String资源
| 方法 | 功能 | 用法 |
| ------ | ------ | ------ |
| Int.resToString | 通过StringRes获取String值 | ${R.string.test.resToString} |
| Int.resToStringOrNull | 通过StringRes获取String值，获取失败时返回null | ${R.string.test.resToStringOrNull} |
| Int.resToString(vararg values: Any?) | 通过StringRes获取String值，并替换格式参数 | ${
            R.string.test2.resToString(
                "1",
                "2"
            )
        } |
| Int.resToStringOrNull(vararg values: Any?) | 通过StringRes获取String值，并替换格式参数，获取失败时返回null | ${
            R.string.test2.resToStringOrNull(
                "1",
                "2"
            )
        } |
 
（4）Drawable资源"""
        binding.ivImage1.setImageDrawable(R.mipmap.ic_launcher.resToDrawable)
        binding.ivImage2.setImageDrawable(R.mipmap.ic_launcher.resToDrawableOrNull)
        binding.ivImage3.setImageDrawable(R.mipmap.ic_close.resToDrawable.applyTint(R.color.main.resToColor))
        binding.ivImage4.setImageDrawable(
            R.mipmap.ic_launcher.resToDrawable.applyRipple(
                requireContext(),
                0x24000000
            )
        )
        binding.ivImage4.setOnClickListener { }
        binding.tvContent2.text = FastSpan.createSpan()
            .append("（5）Color资源\n")
            .appendFastTextStyle("Int.resToColor\n") {
                textColor = R.color.main.resToColor
            }.appendFastTextStyle("Int.resToColorOrNull\n") {
                textColor = R.color.main.resToColorOrNull
            }.appendFastTextStyle("String.hexToColor\n") {
                textColor = "#5ED4FC".hexToColor
            }.appendFastTextStyle("String.hexToColorOrNull\n") {
                textColor = "#5ED4FC".hexToColorOrNull
            }.append("colorToHex：${R.color.main.resToColor.colorToHex}\n")
            .append("colorToHexOrNull：${R.color.main.resToColor.colorToHexOrNull}\n")
            .append("lightColorNess：${R.color.main.resToColor.lightColorNess}\n")
            .append("isLightColor：${R.color.main.resToColor.isLightColor}\n\n")
            .append("（6）Dimension资源\n")
            .append("resToDimenValue：${R.dimen.test.resToDimenValue}\n")
            .append("resToDimenValueOrNull：${R.dimen.test.resToDimenValueOrNull}\n\n")
            .append("（7）Attr资源\n")
        binding.ivImage5.setBackgroundResource(requireContext().selectableItemBackgroundRes!!)
        binding.ivImage5.setOnClickListener {  }
        binding.ivImage6.setBackgroundResource(requireContext().selectableItemBackgroundBorderlessRes!!)
        binding.ivImage6.setOnClickListener {  }
        binding.ivImage7.setBackgroundResource(requireContext().actionBarItemBackgroundRes!!)
        binding.ivImage7.setOnClickListener {  }
    }

}