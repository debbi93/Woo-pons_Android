package com.android.woopons.forgotpassword

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.woopons.dashboard.DashboardActivity
import com.android.woopons.databinding.ActivityForgotPasswordBinding
import com.android.woopons.login.LoginActivity
import com.android.woopons.models.UserDataModel
import com.android.woopons.network.LocalPreference
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    lateinit var kProgressHUD: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.cvSendCode.setOnClickListener {
            validateEmail()
        }

        binding.rlBack.setOnClickListener {
            finish()
        }
    }

    private fun validateEmail() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            showToast("Enter email", this)
        } else if (!AppUtils.validEmailPattern(email)) {
            showToast("Enter valid email", this)
        } else {
            sendEmail(email)
        }

    }

    private fun sendEmail(email: String) {
        kProgressHUD = AppUtils.getKProgressHUD(this)
        kProgressHUD.show()
        NetworkClass.callApi(URLApi.forgotPassword(email), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD.dismiss()
                showToast(message, this@ForgotPasswordActivity)
                val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD.dismiss()
                showToast(error ?: "", this@ForgotPasswordActivity)
            }
        })
    }

}