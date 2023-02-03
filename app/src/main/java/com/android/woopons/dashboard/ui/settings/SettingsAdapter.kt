package com.android.woopons.dashboard.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.woopons.R
import com.android.woopons.databinding.LayoutSettingsBinding

interface SettingsItemClickListener {
    fun onItemClick(position: Int)
}

class SettingsAdapter(var mContext: Context, var onItemClickListener: SettingsItemClickListener) :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding =
            LayoutSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 6
    }

    inner class SettingsViewHolder(itemView: LayoutSettingsBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val ivIcon: ImageView = itemView.ivIcon
        private val tvText: TextView = itemView.tvText


        fun bind(position: Int) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
            when (position) {
                0 -> {
                    ivIcon.setImageResource(R.drawable.ic_profile)
                    tvText.text = mContext.getString(R.string.profile)
                }
                1 -> {
                    ivIcon.setImageResource(R.drawable.ic_change_password)
                    tvText.text = mContext.getString(R.string.change_password)
                }
                2 -> {
                    ivIcon.setImageResource(R.drawable.ic_feeback)
                    tvText.text = mContext.getString(R.string.feedback_suggestion)
                }
                3 -> {
                    ivIcon.setImageResource(R.drawable.ic_terms_and_conditions)
                    tvText.text = mContext.getString(R.string.terms_conditions)
                }
                4 -> {
                    ivIcon.setImageResource(R.drawable.ic_privacy_policy)
                    tvText.text = mContext.getString(R.string.privacy_policy)
                }
                5 -> {
                    ivIcon.setImageResource(R.drawable.ic_logout)
                    tvText.text = mContext.getString(R.string.logout)
                }
                else -> {
                    tvText.text = mContext.getString(R.string.profile)
                }
            }
        }

    }
}