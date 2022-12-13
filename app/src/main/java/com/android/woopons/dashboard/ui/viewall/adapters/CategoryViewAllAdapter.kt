package com.android.woopons.dashboard.ui.viewall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutViewAllCategoryBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class CategoryViewAllAdapter (
    var mContext: Context,
    var mCategoryList: List<CategoryModel>?,
    var onItemClickListener: CategoryItemClickListener
) :
    RecyclerView.Adapter<CategoryViewAllAdapter.CategoryViewAllViewHolder>() {

    interface CategoryItemClickListener {
        fun onItemClick(categoryModel: CategoryModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewAllViewHolder {
        val binding =
            LayoutViewAllCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CategoryViewAllViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewAllViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mCategoryList?.size ?: 0
    }

    inner class CategoryViewAllViewHolder(itemView: LayoutViewAllCategoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivCategory: ImageView = itemView.ivCategory
        private val tvCategory: TextView = itemView.tvCategory

        fun bind(position: Int) {
            mCategoryList?.get(position)?.let { categoryModel ->
                AppUtils.loadImage(mContext, categoryModel.image, ivCategory)
                tvCategory.text = categoryModel.name

                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(categoryModel)
                }
            }

        }

    }

}