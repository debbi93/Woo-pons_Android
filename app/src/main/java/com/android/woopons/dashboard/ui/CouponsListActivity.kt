package com.android.woopons.dashboard.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.coupons.CouponsAdapter
import com.android.woopons.databinding.ActivityCouponsListBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD

class CouponsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCouponsListBinding
    var mCouponList: ArrayList<RecentCouponModel> = ArrayList()
    var kProgressHUD: KProgressHUD? = null
    var couponsAdapter: CouponsAdapter? = null
    private var isLoadMoreCallOnGoing = false
    val limit = 20
    var couponsId: Int? = null
    var titleName: String? = null
    var pageType: AppUtils.Companion.Coupons? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCouponsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.extras?.getString("couponsListType")?.let {
            when (it) {
                AppUtils.Companion.Coupons.FAVORITES.name ->
                    pageType = AppUtils.Companion.Coupons.FAVORITES
                AppUtils.Companion.Coupons.CATEGORIES.name ->
                    pageType = AppUtils.Companion.Coupons.CATEGORIES
                AppUtils.Companion.Coupons.TOP_BRANDS.name ->
                    pageType = AppUtils.Companion.Coupons.TOP_BRANDS
            }
        }
        couponsId = intent.extras?.getInt("couponsId")
        titleName = intent.extras?.getString("name")

        setTitle()

        binding.rlBack.setOnClickListener {
            finish()
        }

        couponsAdapter =
            CouponsAdapter(
                this,
                mCouponList,
                pageType,
                object : CouponsAdapter.CouponItemClickListener {

                    override fun onItemClick(couponsModel: RecentCouponModel, pageType: AppUtils.Companion.Coupons?) {
                        val intent =
                            Intent(this@CouponsListActivity, CouponDetailsActivity::class.java)
                        intent.putExtra("couponsModel", couponsModel)
                        intent.putExtra("couponType", pageType?.name)
                        startActivity(intent)
                    }

                    override fun loadMore() {
                        val items = mCouponList
                        val count = items.count()//mData.count() - 1
                        if (!isLoadMoreCallOnGoing && count > 0 && count % limit == 0) {
                            isLoadMoreCallOnGoing = true
                            val page = (count / limit) + 1
                            loadCoupons(false, page)
                        }
                    }

                })
        binding.rvCouponsList.layoutManager = LinearLayoutManager(this)
        binding.rvCouponsList.adapter = couponsAdapter

        loadCoupons(true)

    }

    private fun loadCoupons(isFirst: Boolean, page: Int = 1) {
        if (isFirst) {
            kProgressHUD = AppUtils.getKProgressHUD(this@CouponsListActivity)
            kProgressHUD?.show()
        }
        NetworkClass.callApi(getUrl(page), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    val viewAllModel = Gson().fromJson(it, ViewAllModel::class.java)
                    isLoadMoreCallOnGoing = false

                    if (page == 1) {
                        mCouponList.clear()
                    }
                    viewAllModel.coupons?.let { couponsList ->
                        if (couponsList.size == 0) {
                            isLoadMoreCallOnGoing = true
                        }
                        mCouponList.addAll(couponsList)
                    }
                    couponsAdapter?.notifyDataSetChanged()
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CouponsListActivity)
            }
        })
    }

    private fun getUrl(page: Int = 1): URLApi {
        when (pageType) {
            AppUtils.Companion.Coupons.FAVORITES ->
                return URLApi.getFavorites(page, limit)
            AppUtils.Companion.Coupons.CATEGORIES ->
                return URLApi.getCategoryCoupons(couponsId ?: 0, page, limit)
            AppUtils.Companion.Coupons.TOP_BRANDS ->
                return URLApi.getBrandsCoupons(couponsId ?: 0, page, limit)
            else -> {
                return URLApi.getFavorites(page, limit)
            }
        }
    }

    private fun setTitle() {
        when (pageType) {
            AppUtils.Companion.Coupons.FAVORITES ->
                binding.tvTitle.text = getString(R.string.my_favorites)
            else -> {
                binding.tvTitle.text = titleName
            }
        }
    }

}