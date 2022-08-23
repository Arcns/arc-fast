package com.arc.fast.sample.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Menu(
    var title: String?,
    var url: String?,
    var is_new: Int?
) : Parcelable {
    val isFullScreen: Boolean get() = url?.contains("manage/order/index.php", true) == true
}