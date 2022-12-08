package com.android.woopons.dashboard.ui.viewall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.woopons.databinding.LayoutDashboardCategoryBinding
import com.android.woopons.databinding.LayoutDashboardTopBrandsBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.models.TopBusinessModel
import com.android.woopons.utils.Constants
import com.bumptech.glide.Glide

class TopBrandViewAllAdapter(
    var mContext: Context,
    var mTopBusinessList: List<TopBusinessModel>?,
    var onItemClickListener: TopBrandItemClickListener
) :
    RecyclerView.Adapter<TopBrandViewAllAdapter.TopBrandViewHolder>() {

    interface TopBrandItemClickListener {
        fun onItemClick(topBusinessModel: TopBusinessModel)

        fun loadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopBrandViewHolder {
        val binding =
            LayoutDashboardTopBrandsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TopBrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopBrandViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mTopBusinessList?.size ?: 0
    }


    inner class TopBrandViewHolder(itemView: LayoutDashboardTopBrandsBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivBrands: ImageView = itemView.ivBrands


        fun bind(position: Int) {
            mTopBusinessList?.get(position)?.let { topBusinessModel ->
                Glide.with(mContext).load(Constants.IMAGE_BASE_URL + topBusinessModel.avatar).centerCrop().into(ivBrands)

                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(topBusinessModel)
                }
                if (position >= ((mTopBusinessList?.size ?: 0) - 12)) {
                    onItemClickListener.loadMore()
                }
            }
        }

    }


}