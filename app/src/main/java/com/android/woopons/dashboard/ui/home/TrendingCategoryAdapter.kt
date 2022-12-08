package com.android.woopons.dashboard.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardCategoryBinding
import com.android.woopons.databinding.LayoutDashboardTrendingCategoryBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class TrendingCategoryAdapter(
    var mContext: Context,
    var mCategoryList: List<CategoryModel>?,
    var onItemClickListener: TrendingCategoryItemClickListener
) :
    RecyclerView.Adapter<TrendingCategoryAdapter.TrendingCategoryViewHolder>() {

    interface TrendingCategoryItemClickListener {
        fun onItemClick(categoryModel: CategoryModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingCategoryViewHolder {
        val binding =
            LayoutDashboardTrendingCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TrendingCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingCategoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mCategoryList?.size ?: 0
    }

    inner class TrendingCategoryViewHolder(itemView: LayoutDashboardTrendingCategoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivTrendingCategory: ImageView = itemView.ivTrendingCategory
        private val tvTrendingCategory: TextView = itemView.tvTrendingCategory
        private val cvExplore = itemView.cvExplore


        fun bind(position: Int) {
            mCategoryList?.get(position)?.let { categoryModel ->
                Glide.with(mContext).load(Constants.IMAGE_BASE_URL + categoryModel.image).centerCrop().into(ivTrendingCategory)
                tvTrendingCategory.text = categoryModel.name

                cvExplore.setOnClickListener {
                    onItemClickListener.onItemClick(categoryModel)
                }
            }
        }

    }

}