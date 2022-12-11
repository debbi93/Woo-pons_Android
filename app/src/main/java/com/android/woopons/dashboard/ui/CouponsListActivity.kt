package com.android.woopons.dashboard.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.coupons.CouponsAdapter
import com.android.woopons.databinding.ActivityCouponsListBinding
import com.android.woopons.databinding.LayoutCouponRemovedBinding
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import java.text.FieldPosition

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

                    override fun onItemClick(
                        couponsModel: RecentCouponModel,
                        pageType: AppUtils.Companion.Coupons?
                    ) {
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

                    override fun getCoupon(couponsModel: RecentCouponModel) {
                        addCoupon(couponsModel)
                    }

                    override fun unlockCoupon(couponsModel: RecentCouponModel) {
                    }

                    override fun favoriteClick(couponsModel: RecentCouponModel, position: Int) {
                        changeFavorite(couponsModel, position)
                    }

                })
        binding.rvCouponsList.layoutManager = LinearLayoutManager(this)
        binding.rvCouponsList.adapter = couponsAdapter


    }

    override fun onResume() {
        super.onResume()
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
                    if (mCouponList.size > 0) {
                        binding.ivNoRecords.visibility = View.GONE
                    } else {
                        binding.ivNoRecords.visibility = View.VISIBLE
                    }
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                showToast(error ?: "", this@CouponsListActivity)
            }
        })
    }

    private fun addCoupon(couponModel: RecentCouponModel) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.addCoupon(couponModel.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                initDialog(this@CouponsListActivity)
            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                showToast(error ?: "", this@CouponsListActivity)
            }
        })
    }

    private fun initDialog(context: FragmentActivity): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutCouponRemovedBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        layoutDialog.tvCoupon.text = context.getString(R.string.coupon_added)

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
            loadCoupons(true)
        }

        return alertDialog
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

    private fun changeFavorite(couponsModel: RecentCouponModel, position: Int) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.setFavorite(couponsModel.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                showToast(message ?: "", this@CouponsListActivity)
                couponsModel.is_favourited = !(couponsModel.is_favourited ?: false)
                couponsAdapter?.notifyItemChanged(position)
            }

            override fun onErrorResponse(error: String?) {

            }
        })
    }

}