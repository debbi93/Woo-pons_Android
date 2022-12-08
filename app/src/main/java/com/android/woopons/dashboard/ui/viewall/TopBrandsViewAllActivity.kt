package com.android.woopons.dashboard.ui.viewall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponsListActivity
import com.android.woopons.dashboard.ui.viewall.adapters.CategoryViewAllAdapter
import com.android.woopons.dashboard.ui.viewall.adapters.TopBrandViewAllAdapter
import com.android.woopons.databinding.ActivityViewAllBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.models.TopBusinessModel
import com.android.woopons.models.ViewAllModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD

class TopBrandsViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllBinding
    var kProgressHUD: KProgressHUD? = null
    var businessList: ArrayList<TopBusinessModel> = ArrayList()
    var topBrandViewAllAdapter: TopBrandViewAllAdapter? = null
    private var isLoadMoreCallOnGoing = false
    val limit = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = getString(R.string.top_brands)

        binding.rlBack.setOnClickListener{
            finish()
        }

        topBrandViewAllAdapter =
            TopBrandViewAllAdapter(
                this,
                businessList,
                object : TopBrandViewAllAdapter.TopBrandItemClickListener {

                    override fun onItemClick(topBusinessModel: TopBusinessModel) {
                        val intent = Intent(this@TopBrandsViewAllActivity, CouponsListActivity::class.java)
                        intent.putExtra(
                            "couponsListType",
                            AppUtils.Companion.Coupons.TOP_BRANDS.name
                        )
                        intent.putExtra("couponsId", topBusinessModel.id)
                        intent.putExtra("name", topBusinessModel.name)
                        this@TopBrandsViewAllActivity.startActivity(intent)
                    }

                    override fun loadMore() {
                        val items = businessList
                        val count = items.count()//mData.count() - 1
                        if (!isLoadMoreCallOnGoing && count > 0 && count % limit == 0 ) {
                            isLoadMoreCallOnGoing = true
                            val page = (count / limit) + 1
                            loadBrands(false,page)
                        }
                    }

                })
        binding.rvCategories.layoutManager = GridLayoutManager(this, 3)
        binding.rvCategories.adapter = topBrandViewAllAdapter

        loadBrands(true)

    }

    private fun loadBrands(isFirst: Boolean, page: Int = 1) {
        if (isFirst) {
            kProgressHUD = AppUtils.getKProgressHUD(this@TopBrandsViewAllActivity)
            kProgressHUD?.show()
        }
        NetworkClass.callApi(URLApi.getTopBusiness(page, limit), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    val viewAllModel = Gson().fromJson(it, ViewAllModel::class.java)
                    isLoadMoreCallOnGoing = false

                    if (page == 1){
                        businessList.clear()
                    }
                    viewAllModel.business?.let { couponsList ->
                        if (couponsList.size == 0) {
                            isLoadMoreCallOnGoing = true
                        }
                        businessList.addAll(couponsList)
                    }
                    topBrandViewAllAdapter?.notifyDataSetChanged()
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@TopBrandsViewAllActivity)
            }
        })
    }
}