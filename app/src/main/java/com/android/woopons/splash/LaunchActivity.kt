package com.android.woopons.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.android.woopons.login.LoginActivity
import com.android.woopons.R
import com.android.woopons.dashboard.DashboardActivity
import com.android.woopons.network.LocalPreference
import com.android.woopons.onboarding.OnBoardingActivity

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Handler(Looper.getMainLooper()).postDelayed({
            if (LocalPreference.shared.user == null)
                startActivity(Intent(this, OnBoardingActivity::class.java))
            else
                startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        },1000)
    }
}