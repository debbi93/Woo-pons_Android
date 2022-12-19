package com.android.woopons.dashboard.ui.settings

import android.content.Context
import android.view.LayoutInflater
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
        return 7
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
                    Glide.with(mContext).load(R.drawable.ic_profile).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.profile)
                }
                1 -> {
                    Glide.with(mContext).load(R.drawable.ic_change_password).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.change_password)
                }
                2 -> {
                    Glide.with(mContext).load(R.drawable.ic_my_favorites).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.my_favorites)
                }
                3 -> {
                    Glide.with(mContext).load(R.drawable.ic_feeback).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.feedback_suggestion)
                }
                4 -> {
                    Glide.with(mContext).load(R.drawable.ic_terms_and_conditions).centerInside()
                        .into(ivIcon)
                    tvText.text = mContext.getString(R.string.terms_conditions)
                }
                5 -> {
                    Glide.with(mContext).load(R.drawable.ic_privacy_policy).centerInside()
                        .into(ivIcon)
                    tvText.text = mContext.getString(R.string.privacy_policy)
                }
                6 -> {
                    Glide.with(mContext).load(R.drawable.ic_logout).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.logout)
                }
                else -> {
                    Glide.with(mContext).load(R.drawable.ic_profile).centerInside().into(ivIcon)
                    tvText.text = mContext.getString(R.string.profile)
                }
            }
        }

    }
}