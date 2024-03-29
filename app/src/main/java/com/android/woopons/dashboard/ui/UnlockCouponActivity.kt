package com.android.woopons.dashboard.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.woopons.databinding.ActivityUnlockCouponBinding
import com.android.woopons.databinding.LayoutCouponExpireAlertBinding
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
    private var orderId:Int = 0
    var kProgressHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityUnlockCouponBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kProgressHUD = AppUtils.getKProgressHUD(this@UnlockCouponActivity)

        couponModel = intent.extras?.get("couponsModel") as? RecentCouponModel
        orderId = intent.extras?.getInt("order_id", 0) ?: 0

        unlockCoupon()

        binding.rlBack.setOnClickListener {
            initDialog()
        }

        binding.tvTitle.text = couponModel?.name

        binding.tvCoupon.text = couponModel?.coupon_code

        binding.tvHowToUse.text = couponModel?.business_description

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = (millisUntilFinished / 1000)
                if (time < 10) {
                    binding.tvTimer.text = "0:0" + time.toString()
                }else {
                    binding.tvTimer.text = "0:" + time.toString()
                }
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    override fun onBackPressed() {
        initDialog()
    }

    private fun unlockCoupon() {
        NetworkClass.callApi(URLApi.unlockCoupon(orderId), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
            }

            override fun onErrorResponse(error: String?) {
            }
        })

    }

    private fun initDialog(): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(this@UnlockCouponActivity)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutCouponExpireAlertBinding.inflate(LayoutInflater.from(this@UnlockCouponActivity))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.cvOk.setOnClickListener {
            finish()
        }

        return alertDialog
    }
}