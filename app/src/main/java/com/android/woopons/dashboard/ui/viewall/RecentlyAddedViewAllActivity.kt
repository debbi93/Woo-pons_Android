package com.android.woopons.dashboard.ui.viewall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponDetailsActivity
import com.android.woopons.dashboard.ui.home.RecentlyAddedAdapter
import com.android.woopons.dashboard.ui.viewall.adapters.CategoryViewAllAdapter
import com.android.woopons.dashboard.ui.viewall.adapters.RecentlyAddedViewAllAdapter
import com.android.woopons.databinding.ActivityViewAllBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.models.DashboardModel
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD

class RecentlyAddedViewAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllBinding
    var kProgressHUD: KProgressHUD? = null
    var recentCouponsList: ArrayList<RecentCouponModel> = ArrayList()
    var recentlyAddedAdapter: RecentlyAddedViewAllAdapter? = null
    private var isLoadMoreCallOnGoing = false
    val limit = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rlBack.setOnClickListener{
            finish()
        }

        binding.tvTitle.text = getString(R.string.recently_added)

        recentlyAddedAdapter =
            RecentlyAddedViewAllAdapter(
                this,
                recentCouponsList,
                object : RecentlyAddedViewAllAdapter.RecentlyAddedItemClickListener {
                    override fun onItemClick(recentCouponModel: RecentCouponModel) {
                        val intent = Intent(this@RecentlyAddedViewAllActivity, CouponDetailsActivity::class.java)
                        intent.putExtra("couponsModel", recentCouponModel)
                        startActivity(intent)
                    }

                    override fun loadMore() {
                        val items = recentCouponsList
                        val count = items.count()//mData.count() - 1
                        if (!isLoadMoreCallOnGoing && count > 0 && count % limit == 0 ) {
                            isLoadMoreCallOnGoing = true
                            val page = (count / limit) + 1
                            loadCoupons(false,page)
                        }
                    }

                })
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = recentlyAddedAdapter

        loadCoupons(true)
    }

    private fun loadCoupons(isFirst: Boolean, page: Int = 1) {
        if (isFirst) {
            kProgressHUD = AppUtils.getKProgressHUD(this@RecentlyAddedViewAllActivity)
            kProgressHUD?.show()
        }
        NetworkClass.callApi(URLApi.getAllCoupons(page, limit), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    val viewAllModel = Gson().fromJson(it, ViewAllModel::class.java)
                    isLoadMoreCallOnGoing = false

                    if (page == 1){
                        recentCouponsList.clear()
                    }
                    viewAllModel.coupons?.let { couponsList ->
                        if (couponsList.size == 0) {
                            isLoadMoreCallOnGoing = true
                        }
                        recentCouponsList.addAll(couponsList)
                    }
                    recentlyAddedAdapter?.notifyDataSetChanged()
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@RecentlyAddedViewAllActivity)
            }
        })
    }
}