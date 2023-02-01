package com.android.woopons.utils

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.android.woopons.R
import com.android.woopons.login.LoginActivity
import com.android.woopons.network.LocalPreference
import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD


class AppUtils {

    companion object {
        fun validEmailPattern(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun convertDpToPixels(context: Context?, dp: Float): Int {
            if (context == null) {
                return dp.toInt()
            }
            val r = context.resources
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.displayMetrics
            ).toInt()
        }

        fun getParams(
            activity: Context?,
            leftMarginInDp: Float,
            topMargInDp: Float,
            rightMarginInDp: Float,
            bottomMarginInDp: Float
        ): LinearLayout.LayoutParams {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Converting pixels into DPI
            params.setMargins(
                convertDpToPixels(activity, leftMarginInDp),
                convertDpToPixels(activity, topMargInDp),
                convertDpToPixels(activity, rightMarginInDp),
                convertDpToPixels(activity, bottomMarginInDp)
            )
            return params
        }

        fun getKProgressHUD(context: Context): KProgressHUD {
            return KProgressHUD.create(context).setCustomView(ProgressHUD(context)).setSize(100, 100).setWindowColor(context.getResources().getColor(android.R.color.transparent)).setCancellable(false)
        }

        fun showToast(message: String, context: Context) {
            if (message == "Unauthenticated.") {
                logout(context)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        fun loadImage(context: Context, url: String?, imageView: ImageView) {
            Glide.with(context).load(Constants.IMAGE_BASE_URL + url).placeholder(R.drawable.ic_placeholder).centerCrop().into(imageView)
        }

        fun getAcronyms(text: String?): String {
            return text?.split(' ')?.mapNotNull { it.firstOrNull()?.toString() }?.reduce { acc, s -> acc + s }?.uppercase() ?: ""
        }

        private fun logout(context: Context) {
            Toast.makeText(
                context,
                "Your session has expired. Please try to log in again",
                Toast.LENGTH_LONG
            ).show()
            clearCache()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        fun clearCache() {
            LocalPreference.shared.removeAll()
        }

        enum class Dashboard {
            CATEGORIES, RECENTLY_ADDED, TOP_BRANDS, TRENDING_CATEGORIES
        }

        enum class Coupons {
            FAVORITES, CATEGORIES, TOP_BRANDS, NEWLY_ADDED, HISTORY
        }

    }


}
