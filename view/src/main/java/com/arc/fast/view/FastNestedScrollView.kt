//package com.arc.fast.view
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Log
//import android.view.MotionEvent
//import androidx.core.widget.NestedScrollView
//
///**
// * 解决Banner嵌套滚动事件冲突的兼容性宿主
// * 注意：使用时Banner需要是此宿主布局的直接且唯一的子元素
// */
//open class FastNestedScrollView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : NestedScrollView(context, attrs, defStyleAttr) {
//
//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        when(ev.action){
//            MotionEvent.ACTION_MOVE->{
//                val activePointerIndex = ev.findPointerIndex(mActivePointerId)
//                if (activePointerIndex == -1) {
//                    Log.e(TAG, "Invalid pointerId=$mActivePointerId in onTouchEvent")
//                    break
//                }
//
//                val y = ev.getY(activePointerIndex).toInt()
//                var deltaY = mLastMotionY - y
//                deltaY -= releaseVerticalGlow(deltaY, ev.getX(activePointerIndex))
//                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
//                    val parent = parent
//                    parent?.requestDisallowInterceptTouchEvent(true)
//                    mIsBeingDragged = true
//                    if (deltaY > 0) {
//                        deltaY -= mTouchSlop
//                    } else {
//                        deltaY += mTouchSlop
//                    }
//                }
//            }
//        }
//        return super.onTouchEvent(ev)
//    }
//}