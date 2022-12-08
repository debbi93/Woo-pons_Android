package com.android.woopons.dashboard.ui.termsconditions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.woopons.databinding.ActivityTermsAndConditionsBinding

class TermsAndConditionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsAndConditionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rlBack.setOnClickListener {
            finish()
        }
    }
}