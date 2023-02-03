package com.android.woopons.dashboard.ui.coupons

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.R
import com.android.woopons.databinding.LayoutCouponsDesignBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class CouponsAdapter(
    var mContext: Context,
    var mCouponList: ArrayList<RecentCouponModel>?,
    var mPageType: AppUtils.Companion.Coupons?,
    var onItemClickListener: CouponItemClickListener
) :
    RecyclerView.Adapter<CouponsAdapter.CouponsViewHolder>() {

    interface CouponItemClickListener {
        fun onItemClick(couponsModel: RecentCouponModel, pageType: AppUtils.Companion.Coupons?)

        fun loadMore()

        fun getCoupon(couponsModel: RecentCouponModel)

        fun unlockCoupon(couponsModel: RecentCouponModel)

        fun favoriteClick(couponsModel: RecentCouponModel, position: Int)
    }

    fun setPageType(pageType: AppUtils.Companion.Coupons) {
        this.mPageType = pageType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponsViewHolder {
        val binding =
            LayoutCouponsDesignBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CouponsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CouponsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mCouponList?.size ?: 0
    }

    inner class CouponsViewHolder(itemView: LayoutCouponsDesignBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivImage: ImageView = itemView.ivImage
        private val tvOutletName: TextView = itemView.tvOutletName
        private val rbRating: RatingBar = itemView.rbRating
        private val tvRating: TextView = itemView.tvRating
        private val tvUnlimited: TextView = itemView.tvUnlimited
        private val tvGetCoupon: TextView = itemView.tvGetCoupon
        private val cvViewDetails: CardView = itemView.cvViewDetails
        private val ivFavorite = itemView.ivFavorite
        private val rlNameView = itemView.rlNameView
        private val tvImageName = itemView.tvImageName


        fun bind(position: Int) {
            mCouponList?.get(position)?.let { couponsModel ->
                if (couponsModel.company_logo?.isBlank() ?: true) {
                    rlNameView.visibility = View.VISIBLE
                    tvImageName.text = AppUtils.getAcronyms(couponsModel.company_name)
                } else {
                    rlNameView.visibility = View.GONE
                    AppUtils.loadImage(mContext, couponsModel.company_logo, ivImage)
                }
                tvOutletName.text = couponsModel.company_name
                rbRating.rating = couponsModel.rating_avg ?: 0f
                tvRating.text =
                    "${couponsModel.rating_avg ?: 0}"
                tvUnlimited.text = couponsModel.repetition

                cvViewDetails.setOnClickListener {
                    onItemClickListener.onItemClick(couponsModel, mPageType)
                }
                if (couponsModel.is_favourited ?: false)
                    ivFavorite.setImageResource(R.drawable.ic_heart_filled)
                else
                    ivFavorite.setImageResource(R.drawable.ic_heart_unfilled)

                if (mPageType == AppUtils.Companion.Coupons.FAVORITES) {
                    ivFavorite.setOnClickListener(null)
                } else {
                    ivFavorite.setOnClickListener {
                        onItemClickListener.favoriteClick(couponsModel, position)
                    }
                }

                tvUnlimited.visibility = View.VISIBLE
                tvGetCoupon.visibility = View.VISIBLE
                tvGetCoupon.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
                when (mPageType) {
                    AppUtils.Companion.Coupons.NEWLY_ADDED -> {
                        tvGetCoupon.text = mContext.getString(R.string.unlock_coupon)
                        tvGetCoupon.setOnClickListener {
                            onItemClickListener.unlockCoupon(couponsModel)
                        }
                    }
                    AppUtils.Companion.Coupons.HISTORY -> {
                        tvGetCoupon.visibility = View.GONE
                        tvUnlimited.visibility = View.GONE
                    }
                    else -> {
                        tvGetCoupon.text = mContext.getString(R.string.get_coupon_now)
                        tvGetCoupon.setOnClickListener {
                            onItemClickListener.getCoupon(couponsModel)
                        }
                    }
                }
            }
        }

    }

}