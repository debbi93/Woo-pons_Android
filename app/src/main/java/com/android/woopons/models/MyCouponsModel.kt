package com.android.woopons.models

import java.io.Serializable

data class MyCouponsModel(
    val newly_added: List<RecentCouponModel>,
    val history: List<RecentCouponModel>,
    val favorite: List<RecentCouponModel>,
): Serializable