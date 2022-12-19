package com.android.woopons.dashboard.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.login.LoginActivity
import com.android.woopons.R
import com.android.woopons.dashboard.ui.feedback.FeedbackActivity
import com.android.woopons.dashboard.ui.privacypolicy.PrivacyPolicyActivity
import com.android.woopons.dashboard.ui.profile.ProfileActivity
import com.android.woopons.dashboard.ui.termsconditions.TermsAndConditionsActivity
import com.android.woopons.databinding.FragmentSettingsBinding
import com.android.woopons.databinding.LayoutLogoutBinding
import com.android.woopons.BuildConfig
import com.android.woopons.changepassword.ChangePasswordActivity
import com.android.woopons.dashboard.ui.CouponsListActivity
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.kaopiz.kprogresshud.KProgressHUD

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    var kProgressHUD: KProgressHUD? = null

//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { context ->
            val settingsAdapter = SettingsAdapter(context, object : SettingsItemClickListener {
                override fun onItemClick(position: Int) {
                    when (position) {
                        0 -> {
                            startActivity(Intent(context, ProfileActivity::class.java))
                        }
                        1 -> {
                            startActivity(Intent(context, ChangePasswordActivity::class.java))
                        }
                        2 -> {
                            val intent = Intent(context, CouponsListActivity::class.java)
                            intent.putExtra("couponsListType", AppUtils.Companion.Coupons.FAVORITES.name)
                            startActivity(intent)
                        }
                        3 -> {
                            startActivity(Intent(context, FeedbackActivity::class.java))
                        }
                        4 -> {
                            startActivity(Intent(context, TermsAndConditionsActivity::class.java))
                        }
                        5 -> {
                            startActivity(Intent(context, PrivacyPolicyActivity::class.java))
                        }
                        6 -> {
                            initDialog(context)
                        }
                        else -> {
                            Toast.makeText(context, "${position}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }


            })
            binding.rvSettings.layoutManager = LinearLayoutManager(context)
            binding.rvSettings.adapter = settingsAdapter

            binding.tvVersion.text = "${context.getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    private fun initDialog(context: FragmentActivity): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutLogoutBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()
        layoutDialog.tvNo.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.cvLogut.setOnClickListener {
            alertDialog.dismiss()
            logout(context)
        }
        return alertDialog
    }

    private fun logout(context: FragmentActivity) {
        kProgressHUD = AppUtils.getKProgressHUD(context)
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.logout(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                AppUtils.clearCache()
                startActivity(Intent(context, LoginActivity::class.java))
                context.finish()
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", context)
            }
        })
    }
}