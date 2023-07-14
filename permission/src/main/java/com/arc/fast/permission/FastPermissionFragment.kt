package com.arc.fast.permission

import android.os.Bundle
import androidx.fragment.app.Fragment

class FastPermissionFragment(val onFast: ((Fragment, FastPermissionUtil) -> Unit)?) :
    Fragment() {
    constructor() : this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onFast?.invoke(this, FastPermissionUtil(this))
    }
}
