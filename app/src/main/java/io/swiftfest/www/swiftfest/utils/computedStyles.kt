package io.swiftfest.www.swiftfest.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import io.swiftfest.www.swiftfest.R

fun preferredMargin(windowManager: WindowManager, resources: Resources): Int {

    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)

    return when (metrics.densityDpi) {
        DisplayMetrics.DENSITY_HIGH -> resources.getDimension(R.dimen.high_margin).toInt()
        DisplayMetrics.DENSITY_MEDIUM -> resources.getDimension(R.dimen.medium_margin).toInt()
        DisplayMetrics.DENSITY_LOW -> resources.getDimension(R.dimen.small_margin).toInt()
        else -> resources.getDimension(R.dimen.def_margin).toInt()
    }
}