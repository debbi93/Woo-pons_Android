package com.android.woopons.models

data class UserDataModel (
    val id: Int? = 0,
    val name: String? = "",
    val email: String? = "",
    val email_verified: String? = "",
    val roleId: String? = "",
    val token: String? = "",
    val avatar: String? = "",
    val phone: String? = "",
    val address: String? = "",
    val dob: String? = "",
    val sub_status: String? = ""
)