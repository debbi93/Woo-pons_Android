package com.android.woopons.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomHorizontalLayoutManager(context: Context?) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    private var isScrollEnabled = true
    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}