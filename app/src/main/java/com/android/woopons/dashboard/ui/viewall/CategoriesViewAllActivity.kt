package com.android.woopons.dashboard.ui.viewall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.android.woopons.dashboard.ui.CouponsListActivity
import com.android.woopons.dashboard.ui.viewall.adapters.CategoryViewAllAdapter
import com.android.woopons.databinding.ActivityViewAllBinding
import com.android.woopons.models.CategoryModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD

class CategoriesViewAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllBinding
    var kProgressHUD: KProgressHUD? = null
    var categoriesList: ArrayList<CategoryModel> = ArrayList()
    var categoryViewAllAdapter: CategoryViewAllAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rlBack.setOnClickListener{
            finish()
        }

        categoryViewAllAdapter =
            CategoryViewAllAdapter(
                this,
                categoriesList,
                object : CategoryViewAllAdapter.CategoryItemClickListener {
                    override fun onItemClick(categoryModel: CategoryModel) {
                        val intent = Intent(this@CategoriesViewAllActivity, CouponsListActivity::class.java)
                        intent.putExtra(
                            "couponsListType",
                            AppUtils.Companion.Coupons.CATEGORIES.name
                        )
                        intent.putExtra("couponsId", categoryModel.id)
                        intent.putExtra("name", categoryModel.name)
                        this@CategoriesViewAllActivity.startActivity(intent)
                    }

                })
        binding.rvCategories.layoutManager = GridLayoutManager(this, 2)
        binding.rvCategories.adapter = categoryViewAllAdapter

        loadCategories()

    }

    private fun loadCategories() {
        kProgressHUD = AppUtils.getKProgressHUD(this@CategoriesViewAllActivity)
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.getCategories(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                response?.let {
                    val itemType = object : TypeToken<List<CategoryModel>>() {}.type
                    categoriesList.clear()
                    categoriesList.addAll(
                        Gson().fromJson<List<CategoryModel>>(
                            it,
                            itemType
                        )
                    )
                    categoryViewAllAdapter?.notifyDataSetChanged()
                }

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(error ?: "", this@CategoriesViewAllActivity)
            }
        })
    }
}