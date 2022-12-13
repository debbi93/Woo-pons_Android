package com.android.woopons.dashboard.ui.viewall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardCategoryBinding
import com.android.woopons.databinding.LayoutDashboardTrendingCategoryBinding
import com.android.woopons.databinding.LayoutViewAllTrendingCategoryBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class TrendingViewAllAdapter(
    var mContext: Context,
    var mCategoryList: List<CategoryModel>?,
    var onItemClickListener: TrendingCategoryItemClickListener
) :
    RecyclerView.Adapter<TrendingViewAllAdapter.TrendingCategoryViewHolder>() {

    interface TrendingCategoryItemClickListener {
        fun onItemClick(categoryModel: CategoryModel)

        fun loadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingCategoryViewHolder {
        val binding =
            LayoutViewAllTrendingCategoryBinding.inflate(
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

    inner class TrendingCategoryViewHolder(itemView: LayoutViewAllTrendingCategoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivTrendingCategory: ImageView = itemView.ivTrendingCategory
        private val tvTrendingCategory: TextView = itemView.tvTrendingCategory
        private val tvDetails: TextView = itemView.tvDetails
        private val cvExplore: CardView = itemView. cvExplore


        fun bind(position: Int) {
            mCategoryList?.get(position)?.let { categoryModel ->
                AppUtils.loadImage(mContext, categoryModel.image, ivTrendingCategory)
                tvTrendingCategory.text = categoryModel.name
                tvDetails.text = categoryModel.description
                cvExplore.setOnClickListener {
                    onItemClickListener.onItemClick(categoryModel)
                }

                if (position >= (mCategoryList!!.size - 4)) {
                    onItemClickListener.loadMore()
                }
            }
        }

    }

}