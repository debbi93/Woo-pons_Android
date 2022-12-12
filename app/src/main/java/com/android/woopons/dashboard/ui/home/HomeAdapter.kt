package com.android.woopons.dashboard.ui.home

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponDetailsActivity
import com.android.woopons.dashboard.ui.CouponsListActivity
import com.android.woopons.databinding.LayoutDashboardSearchBinding
import com.android.woopons.databinding.LayoutDashboardViewAllWithRecyclerBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.models.DashboardModel
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.models.TopBusinessModel
import com.android.woopons.utils.AppUtils
import java.util.*


class HomeAdapter(
    var mContext: Context,
    var mDashboardModel: DashboardModel?,
    var onItemClickListener: HomeItemClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    val SEARCH_LAYOUT = 100
    val RECYLER_LAYOUT = 101

    interface HomeItemClickListener {
        fun onViewAllClick(dashboardEnum: AppUtils.Companion.Dashboard)

        fun search(text: String)

    }

    fun setDashboardModel(dashboardModel: DashboardModel?) {
        mDashboardModel = dashboardModel
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        if (viewType == SEARCH_LAYOUT) {
            val binding =
                LayoutDashboardSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return SearchViewHolder(binding)
        } else {
            val binding =
                LayoutDashboardViewAllWithRecyclerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return CategoryViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        if (mDashboardModel == null)
            return 0
        else
            return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == SEARCH_LAYOUT) {
            (holder as SearchViewHolder).bind()
        } else {
            (holder as CategoryViewHolder).bind(position)
        }
    }

    override fun getItemViewType(flatPosition: Int): Int {
//        return if (flatPosition == 0) {
//            SEARCH_LAYOUT
//        } else RECYLER_LAYOUT
        return RECYLER_LAYOUT
    }

    inner class SearchViewHolder(itemView: LayoutDashboardSearchBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val etSearch: EditText = itemView.etSearch
        var searchPlaceTextWatcher: TextWatcher? = null
        var timer = Timer()
        val DELAY: Long = 1000

        fun bind() {
            var searched = false
            timer.cancel()

            etSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searched = true
                    onItemClickListener.search(etSearch.text.toString())
                    hideKeyboard()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            searchPlaceTextWatcher = object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                if (!searched)
                                    onItemClickListener.search(s.toString())
                            }
                        },
                        DELAY
                    )
                }
            }

            etSearch.removeTextChangedListener(searchPlaceTextWatcher)
            etSearch.addTextChangedListener(searchPlaceTextWatcher)
        }

        fun hideKeyboard() {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        }

    }

    inner class CategoryViewHolder(itemView: LayoutDashboardViewAllWithRecyclerBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val tvTitle: TextView = itemView.tvTitle
        private val tvViewAll: TextView = itemView.tvViewAll
        private val rvHorizontalRecycler: RecyclerView = itemView.rvHorizontalRecycler


        fun bind(position: Int) {
            rvHorizontalRecycler.layoutManager = LinearLayoutManager(mContext, HORIZONTAL, false)
            when (position) {
                0 -> {
                    if ((mDashboardModel?.categories?.size ?: 0) > 0) {
                        itemView.visibility = VISIBLE
                    } else {
                        itemView.visibility = GONE
                    }
                    tvTitle.text = mContext.getString(R.string.select_categories)
                    val categoryAdapter = CategoryAdapter(
                        mContext,
                        mDashboardModel?.categories,
                        object : CategoryAdapter.CategoryItemClickListener {

                            override fun onItemClick(categoryModel: CategoryModel) {
                                val intent = Intent(mContext, CouponsListActivity::class.java)
                                intent.putExtra(
                                    "couponsListType",
                                    AppUtils.Companion.Coupons.CATEGORIES.name
                                )
                                intent.putExtra("couponsId", categoryModel.id)
                                intent.putExtra("name", categoryModel.name)
                                mContext.startActivity(intent)
                            }

                        })
                    rvHorizontalRecycler.adapter = categoryAdapter
                    tvViewAll.setOnClickListener {
                        onItemClickListener.onViewAllClick(AppUtils.Companion.Dashboard.CATEGORIES)
                    }
                }
                1 -> {
                    if ((mDashboardModel?.coupons?.size ?: 0) > 0) {
                        itemView.visibility = VISIBLE
                    } else {
                        itemView.visibility = GONE
                    }
                    tvTitle.text = mContext.getString(R.string.recently_added)
                    val topBrandAdapter = RecentlyAddedAdapter(
                        mContext,
                        mDashboardModel?.coupons,
                        object : RecentlyAddedAdapter.RecentlyAddedItemClickListener {

                            override fun onItemClick(couponModel: RecentCouponModel) {
                                val intent = Intent(mContext, CouponDetailsActivity::class.java)
                                intent.putExtra("couponsModel", couponModel)
                                mContext.startActivity(intent)
                            }

                        })
                    rvHorizontalRecycler.adapter = topBrandAdapter
                    tvViewAll.setOnClickListener {
                        onItemClickListener.onViewAllClick(AppUtils.Companion.Dashboard.RECENTLY_ADDED)
                    }
                }
                2 -> {
                    if ((mDashboardModel?.business?.size ?: 0) > 0) {
                        itemView.visibility = VISIBLE
                    } else {
                        itemView.visibility = GONE
                    }
                    tvTitle.text = mContext.getString(R.string.top_brands)
                    val topBrandAdapter = TopBrandAdapter(
                        mContext,
                        mDashboardModel?.business,
                        object : TopBrandAdapter.TopBrandItemClickListener {
                            override fun onItemClick(topBusinessModel: TopBusinessModel) {
                                val intent = Intent(mContext, CouponsListActivity::class.java)
                                intent.putExtra(
                                    "couponsListType",
                                    AppUtils.Companion.Coupons.TOP_BRANDS.name
                                )
                                intent.putExtra("couponsId", topBusinessModel.id)
                                intent.putExtra("name", topBusinessModel.name)
                                mContext.startActivity(intent)
                            }

                        })
                    rvHorizontalRecycler.adapter = topBrandAdapter
                    tvViewAll.setOnClickListener {
                        onItemClickListener.onViewAllClick(AppUtils.Companion.Dashboard.TOP_BRANDS)
                    }
                }
                3 -> {
                    if ((mDashboardModel?.trending_categories?.size ?: 0) > 0) {
                        itemView.visibility = VISIBLE
                    } else {
                        itemView.visibility = GONE
                    }
                    tvTitle.text = mContext.getString(R.string.trending_categories)
                    val categoryAdapter = TrendingCategoryAdapter(
                        mContext,
                        mDashboardModel?.trending_categories,
                        object : TrendingCategoryAdapter.TrendingCategoryItemClickListener {
                            override fun onItemClick(categoryModel: CategoryModel) {
                                val intent = Intent(mContext, CouponsListActivity::class.java)
                                intent.putExtra(
                                    "couponsListType",
                                    AppUtils.Companion.Coupons.CATEGORIES.name
                                )
                                intent.putExtra("couponsId", categoryModel.id)
                                intent.putExtra("name", categoryModel.name)
                                mContext.startActivity(intent)
                            }

                        })
                    rvHorizontalRecycler.adapter = categoryAdapter
                    tvViewAll.setOnClickListener {
                        onItemClickListener.onViewAllClick(AppUtils.Companion.Dashboard.TRENDING_CATEGORIES)
                    }
                }
                else -> {
                    tvTitle.text = mContext.getString(R.string.select_categories)
                }
            }

        }

    }

}