package com.android.woopons.models

data class ViewAllModel (
    val total_count: Int?,
    val coupons: List<RecentCouponModel>?,
    val business: List<TopBusinessModel>?,
    val categories: List<CategoryModel>?,
)