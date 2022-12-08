package com.android.woopons.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.woopons.login.LoginActivity
import com.android.woopons.databinding.ActivityOnboardingBinding
import com.android.woopons.utils.CustomHorizontalLayoutManager

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val onBoardingAdapter =  OnBoardingAdapter(this, object : OnBoardingAdapterItemClickListener{
            override fun onNextButtonClick(position: Int) {
                scrollRecycler(position + 1)
            }

            override fun doneButtonClick() {
                startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
                finish()
            }

        })
        binding.recyclerView.layoutManager = CustomHorizontalLayoutManager(this)
        binding.recyclerView.adapter = onBoardingAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.transparentView.visibility = View.GONE
                    setScrolling(false)
                }
            }
        })
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.transparentView.visibility = View.GONE
        setScrolling(false)
    }

    fun scrollRecycler(position: Int) {
        binding.transparentView.visibility  = View.VISIBLE
        setScrolling(true)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.recyclerView.smoothScrollToPosition(position)
        }, 300)
    }

    private fun setScrolling(scroll: Boolean) {
        if (binding.recyclerView.layoutManager != null)
            (binding.recyclerView.layoutManager as CustomHorizontalLayoutManager).setScrollEnabled(scroll)
    }
}