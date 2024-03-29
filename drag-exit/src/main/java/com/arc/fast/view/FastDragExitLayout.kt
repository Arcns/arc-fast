package com.arc.fast.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.arc.fast.dragexit.R
import com.arc.fast.view.rounded.RoundedConstraintLayout
import kotlin.math.abs

/**
 * 拖拽退出视图
 */
class FastDragExitLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RoundedConstraintLayout(context, attrs, defStyleAttr) {

    // 是否启用拖拽
    private var enableDragExit = false

    // 支持拖拽退出的方向
    var enableLeftDragExit = true
    var enableRightDragExit = true
    var enableTopDragExit = true
    var enableBottomDragExit = true

    // 是否支持横向拖拽移动
    var enableDragHorizontal = true

    // 是否支持纵向拖拽移动
    var enableDragVertical = true

    // 是否支持拖拽缩放
    var enableDragScale = true

    // 拖拽缩放的参照物
    var dragScaleReference = DragScaleReference.All

    // 拖拽缩放因子（该值越小，缩放效果越明显）
    // 缩放计算公式为：scale=1-abs(reference)/referenceMax*dragScaleFactor
    var dragScaleFactor = 1f

    // 拖拽时缩放的保留比例值，如果有设置该值，那么在拖拽时缩放比例会叠加该值
    // 用于强调首次触发拖拽缩放时的效果感
    var dragScaleReserve = 0f

    // 拖拽达到退出的距离（拖拽超过该距离回执行退出操作，未达到该距离则会恢复）
    var dragExitDistance = DEFAULT_DRAG_EXIT_DISTANCE
        get() {
            if (field == DEFAULT_DRAG_EXIT_DISTANCE && layoutWidth > 0 && layoutHeight > 0) {
                field = if (!enableDragHorizontal) layoutHeight * 0.2f else layoutWidth * 0.2f
            }
            return field
        }

    // 拖拽开始的位置 0从按下的位置开始 1从首次可以移动的位置开始
    var dragStartPosition = DragStartPosition.FirstMove

    // 拖拽为达到离开距离时的恢复动画时长（毫秒）
    var dragesumeDuration = 100L

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

    // 拖拽恢复动画
    private val dragResumeAnimator by lazy {
        FastDragResumeAnimator(this) {
            isAnimation = false
            currentScale = 1f
            onDragCallback?.onEnd(false)
        }
    }

    // 是否启用圆角
    override val enableRoundedRadius: Boolean
        get() = enableDragExit && (enableTouch || isAnimation)

    // 拖拽回调
    private var onDragCallback: OnDragCallback? = null

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
                enableDragScale = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableDragScale, true
                )
                dragScaleFactor = typedArray.getFloat(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragScaleFactor, 1f
                )
                dragScaleReserve = typedArray.getFloat(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragScaleReserve, 0f
                )
                dragExitDistance = typedArray.getFloat(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragExitDistance,
                    DEFAULT_DRAG_EXIT_DISTANCE
                )
                enableDragHorizontal = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableDragHorizontal, true
                )
                enableDragVertical = typedArray.getBoolean(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_enableDragVertical, true
                )
                dragStartPosition = typedArray.getInt(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragStartPosition,
                    DragStartPosition.FirstMove.value
                ).let { position ->
                    DragStartPosition.values().firstOrNull { it.value == position }
                        ?: DragStartPosition.FirstMove
                }
                dragesumeDuration = typedArray.getInt(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragResumeDuration,
                    100
                ).toLong()
                dragScaleReference = typedArray.getInt(
                    R.styleable.FastDragExitLayout_fastDragExitLayout_dragScaleReference,
                    DragScaleReference.All.value
                ).let { reference ->
                    DragScaleReference.values().firstOrNull { it.value == reference }
                        ?: DragScaleReference.All
                }
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
        onDragCallback: OnDragCallback? = null,
        onExitWaitCallback: ((currentScale: Float, continueExit: () -> Unit) -> Unit)? = null,
        onExitCallback: ((currentScale: Float) -> Unit)? = null
    ) {
        this.enableDragExit = true
        this.bindExitActivity = bindExitActivity
        this.onDragCallback = onDragCallback
        this.onExitWaitCallback = onExitWaitCallback
        this.onExitCallback = onExitCallback
    }

    private fun onFixInterceptTouchEvent(event: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(event)
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!enableDragExit || (!enableLeftDragExit && !enableRightDragExit && !enableTopDragExit && !enableBottomDragExit)) {
            return onFixInterceptTouchEvent(event)
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
                    return onFixInterceptTouchEvent(event)
                }
                return if (onInterceptCheck(event) == InterceptCheckResult.Intercept) true
                else onFixInterceptTouchEvent(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {}
        }
        return onFixInterceptTouchEvent(event)
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
                if (dragStartPosition == DragStartPosition.Down) {
                    currentTouchX = startInterceptTouchX
                    currentTouchY = startInterceptTouchY
                } else {
                    currentTouchX = event.rawX
                    currentTouchY = event.rawY
                }
                enableTouch = true
                onDragCallback?.onStart()
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
                        if (enableDragHorizontal) {
                            leftMargin += dx.toInt()
                            currentLeft = leftMargin
                        }
                        if (enableDragVertical) {
                            topMargin += dy.toInt()
                            currentTop = topMargin
                        }
                    }
                    // 缩放
                    if (enableDragScale && dragScaleFactor > 0) {
                        // 缩放计算公式为：scale=1-abs(reference)/referenceMax*dragScaleFactor
                        currentScale = if (dragScaleReference == DragScaleReference.X) {
                            1 - abs(currentLeft) / (layoutWidth * dragScaleFactor)
                        } else if (dragScaleReference == DragScaleReference.Y) {
                            1 - abs(currentTop) / (layoutHeight * dragScaleFactor)
                        } else {
                            1 - (abs(currentLeft) + abs(currentTop)) / ((layoutWidth + layoutHeight) * dragScaleFactor)
                        } - dragScaleReserve //0.05f
                        scaleX = currentScale
                        scaleY = currentScale
                    }
                    onDragCallback?.onDrag(currentLeft, currentTop, currentScale)
                    // 获取移动后的位置
                    currentTouchX = x
                    currentTouchY = y
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (enableTouch) {
                    enableTouch = false
                    if (abs(currentTop) > dragExitDistance || abs(currentLeft) > dragExitDistance) {
                        // 退出页面
                        isAnimation = true
                        if (onExitWaitCallback != null) {
                            onExitWaitCallback?.invoke(currentScale, this::onExit)
                        } else {
                            onExit()
                        }
                    } else {
                        // 恢复
                        isAnimation = true
                        dragResumeAnimator.resume(dragesumeDuration)
                    }
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onExit() {
        onDragCallback?.onEnd(true)
        onExitWaitCallback = null
        onExitCallback?.invoke(currentScale)
        onExitCallback = null
        bindExitActivity?.finishAfterTransition()
        bindExitActivity = null
    }

    interface OnDragCallback {
        fun onStart() {}
        fun onEnd(isExit: Boolean) {}
        fun onDrag(left: Int, top: Int, scale: Float) {}
    }

    companion object {
        const val DEFAULT_DRAG_EXIT_DISTANCE = -1f
    }
}

enum class InterceptCheckResult {
    NotIntercept, Intercept, Wait
}

// 拖拽开始的位置  1从首次可以移动的位置开始
enum class DragStartPosition(val value: Int) {
    // 0从按下的位置开始
    Down(0),

    // 1从首次可以移动的位置开始
    FirstMove(1)
}

enum class DragScaleReference(val value: Int) {
    X(0), Y(1), All(2)
}
