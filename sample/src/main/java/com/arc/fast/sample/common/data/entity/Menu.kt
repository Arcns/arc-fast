package com.arc.fast.sample.common.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Menu(
    var title: String?,
    var subTitle: String? = null,
    var url: String? = null,
    var is_new: Int? = 0
) : Parcelable {
    val isFullScreen: Boolean get() = url?.contains("manage/order/index.php", true) == true
}