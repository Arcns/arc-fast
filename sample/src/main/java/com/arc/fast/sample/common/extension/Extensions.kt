package com.arc.fast.sample.common.extension

import android.util.Log
import com.arc.fast.sample.BuildConfig
import com.arc.fast.sample.SampleApp


fun LOG(log: String) {
    if (SampleApp.isDebug) Log.e(BuildConfig.APPLICATION_ID, log)
}

