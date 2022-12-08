package com.android.woopons.dashboard.ui.coupons

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponDetailsActivity
import com.android.woopons.databinding.FragmentCouponsBinding
import com.android.woopons.models.MyCouponsModel
import com.android.woopons.models.RecentCouponModel
import com.android.woopons.network.NetworkClass
import com.android.woopons.network.Response
import com.android.woopons.network.URLApi
import com.android.woopons.utils.AppUtils
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject

class CouponsFragment : Fragment() {

    private var _binding: FragmentCouponsBinding? = null
    lateinit var kProgressHUD: KProgressHUD
    var myCouponsModel: MyCouponsModel? = null
    var couponsAdapter: CouponsAdapter? = null
    var mCouponList: ArrayList<RecentCouponModel> = ArrayList()

    var isNewlyAddedSelected = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCouponsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { context ->

            couponsAdapter =
                CouponsAdapter(
                    context,
                    mCouponList,
                    AppUtils.Companion.Coupons.NEWLY_ADDED,
                    object : CouponsAdapter.CouponItemClickListener {
                        override fun onItemClick(couponsModel: RecentCouponModel, pageType: AppUtils.Companion.Coupons?) {
                            val intent = Intent(context, CouponDetailsActivity::class.java)
                            intent.putExtra("couponsModel", couponsModel)
                            intent.putExtra("couponType", pageType?.name)
                            startActivity(intent)
                        }

                        override fun loadMore() {

                        }

                    })
            binding.rvCouponsList.layoutManager = LinearLayoutManager(context)
            binding.rvCouponsList.adapter = couponsAdapter

            fetchCoupons(context)

            buttonSelected(context)

            binding.rlNewlyAdded.setOnClickListener {
                isNewlyAddedSelected = true
                buttonSelected(context)
            }

            binding.rlHistory.setOnClickListener {
                isNewlyAddedSelected = false
                buttonSelected(context)
            }
        }
    }

    private fun buttonSelected(context: Context) {
        setCouponsList()
        if (isNewlyAddedSelected) {
            binding.tvNewlyAdded.setTextColor(context.getColor(R.color.primary_color))
            binding.viewNewlyAdded.visibility = View.VISIBLE
            binding.tvHistory.setTextColor(context.getColor(R.color.black_fifty))
            binding.viewHistory.visibility = View.GONE
        } else {
            binding.tvNewlyAdded.setTextColor(context.getColor(R.color.black_fifty))
            binding.viewNewlyAdded.visibility = View.GONE
            binding.tvHistory.setTextColor(context.getColor(R.color.primary_color))
            binding.viewHistory.visibility = View.VISIBLE
        }
    }

    fun setCouponsList() {
        mCouponList.clear()
        if (isNewlyAddedSelected) {
            mCouponList.addAll(myCouponsModel?.newly_added ?: ArrayList())
            couponsAdapter?.setPageType(AppUtils.Companion.Coupons.NEWLY_ADDED)
        } else {
            mCouponList.addAll(myCouponsModel?.history ?: ArrayList())
            couponsAdapter?.setPageType(AppUtils.Companion.Coupons.HISTORY)
        }
        couponsAdapter?.notifyDataSetChanged()
        if (mCouponList.size > 0) {
            binding.ivNoRecords.visibility = View.GONE
        } else {
            binding.ivNoRecords.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchCoupons(context: Context) {
        kProgressHUD = AppUtils.getKProgressHUD(context)
        kProgressHUD.show()
        NetworkClass.callApi(URLApi.getMyCoupons(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD.dismiss()
                val json = JSONObject(response ?: "")

                myCouponsModel = Gson().fromJson(json.toString(), MyCouponsModel::class.java)

                setCouponsList()

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD.dismiss()
                AppUtils.showToast(error ?: "", context)
            }
        })
    }
}