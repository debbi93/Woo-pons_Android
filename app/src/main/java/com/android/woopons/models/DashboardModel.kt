package com.android.woopons.models

import java.io.Serializable

data class DashboardModel(
    val categories: List<CategoryModel>,
    val coupons: List<RecentCouponModel>,
    val business: List<TopBusinessModel>,
    val trending_categories: List<CategoryModel>
): Serializable

data class CategoryModel(
    val id: Int?,
    val name: String?,
    val image: String?,
    val description: String?,
    val created_at: String?,
    val updated_at: String?
): Serializable

data class RecentCouponModel(
    val order_id: Int?,
    val id: Int?,
    val name: String?,
    val offer: String?,
    val about: String?,
    val repetition: String?,
    val coupon_code: String?,
    val status: Int?,
    val company_name: String?,
    val company_category: String?,
    val company_logo: String?,
    val company_location: String?,
    val rating_count: Int?,
    val rating_avg: Float?,
    var is_favourited: Boolean?,
    val how_to_use: String?
): Serializable

data class TopBusinessModel(
    val id: Int?,
    val user_id: String?,
    val name: String?,
    val email: String?,
    val email_verified: String?,
    val roleId: String?,
    val avatar: String?,
    val phone: String?,
    val address: String?,
    val business_type: String?,
    val business_phone: String?,
    val description: String?,
    val business_status: Int?,
): Serializable