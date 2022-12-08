package com.android.woopons.forgotpassword

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.woopons.changepassword.ChangePasswordActivity
import com.android.woopons.databinding.ActivityEnterCodeBinding
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.kaopiz.kprogresshud.KProgressHUD

class EnterCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterCodeBinding
    lateinit var kProgressHUD: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterCodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cvNext.setOnClickListener {
            validateCode()
        }

        binding.rlBack.setOnClickListener {
            finish()
        }
    }

    fun validateCode() {
        val etCode1 = binding.etCode1.text.toString().trim()
        val etCode2 = binding.etCode2.text.toString().trim()
        val etCode3 = binding.etCode3.text.toString().trim()
        val etCode4 = binding.etCode4.text.toString().trim()

        val code = etCode1 + etCode2 + etCode3 + etCode4

        if (code.length < 4) {
            showToast("Enter code", this)
        } else {
            validateCode(code)
        }
    }

    private fun validateCode(code: String) {
        kProgressHUD = AppUtils.getKProgressHUD(this)
        kProgressHUD.show()
        NetworkClass.callApi(URLApi.forgotPassword(code), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD.dismiss()
                startActivity(Intent(this@EnterCodeActivity, ChangePasswordActivity::class.java))
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD.dismiss()
                showToast(error ?: "", this@EnterCodeActivity)
            }
        })
    }
}