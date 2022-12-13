package com.android.woopons.dashboard.ui.viewall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardRecentlyAddedBinding
import com.android.woopons.databinding.LayoutViewAllRecentlyAddedBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class RecentlyAddedViewAllAdapter(
    var mContext: Context,
    var mRecentList: List<RecentCouponModel>?,
    var onItemClickListener: RecentlyAddedItemClickListener
) :
    RecyclerView.Adapter<RecentlyAddedViewAllAdapter.RecentlyAddedViewHolder>() {

    interface RecentlyAddedItemClickListener {
        fun onItemClick(recentCouponModel: RecentCouponModel)

        fun loadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyAddedViewHolder {
        val binding =
            LayoutViewAllRecentlyAddedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecentlyAddedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentlyAddedViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mRecentList?.size ?: 0
    }

    inner class RecentlyAddedViewHolder(itemView: LayoutViewAllRecentlyAddedBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivBarCode: ImageView = itemView.ivBarCode
        private val tvCompanyName: TextView = itemView.tvCompanyName
        private val tvCategoryName: TextView = itemView.tvCategoryName
        private val tvUnlimited: TextView = itemView.tvUnlimited
        private val ivImage: ImageView = itemView.ivImage
        private val llSeeDetails: LinearLayout = itemView.llSeeDetails


        fun bind(position: Int) {
            mRecentList?.get(position)?.let { couponModel ->
                AppUtils.loadImage(mContext, couponModel.company_logo, ivImage)
                tvCompanyName.text = couponModel.name
                tvCategoryName.text = couponModel.company_category
                tvUnlimited.text = couponModel.repetition

                llSeeDetails.setOnClickListener {
                    onItemClickListener.onItemClick(couponModel)
                }
                if (position >= (mRecentList!!.size - 4)) {
                    onItemClickListener.loadMore()
                }
            }
        }

    }

}