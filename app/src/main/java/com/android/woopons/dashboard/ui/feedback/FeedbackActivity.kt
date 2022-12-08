package com.android.woopons.dashboard.ui.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.woopons.databinding.ActivityFeedbackBinding
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    var kProgressHUD: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rlBack.setOnClickListener {
            finish()
        }

        binding.cvSubmit.setOnClickListener {
            validateFeedback()
        }
    }

    private fun validateFeedback() {
        val feedback = binding.etFeedback.text.toString().trim()
        if (feedback.isEmpty()) {
            AppUtils.showToast("Enter feedback", this)
        } else {
            submitFeedback(feedback)
        }

    }

    private fun submitFeedback(feedback: String) {
        kProgressHUD = AppUtils.getKProgressHUD(this)
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.setFeedback(feedback), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                AppUtils.showToast("Your feedback submitted", this@FeedbackActivity)
                finish()


            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@FeedbackActivity)
            }
        })
    }
}