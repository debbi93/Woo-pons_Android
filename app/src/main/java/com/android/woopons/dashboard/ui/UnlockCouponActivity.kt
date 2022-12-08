package com.android.woopons.dashboard.ui

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.android.woopons.databinding.ActivityUnlockCouponBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD
import kotlin.Long

class UnlockCouponActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnlockCouponBinding
    private var couponModel: RecentCouponModel? = null
    var kProgressHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnlockCouponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kProgressHUD = AppUtils.getKProgressHUD(this@UnlockCouponActivity)

        couponModel = intent.extras?.get("couponsModel") as? RecentCouponModel

        binding.rlBack.setOnClickListener {
            unlockCoupon()
            finish()
        }

        binding.tvTitle.text = couponModel?.company_name

        binding.tvCoupon.text = couponModel?.coupon_code

        binding.tvHowToUse.text = couponModel?.how_to_use

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "00:" + (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                unlockCoupon()
            }
        }.start()
    }

    override fun onDestroy() {
        unlockCoupon()
        super.onDestroy()
    }

    private fun unlockCoupon() {
        NetworkClass.callApi(URLApi.unlockCoupon(couponModel?.order_id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
            }

            override fun onErrorResponse(error: String?) {
            }
        })

        finish()
    }
}