package com.android.woopons.dashboard.ui.viewall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponsListActivity
import com.android.woopons.dashboard.ui.viewall.adapters.TrendingViewAllAdapter
import com.android.woopons.databinding.ActivityViewAllBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD

class TrendingViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllBinding
    var kProgressHUD: KProgressHUD? = null
    var categorylist: ArrayList<CategoryModel> = ArrayList()
    var trendingViewAllAdapter: TrendingViewAllAdapter? = null
    private var isLoadMoreCallOnGoing = false
    val limit = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = getString(R.string.trending_categories)

        binding.rlBack.setOnClickListener{
            finish()
        }

        trendingViewAllAdapter =
            TrendingViewAllAdapter(
                this,
                categorylist,
                object : TrendingViewAllAdapter.TrendingCategoryItemClickListener {

                    override fun onItemClick(categoryModel: CategoryModel) {
                        val intent = Intent(this@TrendingViewAllActivity, CouponsListActivity::class.java)
                        intent.putExtra(
                            "couponsListType",
                            AppUtils.Companion.Coupons.CATEGORIES.name
                        )
                        intent.putExtra("couponsId", categoryModel.id)
                        intent.putExtra("name", categoryModel.name)
                        this@TrendingViewAllActivity.startActivity(intent)
                    }

                    override fun loadMore() {
                        val items = categorylist
                        val count = items.count()//mData.count() - 1
                        if (!isLoadMoreCallOnGoing && count > 0 && count % limit == 0 ) {
                            isLoadMoreCallOnGoing = true
                            val page = (count / limit) + 1
                            loadTrendingCategories(false,page)
                        }
                    }

                })
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = trendingViewAllAdapter

        loadTrendingCategories(true)

    }

    private fun loadTrendingCategories(isFirst: Boolean, page: Int = 1) {
        if (isFirst) {
            kProgressHUD = AppUtils.getKProgressHUD(this@TrendingViewAllActivity)
            kProgressHUD?.show()
        }
        NetworkClass.callApi(URLApi.getTopCategory(page, limit), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    val viewAllModel = Gson().fromJson(it, ViewAllModel::class.java)
                    isLoadMoreCallOnGoing = false

                    if (page == 1){
                        categorylist.clear()
                    }
                    viewAllModel.categories?.let { couponsList ->
                        if (couponsList.size == 0) {
                            isLoadMoreCallOnGoing = true
                        }
                        categorylist.addAll(couponsList)
                    }
                    trendingViewAllAdapter?.notifyDataSetChanged()
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@TrendingViewAllActivity)
            }
        })
    }
}