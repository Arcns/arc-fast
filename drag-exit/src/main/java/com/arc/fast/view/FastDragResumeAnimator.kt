package com.arc.fast.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import android.view.ViewGroup

class FastDragResumeAnimator(
    val fastDragExitLayout: FastDragExitLayout,
    val onResumeEnd: () -> Unit
) {
    private val marginLast = 0f
    private val scaleLast = 1f
    private var topMarginFirst = 0f
    private var topMarginDiffer = 0f
    private var leftMarginFirst = 0f
    private var leftMarginDiffer = 0f
    private var scaleFirst = 0f
    private var scaleDiffer = 0f
    private val animator by lazy {
        ObjectAnimator.ofFloat(
            fastDragExitLayout,
            object : Property<View, Float>(Float::class.java, this::class.java.name) {
                override fun get(view: View): Float = 0f
                override fun set(view: View, progress: Float) {
                    //  把计算好的值设置到view中
                    fastDragExitLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        topMargin = calculatorFloatValue(
                            topMarginFirst,
                            marginLast,
                            topMarginDiffer,
                            progress
                        ).toInt()
                        leftMargin = calculatorFloatValue(
                            leftMarginFirst,
                            marginLast,
                            leftMarginDiffer,
                            progress
                        ).toInt()
                    }
                    val scale = calculatorFloatValue(
                        scaleFirst,
                        scaleLast,
                        scaleDiffer,
                        progress
                    )
                    fastDragExitLayout.scaleX = scale
                    fastDragExitLayout.scaleY = scale
                }
            },
            0f,
            1f
        ).apply {
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    onResumeEnd.invoke()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    fun resume(animatorDuration: Long = 100) {
        if (animatorDuration <= 0) {
            fastDragExitLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = marginLast.toInt()
                leftMargin = marginLast.toInt()
            }
            fastDragExitLayout.scaleX = scaleLast
            fastDragExitLayout.scaleY = scaleLast
            onResumeEnd.invoke()
        } else {
            topMarginFirst = fastDragExitLayout.marginTop.toFloat()
            topMarginDiffer = marginLast - topMarginFirst
            leftMarginFirst = fastDragExitLayout.marginLeft.toFloat()
            leftMarginDiffer = marginLast - leftMarginFirst
            scaleFirst = fastDragExitLayout.currentScale
            scaleDiffer = scaleLast - scaleFirst
            animator.setDuration(animatorDuration).start()
        }
    }

    private fun calculatorFloatValue(
        first: Float,
        last: Float,
        differ: Float,
        progress: Float
    ): Float {
        if (first == last) return first
        val isPositiveFirst = first > last
        return (first + differ * progress).let {
            if (isPositiveFirst) {
                if (it > first) first else if (it < last) last else it
            } else {
                if (it > last) last else if (it < first) first else it
            }
        }
    }
}