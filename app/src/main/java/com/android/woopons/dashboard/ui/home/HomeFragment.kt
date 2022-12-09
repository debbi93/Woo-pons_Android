package com.android.woopons.dashboard.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.dashboard.ui.viewall.CategoriesViewAllActivity
import com.android.woopons.dashboard.ui.viewall.RecentlyAddedViewAllActivity
import com.android.woopons.dashboard.ui.viewall.TopBrandsViewAllActivity
import com.android.woopons.dashboard.ui.viewall.TrendingViewAllActivity
import com.android.woopons.databinding.FragmentHomeBinding
import com.android.woopons.models.DashboardModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.android.woopons.utils.AppUtils.Companion.showToast
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    var kProgressHUD: KProgressHUD? = null
    var dashboardModel: DashboardModel? = null
    var homeAdapter: HomeAdapter? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { context ->
            kProgressHUD = AppUtils.getKProgressHUD(context)

            homeAdapter =
                HomeAdapter(context, dashboardModel, object : HomeAdapter.HomeItemClickListener {

                    override fun onViewAllClick(dashboardEnum: AppUtils.Companion.Dashboard) {
                        if (dashboardEnum == AppUtils.Companion.Dashboard.CATEGORIES) {
                            val intent = Intent(context, CategoriesViewAllActivity::class.java)
                            startActivity(intent)
                        } else if (dashboardEnum == AppUtils.Companion.Dashboard.RECENTLY_ADDED) {
                            val intent = Intent(context, RecentlyAddedViewAllActivity::class.java)
                            startActivity(intent)
                        } else if (dashboardEnum == AppUtils.Companion.Dashboard.TOP_BRANDS) {
                            val intent = Intent(context, TopBrandsViewAllActivity::class.java)
                            startActivity(intent)
                        } else if (dashboardEnum == AppUtils.Companion.Dashboard.TRENDING_CATEGORIES) {
                            val intent = Intent(context, TrendingViewAllActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun search(text: String) {
                        if (text.length > 0)
                            search(context, text)
                        else
                            fetchHomeData(context)
                    }

                })
            binding.rvHome.layoutManager = LinearLayoutManager(context)
            binding.rvHome.adapter = homeAdapter

            fetchHomeData(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchHomeData(context: Context) {
        setProgressVisiblity(true)
        NetworkClass.callApi(URLApi.getDashboardData(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                val json = JSONObject(response ?: "")

                dashboardModel = Gson().fromJson(json.toString(), DashboardModel::class.java)
                setProgressVisiblity(false)
                homeAdapter?.setDashboardModel(dashboardModel)
                setNoRecordsVisibility()
                homeAdapter?.notifyDataSetChanged()

            }

            override fun onErrorResponse(error: String?) {
                setProgressVisiblity(false)
                showToast(error ?: "", context)
            }
        })
    }

    fun setProgressVisiblity(visible: Boolean) {
        activity?.runOnUiThread {
            if (visible) {
                kProgressHUD?.show()
            } else {
                kProgressHUD?.dismiss()
            }
        }
    }

    fun search(context: Context, search: String) {
        NetworkClass.callApi(URLApi.search(search), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                val json = JSONObject(response ?: "")

                dashboardModel = Gson().fromJson(json.toString(), DashboardModel::class.java)
                homeAdapter?.setDashboardModel(dashboardModel)
                setNoRecordsVisibility()
                homeAdapter?.notifyDataSetChanged()

            }

            override fun onErrorResponse(error: String?) {
                showToast(error ?: "", context)
            }
        })
    }

    fun setNoRecordsVisibility() {
        if ((dashboardModel?.categories?.size ?: 0) == 0 && (dashboardModel?.coupons?.size
                ?: 0) == 0
            && (dashboardModel?.business?.size
                ?: 0) == 0 && (dashboardModel?.trending_categories?.size ?: 0) == 0
        ) {
            binding.ivNoRecords.visibility = View.VISIBLE
        } else {
            binding.ivNoRecords.visibility = View.GONE
        }
    }
}