package com.arc.fast.sample.utils

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

const val SHARED_ELEMENT_APP_NAME = "shared_element_app_name"

class NavTransitionOptions(
    val enter: NavTransition? = NavTransition.FadeThrough,
    val exit: NavTransition? = NavTransition.FadeThrough,
    val isSharedElementsDestination: Boolean = false,
    var sharedElements: HashMap<(root: View) -> View?, String>? = null
) {

    fun addSharedElements(vararg elements: Pair<Int, String>): NavTransitionOptions {
        if (sharedElements == null) sharedElements = HashMap()
        elements.forEach {
            sharedElements?.put({ root: View ->
                root.findViewById(it.first)
            }, it.second)
        }
        return this
    }


    fun addSharedElementViews(vararg elements: Pair<(root: View) -> View?, String?>): NavTransitionOptions {
        if (sharedElements == null) sharedElements = HashMap()
        elements.forEach {
            sharedElements?.put(it.first, it.second ?: return@forEach)
        }
        return this
    }
}

class NavTransitionPresenter(private val options: NavTransitionOptions?) {

    fun setupTransition(fragment: Fragment) {
        if (options == null) return
        // 设置目的地共享元素转换动画
        if (options.isSharedElementsDestination) {
            fragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
                isHoldAtEndEnabled = false
                fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
                scrimColor = Color.TRANSPARENT
                duration = 450
            }
        }
        // 设置进场动画
        when (options.enter) {
            NavTransition.SharedAxisZ -> {
                fragment.enterTransition =
                    MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
                fragment.returnTransition =
                    MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
            }
            NavTransition.ElevationScale -> {
                fragment.enterTransition = MaterialElevationScale(/* growing= */ false)
                fragment.returnTransition = MaterialElevationScale(/* growing= */ true)
            }
            NavTransition.FadeThrough -> {
                fragment.enterTransition = MaterialFadeThrough()
            }
            else -> {
                fragment.enterTransition = null
                fragment.returnTransition = null
            }
        }
        // 设置离场动画
        when (options.exit) {
            NavTransition.SharedAxisZ -> {
                fragment.exitTransition =
                    MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
                fragment.reenterTransition =
                    MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
            }
            NavTransition.ElevationScale -> {
                fragment.exitTransition = MaterialElevationScale(/* growing= */ false)
                fragment.reenterTransition = MaterialElevationScale(/* growing= */ true)
            }
            NavTransition.FadeThrough -> {
                fragment.exitTransition = MaterialFadeThrough()
            }
            else -> {
                fragment.exitTransition = null
                fragment.reenterTransition = null
            }
        }
    }

    fun setupView(root: View) {
        if (options == null) return
        // 使用ElevationScale时，请确保将Fragment的根视图标记为TransitionGroup
        // 这将确保动画作为一个整体应用于 Fragment 视图，而不是单独应用于每个子视图
        if (options.enter == NavTransition.ElevationScale || options.exit == NavTransition.ElevationScale) {
            (root as? ViewGroup)?.isTransitionGroup = true
        }
        // 设置共享元素
        options.sharedElements?.forEach {
            it.key.invoke(root)?.transitionName = it.value
        }
    }

}

enum class NavTransition {
    // Z轴过渡
    SharedAxisZ,

    // 缩放淡化
    ElevationScale,

    // 淡入淡出
    FadeThrough
}