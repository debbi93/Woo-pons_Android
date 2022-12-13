package com.android.woopons.dashboard.ui.coupons

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.woopons.R
import com.android.woopons.dashboard.ui.CouponDetailsActivity
import com.android.woopons.dashboard.ui.UnlockCouponActivity
import com.android.woopons.databinding.FragmentCouponsBinding
import com.android.woopons.databinding.LayoutCouponRemovedBinding
import com.android.woopons.databinding.LayoutRedeemCouponBinding
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

    private lateinit var binding: FragmentCouponsBinding
    var kProgressHUD: KProgressHUD? = null
    var myCouponsModel: MyCouponsModel? = null
    var couponsAdapter: CouponsAdapter? = null
    var mCouponList: ArrayList<RecentCouponModel> = ArrayList()

    var isNewlyAddedSelected = true

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCouponsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { context ->

            kProgressHUD = AppUtils.getKProgressHUD(context)

            couponsAdapter =
                CouponsAdapter(
                    context,
                    mCouponList,
                    AppUtils.Companion.Coupons.NEWLY_ADDED,
                    object : CouponsAdapter.CouponItemClickListener {
                        override fun onItemClick(
                            couponsModel: RecentCouponModel,
                            pageType: AppUtils.Companion.Coupons?
                        ) {
                            val intent = Intent(context, CouponDetailsActivity::class.java)
                            intent.putExtra("couponsModel", couponsModel)
                            intent.putExtra("couponType", pageType?.name)
                            startActivity(intent)
                        }

                        override fun loadMore() {

                        }

                        override fun getCoupon(couponsModel: RecentCouponModel) {

                        }

                        override fun unlockCoupon(couponsModel: RecentCouponModel) {
                            initDialog(context, couponsModel)
                        }

                        override fun favoriteClick(couponsModel: RecentCouponModel, position: Int) {
                            changeFavorite(context, couponsModel, position)
                        }

                    })
            binding.rvCouponsList.layoutManager = LinearLayoutManager(context)
            binding.rvCouponsList.adapter = couponsAdapter

            buttonSelected(context, false)

            binding.rlNewlyAdded.setOnClickListener {
                isNewlyAddedSelected = true
                buttonSelected(context, true)
            }

            binding.rlHistory.setOnClickListener {
                isNewlyAddedSelected = false
                buttonSelected(context, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            fetchCoupons(it, true)
        }
    }

    private fun buttonSelected(context: Context, showNoCoupons: Boolean) {
        setCouponsList(showNoCoupons)
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

    fun setCouponsList(showNoCoupons: Boolean) {
        mCouponList.clear()
        if (isNewlyAddedSelected) {
            myCouponsModel?.newly_added?.let {
                mCouponList.addAll(it)
            }
            couponsAdapter?.setPageType(AppUtils.Companion.Coupons.NEWLY_ADDED)
        } else {
            myCouponsModel?.history?.let {
                mCouponList.addAll(it)
            }
            couponsAdapter?.setPageType(AppUtils.Companion.Coupons.HISTORY)
        }
        couponsAdapter?.notifyDataSetChanged()
        if (mCouponList.size == 0 && showNoCoupons) {
            binding.ivNoRecords.visibility = View.VISIBLE
        } else {
            binding.ivNoRecords.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    private fun fetchCoupons(context: Context, showNoCoupons: Boolean) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.getMyCoupons(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                val json = JSONObject(response ?: "")

                myCouponsModel = Gson().fromJson(json.toString(), MyCouponsModel::class.java)

                setCouponsList(showNoCoupons)

            }

            override fun onErrorResponse(error: String?) {
                kProgressHUD?.dismiss()
                setCouponsList(showNoCoupons)
                AppUtils.showToast(error ?: "", context)
            }
        })
    }

    private fun initDialog(context: FragmentActivity, couponsModel: RecentCouponModel): AlertDialog? {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(false)
        val layoutDialog = LayoutRedeemCouponBinding.inflate(LayoutInflater.from(context))
        val dialogView: View = layoutDialog.root
        alertBuilder.setView(dialogView)
        val alertDialog = alertBuilder.create()
        alertDialog.show()

        layoutDialog.cvUnlock.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent(context, UnlockCouponActivity::class.java)
            intent.putExtra("couponsModel", couponsModel)
            startActivity(intent)
        }

        layoutDialog.tvNo.setOnClickListener {
            alertDialog.dismiss()
        }

        layoutDialog.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        return alertDialog
    }

    private fun changeFavorite(context: FragmentActivity, couponsModel: RecentCouponModel, position: Int) {
        kProgressHUD?.show()
        NetworkClass.callApi(URLApi.setFavorite(couponsModel.id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                kProgressHUD?.dismiss()
                AppUtils.showToast(message ?: "", context)
                couponsModel.is_favourited = !(couponsModel.is_favourited ?: false)
                couponsAdapter?.notifyItemChanged(position)
            }

            override fun onErrorResponse(error: String?) {

            }
        })
    }

}