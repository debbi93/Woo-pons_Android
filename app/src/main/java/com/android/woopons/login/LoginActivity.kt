package com.android.woopons.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.woopons.R
import com.android.woopons.dashboard.DashboardActivity
import com.android.woopons.databinding.ActivityLoginBinding
import com.android.woopons.forgotpassword.ForgotPasswordActivity
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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var kProgressHUD: KProgressHUD
    var showPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        binding.cvLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }

        binding.cvLogin.setOnClickListener {
            validateLogin()
        }

        binding.ivShowPassword.setOnClickListener {
            showPassword = !showPassword
            showPassword()
        }

        showPassword()
    }

    private fun showPassword() {
        if (showPassword) {
            binding.ivShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.show
                )
            )
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.ivShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.hide
                )
            )
        }
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun validateLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        if (email.isEmpty()) {
            showToast("Enter email", this)
        } else if (!AppUtils.validEmailPattern(email)) {
            showToast("Enter valid email", this)
        } else if (password.isEmpty()) {
            showToast("Enter password", this)
        } else {
            login(email, password)
        }

    }

    private fun login(email: String, password: String) {
        kProgressHUD = AppUtils.getKProgressHUD(this)
        kProgressHUD.show()
        NetworkClass.callApi(URLApi.login(email, password), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                val json = JSONObject(response ?: "")
                LocalPreference.shared.user =
                    Gson().fromJson(json.toString(), UserDataModel::class.java) ?: UserDataModel()
                LocalPreference.shared.token = LocalPreference.shared.user?.token
                LocalPreference.shared.password = password

                kProgressHUD.dismiss()
                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                finish()
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD.dismiss()
                showToast(error ?: "", this@LoginActivity)
            }
        })
    }
}