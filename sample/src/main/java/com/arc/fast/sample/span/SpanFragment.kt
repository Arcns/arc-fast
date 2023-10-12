package com.arc.fast.sample.span

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arc.fast.core.extensions.dpToPx
import com.arc.fast.core.extensions.resToColor
import com.arc.fast.core.extensions.spToPx
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.R
import com.arc.fast.sample.databinding.FragmentSpanBinding
import com.arc.fast.span.FastTextWrapSpan
import com.arc.fast.span.appendFastImageStyle
import com.arc.fast.span.appendFastSpacing
import com.arc.fast.span.appendFastSpan
import com.arc.fast.span.appendFastTextStyle
import com.arc.fast.span.enableClickableSpan

class SpanFragment : BaseFragment<FragmentSpanBinding>() {

    override val toolbar get() = binding.toolbar

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSpanBinding = FragmentSpanBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.appendFastImageStyle(
            context = requireContext(),
            drawableRes = R.mipmap.ic_launcher_round
        ) {
            width = 20.dpToPx
            height = 20.dpToPx
            paddingRight = 8.dpToPx
            onClick = {
                Toast.makeText(requireContext(), "图标", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        spannableStringBuilder.appendFastSpan(
            "满99元减10元", FastTextWrapSpan(
                radius = 4f.dpToPx,
                borderSize = 1f.dpToPx,
                borderColor = R.color.main.resToColor,
                textSize = 12f.spToPx,
                textColor = R.color.main.resToColor,
                textTypeface = Typeface.create(Typeface.DEFAULT,Typeface.ITALIC),
                textRightMargin = 6f.dpToPx,
                topPadding = 2f.dpToPx,
                bottomPadding = 2f.dpToPx,
                leftPadding = 6f.dpToPx,
                rightPadding = 6f.dpToPx
            )
        )
        spannableStringBuilder.appendFastSpan(
            "满99元减10元", FastTextWrapSpan(
                radius = 4f.dpToPx,
                borderSize = 1f.dpToPx,
                borderColor = R.color.main.resToColor,
                textSize = 12f.spToPx,
                textColor = R.color.main.resToColor,
                textTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                textRightMargin = 6f.dpToPx,
                topPadding = 2f.dpToPx,
                bottomPadding = 2f.dpToPx,
                leftPadding = 6f.dpToPx,
                rightPadding = 6f.dpToPx
            )
        )
        spannableStringBuilder.append("华为平板MatePad 11 平板电脑120Hz高刷全面屏 鸿蒙HarmonyOS 6G+128GB WIFI 曜石灰 WIFI海岛蓝")
        spannableStringBuilder.appendFastSpacing(6.dpToPx)
        spannableStringBuilder.appendFastTextStyle("10月31日-11月3日的订单，预计在2日内发货") {
            textColor = 0xFF999999.toInt()
            textSize = 14.spToPx
//            textStyle = Typeface.BOLD
            underlineColor = Color.TRANSPARENT
            setTextMediumBold()
            onClick = {
                Toast.makeText(
                    requireContext(),
                    "10月31日-11月3日的订单，预计在2日内发货",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        binding.tvTitle.text = spannableStringBuilder
        binding.tvTitle.enableClickableSpan()
    }
}