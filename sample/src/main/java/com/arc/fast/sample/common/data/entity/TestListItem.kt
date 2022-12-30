package com.arc.fast.sample.common.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class TestListItem(
    val type: TestListItemType
) {

    class TestTitle(val title: String) :
        TestListItem(
            type = TestListItemType.Title
        )

    class TestPager(val data: MutableList<TestPagerItem>) : TestListItem(
        type = TestListItemType.Pager
    )
}

enum class TestListItemType(val value: Int) {
    Title(1), Pager(2)
}


@Parcelize
class TestPagerItem(
    val tab: String,
    val data: MutableList<String>
) : Parcelable