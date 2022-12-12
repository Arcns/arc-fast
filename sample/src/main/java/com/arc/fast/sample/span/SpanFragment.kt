package com.arc.fast.sample.span

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.arc.fast.core.extensions.color
import com.arc.fast.core.extensions.dp
import com.arc.fast.core.extensions.sp
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentSpanBinding
import com.arc.fast.view.*

class SpanFragment : BaseFragment<FragmentSpanBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSpanBinding = FragmentSpanBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.appendFastImage(
            FastImageSpan(
                requireContext(),
                R.mipmap.ic_launcher_round,
                width = 60.dp,
                height = 60.dp,
                rightMargin = 8.dp
            )
        )
        spannableStringBuilder.appendFastSpan(
            "满99元减10元", FastTextWrapSpan(
                radius = 4f.dp,
                borderSize = 1f.dp,
                borderColor = R.color.main.color,
                textSize = 12f.sp,
                textColor = R.color.main.color,
                textRightMargin = 6f.dp,
                topPadding = 2f.dp,
                bottomPadding = 2f.dp,
                leftPadding = 6f.dp,
                rightPadding = 6f.dp
            )
        )
        spannableStringBuilder.append("华为平板MatePad 11 平板电脑120Hz高刷全面屏 鸿蒙HarmonyOS 6G+128GB WIFI 曜石灰 WIFI海岛蓝")
        spannableStringBuilder.appendFastSpacing(6.dp)
        spannableStringBuilder.appendFastTextStyle("10月31日-11月3日的订单，预计在2日内发货") {
            textColor = 0xFF999999.toInt()
            textSize = 14.sp
            textStyle = Typeface.BOLD
        }
        binding.tvTitle.text = spannableStringBuilder
    }
}