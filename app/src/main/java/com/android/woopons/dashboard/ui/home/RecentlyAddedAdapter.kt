package com.android.woopons.dashboard.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardRecentlyAddedBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class RecentlyAddedAdapter(
    var mContext: Context,
    var mRecentList: List<RecentCouponModel>?,
    var onItemClickListener: RecentlyAddedItemClickListener
) :
    RecyclerView.Adapter<RecentlyAddedAdapter.RecentlyAddedViewHolder>() {

    interface RecentlyAddedItemClickListener {
        fun onItemClick(couponModel: RecentCouponModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyAddedViewHolder {
        val binding =
            LayoutDashboardRecentlyAddedBinding.inflate(
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

    inner class RecentlyAddedViewHolder(itemView: LayoutDashboardRecentlyAddedBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivBarCode: ImageView = itemView.ivBarCode
        private val tvCompanyName: TextView = itemView.tvCompanyName
        private val tvCategoryName: TextView = itemView.tvCategoryName
        private val tvUnlimited: TextView = itemView.tvUnlimited
        private val ivImage: ImageView = itemView.ivImage
        private val llSeeDetails = itemView.llSeeDetails


        fun bind(position: Int) {
            mRecentList?.get(position)?.let { couponModel ->
                AppUtils.loadImage(mContext, couponModel.company_logo, ivImage)
                tvCompanyName.text = couponModel.name
                tvCategoryName.text = couponModel.company_category
                tvUnlimited.text = couponModel.repetition

                llSeeDetails.setOnClickListener {
                    onItemClickListener.onItemClick(couponModel)
                }
            }
        }

    }

}