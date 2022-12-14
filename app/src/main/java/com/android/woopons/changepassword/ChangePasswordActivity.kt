package com.android.woopons.changepassword

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.woopons.R
import com.android.woopons.databinding.ActivityChangePasswordBinding
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    var kProgressHUD: KProgressHUD? = null
    var showPassword1 = false
    var showPassword2 = false
    var showPassword3 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cvSaveChanges.setOnClickListener {
            checkValidations()
        }

        binding.rlBack.setOnClickListener {
            finish()
        }

        binding.ivShowPassword1.setOnClickListener {
            showPassword1 = !showPassword1
            showPassword(binding.ivShowPassword1, binding.etOldPassword, showPassword1)
        }

        binding.ivShowPassword2.setOnClickListener {
            showPassword2 = !showPassword2
            showPassword(binding.ivShowPassword2, binding.etNewPassword, showPassword2)
        }

        binding.ivShowPassword3.setOnClickListener {
            showPassword3 = !showPassword3
            showPassword(binding.ivShowPassword3, binding.etConfirmPassword, showPassword3)
        }

        showPassword(binding.ivShowPassword1, binding.etOldPassword, showPassword1)
        showPassword(binding.ivShowPassword2, binding.etNewPassword, showPassword2)
        showPassword(binding.ivShowPassword3, binding.etConfirmPassword, showPassword3)
    }

    private fun showPassword(ivShowPassword:ImageView, etPassword: EditText, showPassword: Boolean) {
        if (showPassword) {
            ivShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.show
                )
            )
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            ivShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.hide
                )
            )
        }
        etPassword.setSelection(etPassword.text.length)
    }

    private fun checkValidations() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val cnfrmPassword = binding.etConfirmPassword.text.toString().trim()
        if (oldPassword.isEmpty()) {
            AppUtils.showToast("Please enter old password", this)
        } else if (newPassword.isEmpty()) {
            AppUtils.showToast("Please enter new password", this)
        } else if (cnfrmPassword.isEmpty()) {
            AppUtils.showToast("Please enter confirm password", this)
        } else if (!newPassword.equals(cnfrmPassword)) {
            AppUtils.showToast("New password and confirm password don't match", this)
        } else {
            changePassword(oldPassword, newPassword)
        }

    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        kProgressHUD = AppUtils.getKProgressHUD(this)
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.changePassword(oldPassword, newPassword), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(message, this@ChangePasswordActivity)
                finish()
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@ChangePasswordActivity)
            }
        })
    }
}