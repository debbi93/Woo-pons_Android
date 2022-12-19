package com.android.woopons.changepassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.woopons.databinding.ActivityChangePasswordBinding
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    var kProgressHUD: KProgressHUD? = null

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