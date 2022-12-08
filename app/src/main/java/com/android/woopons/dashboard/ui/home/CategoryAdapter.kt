package com.android.woopons.dashboard.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardCategoryBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class CategoryAdapter(
    var mContext: Context,
    var mCategoryList: List<CategoryModel>?,
    var onItemClickListener: CategoryItemClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface CategoryItemClickListener {
        fun onItemClick(categoryModel: CategoryModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            LayoutDashboardCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mCategoryList?.size ?: 0
    }

    inner class CategoryViewHolder(itemView: LayoutDashboardCategoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivCategory: ImageView = itemView.ivCategory
        private val tvCategory: TextView = itemView.tvCategory


        fun bind(position: Int) {
            mCategoryList?.get(position)?.let { categoryModel ->
                Glide.with(mContext).load(Constants.IMAGE_BASE_URL + categoryModel.image).centerCrop().into(ivCategory)
                tvCategory.text = categoryModel.name

                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(categoryModel)
                }
            }
        }

    }

}