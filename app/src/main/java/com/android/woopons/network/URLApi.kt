package com.android.woopons.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.File


object URLApi {
    private val TAG = URLApi::class.java.toString()


    private const val BaseUrl =
        "https://woopons.xcelanceweb.com/api/v1/" //URL here
    private var path: String = ""
    private var params: JSONObject = JSONObject()
    var method: NetworkMethod = NetworkMethod.GET
    final fun link(): String {
        return BaseUrl + path
    }

    final fun param(): JSONObject {
        return params
    }

    final fun paramHas(): HashMap<String, Any>? {
        return Gson().fromJson<HashMap<String, Any>>(
            param().toString(), object : TypeToken<HashMap<String?, Any?>?>() {}.type
        )
    }

    fun login(email: String, password: String): URLApi {

        method = NetworkMethod.POST
        path = "auth/login"
        params = JSONObject()
        params.put("email", email)
        params.put("password", password)
        params.put("mobile", true)
        return this
    }

    fun getAccount(): URLApi {

        method = NetworkMethod.GET
        path = "user/myaccount"
        params = JSONObject()
        return this
    }

    fun logout(): URLApi {
        method = NetworkMethod.POST
        path = "logout"
        params = JSONObject()
        return this
    }

    fun updateProfile(
        name: String?, phone: String?,
        avatar: File? = null
    ): URLApi {

        method = NetworkMethod.POST
        path = "updateprofile"
        params = JSONObject()

        this.params.put("name", name)
        this.params.put("phone", phone)
        this.params.put("avatar", avatar)

        return this
    }

    fun forgotPassword(email: String): URLApi {
        method = NetworkMethod.POST
        path = "auth/forgotpassword"
        params = JSONObject()

        this.params.put("email", email)
        return this
    }

    fun getCategories(): URLApi {

        method = NetworkMethod.GET
        path = "allcategories"
        params = JSONObject()
        return this
    }

    fun getDashboardData(): URLApi {

        method = NetworkMethod.GET
        path = "getdashboardata"
        params = JSONObject()
        return this
    }

    fun getAllCoupons(page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "getallcoupons?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getTopBusiness(page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "topratedbusiness?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getTopCategory(page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "topratedcategory?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getFavorites(page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "getfavoriteslist?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getCategoryCoupons(categoryId: Int, page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "getcoupons/category/${categoryId}?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getBrandsCoupons(brandId: Int, page: Int = 1, limit: Int = 20): URLApi {

        method = NetworkMethod.GET
        path = "getcoupons/business/${brandId}?page=${page}&limit=${limit}"
        params = JSONObject()
        return this
    }

    fun getMyCoupons(): URLApi {

        method = NetworkMethod.GET
        path = "getmycoupons"
        params = JSONObject()

        return this
    }

    fun setFeedback(feedback: String): URLApi {
        method = NetworkMethod.POST
        path = "user/feedback"
        params = JSONObject()

        this.params.put("feedback", feedback)
        return this
    }

    fun search(search: String): URLApi {
        method = NetworkMethod.POST
        path = "searchanything"
        params = JSONObject()

        this.params.put("search", search)
        return this
    }

    fun setFavorite(couponId: Int?): URLApi {
        method = NetworkMethod.POST
        path = "toggleFavoriteCoupon"
        params = JSONObject()

        this.params.put("coupon_id", couponId)
        return this
    }

    fun setCouponRating(couponId: Int?, orderId: Int?, rating: Float): URLApi {
        method = NetworkMethod.POST
        path = "addreview"
        params = JSONObject()

        this.params.put("coupon_id", couponId)
        this.params.put("order_id", orderId)
        this.params.put("rating", rating)
        return this
    }

    fun unlockCoupon(couponId: Int?): URLApi {
        method = NetworkMethod.POST
        path = "unlockcoupon"
        params = JSONObject()

        this.params.put("order_id", couponId)
        return this
    }

    fun removeCoupon(couponId: Int?): URLApi {
        method = NetworkMethod.POST
        path = "removecoupon"
        params = JSONObject()

        this.params.put("order_id", couponId)
        return this
    }

    fun addCoupon(couponId: Int?): URLApi {
        method = NetworkMethod.POST
        path = "addcouponorder"
        params = JSONObject()

        this.params.put("coupon_id", couponId)
        return this
    }
}

