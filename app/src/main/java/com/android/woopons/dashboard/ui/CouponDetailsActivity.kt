package com.android.woopons.dashboard.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.woopons.R
import com.android.woopons.databinding.LayoutCouponDetailsBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD

class CouponDetailsActivity : AppCompatActivity() {

    private lateinit var binding: LayoutCouponDetailsBinding
    private var couponModel: RecentCouponModel? = null
    var pageType: AppUtils.Companion.Coupons? = null
    var kProgressHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutCouponDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kProgressHUD = AppUtils.getKProgressHUD(this@CouponDetailsActivity)

        couponModel = intent.extras?.get("couponsModel") as? RecentCouponModel

        intent.extras?.getString("couponType")?.let {
            when (it) {
                AppUtils.Companion.Coupons.FAVORITES.name ->
                    pageType = AppUtils.Companion.Coupons.FAVORITES
                AppUtils.Companion.Coupons.CATEGORIES.name ->
                    pageType = AppUtils.Companion.Coupons.CATEGORIES
                AppUtils.Companion.Coupons.TOP_BRANDS.name ->
                    pageType = AppUtils.Companion.Coupons.TOP_BRANDS
                AppUtils.Companion.Coupons.NEWLY_ADDED.name ->
                    pageType = AppUtils.Companion.Coupons.NEWLY_ADDED
                AppUtils.Companion.Coupons.HISTORY.name ->
                    pageType = AppUtils.Companion.Coupons.HISTORY
            }
        }

        binding.tvTitle.text = couponModel?.company_name
        binding.tvOutletName.text = couponModel?.company_name
        Glide.with(this).load(Constants.IMAGE_BASE_URL + couponModel?.company_logo).centerCrop().into(binding.ivImage)
        binding.rbRating.rating = couponModel?.rating_avg ?: 0f
        binding.tvRating.text =
            "${couponModel?.rating_avg ?: 0} (${couponModel?.rating_count ?: 0} ratings)"
        setIsFavorite()
        binding.tvLocation.text = couponModel?.company_location
        binding.tvRepition.text = couponModel?.repetition
        binding.tvUnique.text = couponModel?.offer
        binding.tvAboutDeal.text = couponModel?.about
        binding.tvUseOffer.text = couponModel?.how_to_use

        binding.tvRemoveCoupon.visibility = View.GONE
        binding.unlockCoupon.visibility = View.GONE
        binding.tvNote.visibility = View.GONE
        binding.rlRateExperience.visibility = View.GONE
        binding.cvGetCoupon.visibility = View.GONE

        if (pageType == AppUtils.Companion.Coupons.FAVORITES) {
            binding.ivFavorite.setOnClickListener(null)
        } else {
            binding.ivFavorite.setOnClickListener {
                couponModel?.is_favourited = !(couponModel?.is_favourited ?: false)
                setIsFavorite()
                changeFavorite()
            }
        }

        when (pageType) {
            AppUtils.Companion.Coupons.NEWLY_ADDED -> {
                binding.tvRemoveCoupon.visibility = View.VISIBLE
                binding.unlockCoupon.visibility = View.VISIBLE
                binding.tvNote.visibility = View.VISIBLE
            }
            AppUtils.Companion.Coupons.HISTORY -> {
                binding.rlRateExperience.visibility = View.VISIBLE
            }
            else -> {
                binding.cvGetCoupon.visibility = View.VISIBLE
            }
        }


        binding.rlBack.setOnClickListener {
            finish()
        }

        binding.rbGiveRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                setRating(rating)
            }
        }
    }

    private fun setIsFavorite() {
        if (couponModel?.is_favourited ?: false)
            Glide.with(this).load(R.drawable.ic_heart_filled).into(binding.ivFavorite)
        else
            Glide.with(this).load(R.drawable.ic_heart_unfilled).into(binding.ivFavorite)
    }

    private fun changeFavorite() {
        NetworkClass.callApi(URLApi.setFavorite(couponModel?.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {


            }

            override fun onErrorResponse(error: String?) {

            }
        })
    }

    private fun setRating(rating: Float) {
        NetworkClass.callApi(URLApi.setCouponRating(couponModel?.id, rating), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {


            }

            override fun onErrorResponse(error: String?) {

            }
        })
    }
}