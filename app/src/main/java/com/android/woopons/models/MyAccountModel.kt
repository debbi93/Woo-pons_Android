package com.android.woopons.models

data class MyAccountModel(
    val id: Int? = 0,
    val plan_name: String? = "",
    val stripe_plan: String? = "",
    val status: String? = "",
    val next_billing: String? = "",
    val canceled_at: String? = "",
    val user: UserDataModel?
)