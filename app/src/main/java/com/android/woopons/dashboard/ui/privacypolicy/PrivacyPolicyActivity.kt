package com.android.woopons.dashboard.ui.privacypolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.woopons.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rlBack.setOnClickListener {
            finish()
        }
    }
}