package com.android.woopons.forgotpassword

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.android.woopons.databinding.ActivityForgotPasswordBinding
import com.android.woopons.databinding.LayoutSuccessBinding
import com.android.woopons.login.LoginActivity
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.kaopiz.kprogresshud.KProgressHUD

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
                initDialog(message)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD.dismiss()
                showToast(error ?: "", this@ForgotPasswordActivity)
            }
        })
    }

    private fun initDialog(message: String): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(this@ForgotPasswordActivity)
        alertBuilder.setCancelable(false)
        val layoutDialog =
            LayoutSuccessBinding.inflate(LayoutInflater.from(this@ForgotPasswordActivity))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.tvOk.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        layoutDialog.tvSuccess.text = message


        return alertDialog
    }

}