package com.example.linguareader.utils

import android.content.Context

object MetricUtils {
    // Converts a density pixel value to pixels
    fun dpToPx(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
