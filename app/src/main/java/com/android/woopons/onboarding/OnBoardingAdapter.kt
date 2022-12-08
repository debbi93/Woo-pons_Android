package com.android.woopons.onboarding

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.woopons.R
import com.android.woopons.databinding.LayoutOnboardingBinding
import com.android.woopons.utils.AppUtils

interface OnBoardingAdapterItemClickListener {
    fun onNextButtonClick(position: Int)

    fun doneButtonClick()
}

class OnBoardingAdapter(var mContext: Context, var onItemClickListener: OnBoardingAdapterItemClickListener) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    val pageSize = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding = LayoutOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return pageSize
    }

    inner class OnBoardingViewHolder(itemView: LayoutOnboardingBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val bgImage: ImageView = itemView.bgImage
        private val tvTop: TextView = itemView.tvTop
        private val tvBottom: TextView = itemView.tvBottom
        private val llDots: LinearLayout = itemView.llDots
        private val cvNext: CardView = itemView.cvNext


        fun bind(position: Int) {
            doDots(position)

            cvNext.setOnClickListener {
                if (position < pageSize - 1){
                    onItemClickListener.onNextButtonClick(position)
                } else {
                    onItemClickListener.doneButtonClick()
                }
            }

            when (position) {
                0 -> {
                    Glide.with(mContext).load(R.drawable.onboarding_1).centerCrop().into(bgImage)
                    tvTop.text = mContext.getString(R.string.get_great)
                    tvBottom.text = mContext.getString(R.string.deals_discounts_offers)
                }
                1 -> {
                    Glide.with(mContext).load(R.drawable.onboarding_2).centerCrop().into(bgImage)
                    tvTop.text = mContext.getString(R.string.spend_through_woopons)
                    tvBottom.text = mContext.getString(R.string.wide_range_of_brands)
                }
                2 -> {
                    Glide.with(mContext).load(R.drawable.onboarding_3).centerCrop().into(bgImage)
                    tvTop.text = mContext.getString(R.string.save_more_with)
                    tvBottom.text = mContext.getString(R.string.app_name)
                }
                else -> {
                    tvTop.text = mContext.getString(R.string.get_great)
                    tvBottom.text = mContext.getString(R.string.deals_discounts_offers)
                }
            }
        }

        private fun doDots(pageSelected: Int) {

            val mDotsList: List<View> = ArrayList()
            llDots.requestLayout()
            llDots.removeAllViews()
            for (i in 0 until pageSize) {
                val view = View(mContext)
                val params: LinearLayout.LayoutParams = if (i == 0) {
                    AppUtils.getParams(mContext, 0.0f, 0.0f, 0.0f, 0.0f)
                } else {
                    AppUtils.getParams(mContext, 10.0f, 0.0f, 0.0f, 0.0f)
                }

                if (i == pageSelected) {
                    view.background = ResourcesCompat.getDrawable(
                        mContext.resources,
                        R.drawable.shape_theme_color_circle,
                        null
                    )
                } else {
                    view.background = ResourcesCompat.getDrawable(
                            mContext.resources,
                            R.drawable.shape_gray_circle,
                            null
                        )
                }

                params.gravity = Gravity.BOTTOM
                view.layoutParams = params
                view.layoutParams.height = AppUtils.convertDpToPixels(mContext, 10.0f)
                view.layoutParams.width = AppUtils.convertDpToPixels(mContext, 10.0f)
                mDotsList.plus(view)
                llDots.addView(view)

            }
        }
    }


}