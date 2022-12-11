package com.android.woopons.dashboard.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.android.woopons.R
import com.android.woopons.databinding.LayoutCouponDetailsBinding
import com.android.woopons.databinding.LayoutCouponRemovedBinding
import com.android.woopons.databinding.LayoutLogoutBinding
import com.android.woopons.databinding.LayoutRedeemCouponBinding
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
import com.ncorti.slidetoact.SlideToActView

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
        Glide.with(this).load(Constants.IMAGE_BASE_URL + couponModel?.company_logo).centerCrop()
            .into(binding.ivImage)
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
                couponModel?.rating?.let { rating ->
                    binding.rbGiveRating.rating = rating
                } ?: run {
                    binding.rlRateExperience.visibility = View.VISIBLE
                }
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
                var ratingValue = rating
                if (ratingValue < 3) {
                    ratingValue = 3f
                    binding.rbGiveRating.rating = ratingValue
                }
                setRating(ratingValue)
            }
        }

        binding.cvGetCoupon.setOnClickListener {
            getCoupon()
        }

        binding.tvRemoveCoupon.setOnClickListener {
            removeCoupon()
        }

        binding.unlockCoupon.onSlideCompleteListener =
            object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    binding.unlockCoupon.resetSlider()
                    initUnlockDialog(this@CouponDetailsActivity)
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
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.setFavorite(couponModel?.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                couponModel?.is_favourited = !(couponModel?.is_favourited ?: false)
                setIsFavorite()
                AppUtils.showToast(message ?: "", this@CouponDetailsActivity)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CouponDetailsActivity)
            }
        })
    }

    private fun setRating(rating: Float) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.setCouponRating(couponModel?.id, couponModel?.order_id, rating), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                finish()
                AppUtils.showToast(message ?: "", this@CouponDetailsActivity)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CouponDetailsActivity)
            }
        })
    }

    private fun removeCoupon() {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.removeCoupon(couponModel?.order_id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                initDialog(this@CouponDetailsActivity, false)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CouponDetailsActivity)
            }
        })
    }

    private fun getCoupon() {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.addCoupon(couponModel?.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                initDialog(this@CouponDetailsActivity, true)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CouponDetailsActivity)
            }
        })
    }

    private fun initDialog(context: FragmentActivity, addCoupon: Boolean): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutCouponRemovedBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        if (addCoupon)
            layoutDialog.tvCoupon.text = context.getString(R.string.coupon_added)
        else
            layoutDialog.tvCoupon.text = context.getString(R.string.coupon_removed)

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        return alertDialog
    }

    private fun initUnlockDialog(context: FragmentActivity): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutRedeemCouponBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()


        layoutDialog.cvUnlock.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(context, UnlockCouponActivity::class.java)
            intent.putExtra("couponsModel", couponModel)
            startActivity(intent)
            finish()
        }

        layoutDialog.tvNo.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        return alertDialog
    }
}