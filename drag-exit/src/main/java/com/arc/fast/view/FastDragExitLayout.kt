package com.arc.fast.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.arc.fast.view.rounded.RoundedConstraintLayout
import kotlin.math.abs
import com.arc.fast.dragexit.R

/**
 * 拖拽退出视图
 */
class FastDragExitLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RoundedConstraintLayout(context, attrs, defStyleAttr) {

    // 是否启用拖拽
    private var enableDragExit = false

    // 支持拖拽退出的方向
    var enableLeftDragExit: Boolean = true
    var enableRightDragExit: Boolean = true
    var enableTopDragExit: Boolean = true
    var enableBottomDragExit: Boolean = true

    // 是否以初始化布局
    private var isInitLayout = false
    private var layoutWidth = 0
    private var layoutHeight = 0

    // 当前触摸坐标
    private var currentTouchX = 0f
    private var currentTouchY = 0f
    private var enableTouch = false

    // 记录拦截触摸坐标，用于判断是否达到拖拽的阙值
    private var startInterceptTouchX = 0f
    private var startInterceptTouchY = 0f
    private var startInterceptTouchTime = 0L

    // 触摸滚动阙值
    private val scaledTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop }
    private var isExceedScaledTouchSlop = false

    // 若判断用户意图不是拖拽退出则不再做拦截
    private var enableInterceptCheck = true

    // 是否正在执行动画
    private var isAnimation = false

    //
    private var bindExitActivity: Activity? = null

    // 默认位置与大小
    private var defaultTop = 0
    private var defaultLeft = 0

    // 当前位置
    private var currentTop = 0
    private var currentLeft = 0

    // 当前缩放比例
    var currentScale = 1f
        private set

    // 缩放计算因子
    private val scaleFactor by lazy {
        (layoutWidth + layoutHeight) * 2.5f
    }

    // 退出因子
    private val exitFactor by lazy {
        layoutWidth * 0.2
    }

    // 是否启用圆角
    override val enableRoundedRadius: Boolean
        get() = enableDragExit && (enableTouch || isAnimation)

    // 拖拽回调
    private var onDragCallback: ((isDrag: Boolean) -> Unit)? = null


    // 退出等待回调（适用于延时操作，您必须确保操作完成后调用continueExit）
    private var onExitWaitCallback: ((currentScale: Float, continueExit: () -> Unit) -> Unit)? =
        null

    // 退出回调
    private var onExitCallback: ((currentScale: Float) -> Unit)? = null

    init {
        if (!isInEditMode) {
            setWillNotDraw(false)
            val typedArray =
                context.theme.obtainStyledAttributes(attrs, R.styleable.FastDragExitLayout, 0, 0)
            try {
                enableLeftDragExit = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableLeftDragExit, true
                )
                enableRightDragExit = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableRightDragExit, true
                )
                enableTopDragExit = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableTopDragExit, true
                )
                enableBottomDragExit = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableBottomDragExit, true
                )
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun callActivityHasFinish() {
        if (enableDragExit) return
        enableDragExit = true
        isAnimation = true
        invalidate()
    }

    fun getDragExitCurrentScale(): Float = currentScale

    fun enableDragExit(
        bindExitActivity: Activity? = null,
        onDragCallback: ((isDrag: Boolean) -> Unit)? = null,
        onExitWaitCallback: ((currentScale: Float, continueExit: () -> Unit) -> Unit)? = null,
        onExitCallback: ((currentScale: Float) -> Unit)? = null
    ) {
        this.enableDragExit = true
        this.bindExitActivity = bindExitActivity
        this.onDragCallback = onDragCallback
        this.onExitWaitCallback = onExitWaitCallback
        this.onExitCallback = onExitCallback
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!enableDragExit || (!enableLeftDragExit && !enableRightDragExit && !enableTopDragExit && !enableBottomDragExit)) {
            return super.onInterceptTouchEvent(event)
        }
        if (!isInitLayout) {
            isInitLayout = true
            layoutWidth = measuredWidth
            layoutHeight = measuredHeight
            updateLayoutParams<MarginLayoutParams> {
                // 获取默认大小
                defaultTop = topMargin
                defaultLeft = leftMargin
                // 固定layout的大小
                width = layoutWidth
                height = layoutHeight
            }
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startInterceptTouchX = event.rawX
                startInterceptTouchY = event.rawY
                startInterceptTouchTime = System.currentTimeMillis()
                enableInterceptCheck = true
                isExceedScaledTouchSlop = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (!enableInterceptCheck) {
                    return super.onInterceptTouchEvent(event)
                }
                return if (onInterceptCheck(event) == InterceptCheckResult.Intercept) true
                else super.onInterceptTouchEvent(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {}
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun onInterceptCheck(event: MotionEvent): InterceptCheckResult {
        val x = event.rawX
        val y = event.rawY
        // 获取手指移动的距离
        val dx = x - startInterceptTouchX
        val dy = y - startInterceptTouchY
        if (abs(dx) > scaledTouchSlop || abs(dy) > scaledTouchSlop) {
            var isUserIntended = true
            // 不是用户意图的滑动
            if ((!enableRightDragExit && dx < 0 && abs(dx) > abs(dy)) ||
                (!enableLeftDragExit && dx > 0 && abs(dx) > abs(dy)) ||
                (!enableTopDragExit && dy > 0 && abs(dy) > abs(dx)) ||
                (!enableBottomDragExit && dy < 0 && abs(dy) > abs(dx))
            ) {
                isUserIntended = false
            }
            if (isUserIntended && !enableTopDragExit && !enableBottomDragExit) {
                val compareDy = abs(dy)
                val compareDx = if (enableLeftDragExit && enableRightDragExit) abs(dx)
                else if (enableLeftDragExit) dx
                else -dx
                if (compareDy > compareDx) {
                    isUserIntended = false
                }
            }
            if (isUserIntended && !enableLeftDragExit && !enableRightDragExit) {
                val compareDx = abs(dx)
                val compareDy = if (enableTopDragExit && enableBottomDragExit) abs(dy)
                else if (enableTopDragExit) -dy
                else dy
                if (compareDx > compareDy) {
                    isUserIntended = false
                }
            }
            if (!isUserIntended) {
                enableInterceptCheck = false
                return InterceptCheckResult.NotIntercept
            }
            if (isExceedScaledTouchSlop) {
//                        if (System.currentTimeMillis() - startInterceptTouchTime > 150) {
                currentTouchX = startInterceptTouchX
                currentTouchY = startInterceptTouchY
                enableTouch = true
                onDragCallback?.invoke(true)
                return InterceptCheckResult.Intercept
//                        }
            } else {
                isExceedScaledTouchSlop = true
            }
        }
        return InterceptCheckResult.Wait
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!enableDragExit) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (enableInterceptCheck) return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!enableTouch && enableInterceptCheck) {
                    val result = onInterceptCheck(event)
                    if (result == InterceptCheckResult.Wait) return true
                    else if (result == InterceptCheckResult.NotIntercept)
                        return super.onTouchEvent(event)
                }
                if (enableTouch) {
                    val x = event.rawX
                    val y = event.rawY
                    // 获取手指移动的距离
                    val dx = x - currentTouchX
                    val dy = y - currentTouchY
                    // 更改当前在窗体的位置
                    updateLayoutParams<MarginLayoutParams> {
                        topMargin += dy.toInt()
                        leftMargin += dx.toInt()
                        currentTop = topMargin
                        currentLeft = leftMargin
                    }
                    // 缩放
                    currentScale =
                        1 - (abs(currentLeft) + abs(currentTop)) / scaleFactor - 0.05f
                    scaleX = currentScale
                    scaleY = currentScale
                    // 获取移动后的位置
                    currentTouchX = x
                    currentTouchY = y
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (enableTouch) {
                    enableTouch = false
                    if (abs(currentTop) > exitFactor || abs(currentLeft) > exitFactor) {
                        // 退出页面
                        isAnimation = true
                        if (onExitWaitCallback != null) {
                            onExitWaitCallback?.invoke(currentScale, this::onExit)
                        } else {
                            onExit()
                        }
                    } else {
                        // 恢复
                        updateLayoutParams<MarginLayoutParams> {
                            topMargin = 0
                            leftMargin = 0
                        }
                        currentScale = 1f
                        scaleX = currentScale
                        scaleY = currentScale
                        onDragCallback?.invoke(false)
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onExit() {
        onExitWaitCallback = null
        onExitCallback?.invoke(currentScale)
        onExitCallback = null
        bindExitActivity?.finishAfterTransition()
        bindExitActivity = null
    }

    private inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
        updateLayoutParams<ViewGroup.LayoutParams>(block)
    }

    @JvmName("updateLayoutParamsTyped")
    private inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(
        block: T.() -> Unit
    ) {
        val params = layoutParams as T
        block(params)
        layoutParams = params
    }
}

enum class InterceptCheckResult {
    NotIntercept, Intercept, Wait
}

