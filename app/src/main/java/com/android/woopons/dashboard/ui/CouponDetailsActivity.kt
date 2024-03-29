package com.android.woopons.dashboard.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.woopons.R
import com.android.woopons.databinding.LayoutActivateCouponAlertBinding
import com.android.woopons.databinding.LayoutCouponDetailsBinding
import com.android.woopons.databinding.LayoutCouponRemovedBinding
import com.android.woopons.databinding.LayoutRedeemCouponBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD
import com.ncorti.slidetoact.SlideToActView
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

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

        binding.tvTitle.text = couponModel?.name
        binding.tvOutletName.text = couponModel?.company_name
        if (couponModel?.company_logo?.isBlank() ?: true) {
            binding.rlNameView.visibility = View.VISIBLE
            binding.tvImageName.text = AppUtils.getAcronyms(couponModel?.company_name)
        } else {
            binding.rlNameView.visibility = View.GONE
            AppUtils.loadImage(this, couponModel?.company_logo, binding.ivImage)
        }
        binding.rbRating.rating = couponModel?.rating_avg ?: 0f
        binding.tvRating.text =
            "${couponModel?.rating_avg ?: 0}"
        setIsFavorite()
        binding.tvLocation.text = couponModel?.company_location
        binding.tvRepition.text = couponModel?.repetition
        binding.tvUnique.text = couponModel?.offer
        binding.tvAboutDeal.text = couponModel?.about
        binding.tvUseOffer.text = couponModel?.how_to_use
        binding.tvAboutBusinessTitle.text = "${getString(R.string.about)} ${couponModel?.company_name}"
        binding.tvAboutBusiness.text = couponModel?.business_description
        binding.tvOperatingTitle.text = "${couponModel?.company_name} ${getString(R.string.has_been_operating)}"
        binding.tvOperating.text = couponModel?.how_long_in_business
        binding.tvLikeToKnowTitle.text = "${couponModel?.company_name} ${getString(R.string.would_like_to_know)}"
        binding.tvLikeToKnow.text = couponModel?.potential_customers_to_know


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

        binding.cvGetDirctions.setOnClickListener {
            couponModel?.latitude?.let { lat ->
                couponModel?.longitude?.let { lon ->
                    val uri =
                        "http://maps.google.com/maps?q=loc:" + lat +"," + lon + " (" + couponModel?.company_name + ")"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(intent)
                }
            }
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
            initActivateDialog(this@CouponDetailsActivity)
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
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_filled)
        else
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_unfilled)
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
                try {
                    response?.let {
                        val jsonObject = JSONObject(it)
                        if (jsonObject.has("data")) {
                            val dict = jsonObject.getJSONObject("data")
                            if (dict.has("plan_type_upgrade") && dict.getBoolean("plan_type_upgrade")) {

                            } else if (dict.has("id")) {
                                val orderId = dict.getInt("id")
                                val intent = Intent(this@CouponDetailsActivity, UnlockCouponActivity::class.java)
                                intent.putExtra("couponsModel", couponModel)
                                intent.putExtra("order_id", orderId)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }catch (e: JSONException) {
                    e.printStackTrace()
                }
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

    private fun initActivateDialog(context: FragmentActivity): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutActivateCouponAlertBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        layoutDialog.tvBack.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.unlockCoupon.onSlideCompleteListener =
            object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    layoutDialog.unlockCoupon.resetSlider()
                    alertDialog.dismiss()
                    getCoupon()
                }
            }

        return alertDialog
    }
}