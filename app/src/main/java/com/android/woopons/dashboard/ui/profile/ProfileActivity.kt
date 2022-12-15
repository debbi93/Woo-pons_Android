package com.android.woopons.dashboard.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.woopons.R
import com.android.woopons.dashboard.ui.upgradeplan.UpgradePlanActivity
import com.android.woopons.databinding.ActivityProfileBinding
import com.android.woopons.models.MyAccountModel
import com.android.woopons.models.UserDataModel
import com.android.woopons.network.LocalPreference
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils.getRealPath
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.File


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    var kProgressHUD: KProgressHUD? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                data?.data?.let { fileUri ->
                    getRealPath(this@ProfileActivity, fileUri)?.let { path ->
                        val file: File = File(path)
                        Glide.with(binding.ivProfile)
                            .load(fileUri)
                            .into(binding.ivProfile)
                        addProfileImage(file)
                    }
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showToast(ImagePicker.getError(data), this@ProfileActivity)

            } else {
                showToast("Task Cancelled", this@ProfileActivity)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        kProgressHUD = AppUtils.getKProgressHUD(this)

        binding.rlBack.setOnClickListener {
            finish()
        }

        binding.tvSave.setOnClickListener {
            validateProfile()
        }

        binding.tvUplaodImage.setOnClickListener {

            ImagePicker.with(this@ProfileActivity)
                .crop()
                .saveDir(this.filesDir)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.cvUpgradePlan.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, UpgradePlanActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        getMyAccount()
    }

    private fun getMyAccount() {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.getAccount(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    LocalPreference.shared.myAccount =
                        Gson().fromJson(it, MyAccountModel::class.java)

                    setAccountData()
                }
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                showToast(error ?: "", this@ProfileActivity)
            }
        })
    }

    private fun setAccountData() {
        Glide.with(this).load(Constants.IMAGE_BASE_URL + LocalPreference.shared.myAccount?.user?.avatar).centerInside().placeholder(R.drawable.ic_placeholder)
            .into(binding.ivProfile)

        binding.etFullName.setText(LocalPreference.shared.myAccount?.user?.name)

        binding.etEmail.setText(LocalPreference.shared.myAccount?.user?.email)

        binding.etPhone.setText(LocalPreference.shared.myAccount?.user?.phone)

        binding.tvStatus.text = LocalPreference.shared.myAccount?.status

        binding.tvPlan.text = LocalPreference.shared.myAccount?.plan_name

        binding.tvNextBilling.text =
            getString(R.string.next_billing_date) + " ${LocalPreference.shared.myAccount?.next_billing}"
    }

    private fun validateProfile() {
        val name = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        if (name.isEmpty()) {
            showToast("Enter name", this)
        } else if (phone.isEmpty()) {
            showToast("Enter phone number", this)
        } else {
            saveChanges(name, phone)
        }

    }

    private fun saveChanges(name: String, phone: String) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.updateProfile(name, phone), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                response?.let {
                    LocalPreference.shared.user =
                        Gson().fromJson(it, UserDataModel::class.java)
                }

                kProgressHUD?.dismiss()
                showToast("Profile updated successfully", this@ProfileActivity)
                finish()
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                showToast(error ?: "", this@ProfileActivity)
            }
        })
    }

    private fun addProfileImage(image: File) {
        kProgressHUD?.show()
        NetworkClass.callFileUpload(
            URLApi.updateProfile(null, null, image),
            image,
            "avatar",
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    response?.let {
                        LocalPreference.shared.user =
                            Gson().fromJson(it, UserDataModel::class.java)
                    }
                    kProgressHUD?.dismiss()
                }

                override fun onErrorResponse(error: String?) {
                    kProgressHUD?.dismiss()
                    setAccountData()
                    showToast(error ?: "", this@ProfileActivity)
                }
            })
    }
}