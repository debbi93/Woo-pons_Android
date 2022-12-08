package com.android.woopons.network

import android.content.Context
import android.content.SharedPreferences
import com.android.woopons.base.BaseApplication
import com.android.woopons.models.MyAccountModel
import com.android.woopons.models.UserDataModel
import com.google.gson.Gson

class LocalPreference(private val mContext: Context?) {

    fun removeAll() {
        editor?.apply {
            clear()
            apply()
        }
    }

    var isRember: Boolean?
        get() = preferences?.getString("isRem", "") ?: "" == "true"
        set(token) {
            editor?.apply {
                putString("isRem", if (token ?: false) "true" else "false")
                apply()
            }
        }

    var user: UserDataModel?
        get() {
            val stUser = preferences?.getString("UserData", "") ?: ""
            if (stUser.isEmpty()) {
                return null
            }
            return Gson().fromJson(stUser, UserDataModel::class.java)
        }
        set(newValue) {
            val userString = Gson().toJson(newValue)
            editor?.apply {
                putString("UserData", userString)
                apply()
            }
        }

    var myAccount: MyAccountModel?
        get() {
            val stUser = preferences?.getString("MyAccount", "") ?: ""
            if (stUser.isEmpty()) {
                return null
            }
            return Gson().fromJson(stUser, MyAccountModel::class.java)
        }
        set(newValue) {
            val userString = Gson().toJson(newValue)
            editor?.apply {
                putString("MyAccount", userString)
                apply()
            }
        }

    var token: String?
        get() = preferences?.getString("AuthToken", "") ?: ""
        set(token) {
            editor?.apply {
                putString("AuthToken", token)
                apply()
            }
        }

    var role: String?
        get() = preferences?.getString("Role", "") ?: ""
        set(Role) {
            editor?.apply {
                putString("Role", Role)
                apply()
            }
        }
    var password: String?
        get() = preferences?.getString("Password", "") ?: ""
        set(Pasword) {
            editor?.apply {
                putString("Password", Pasword)
                apply()
            }
        }
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    companion object {
        @JvmField
        var shared = LocalPreference(BaseApplication.instance)
    }

    init {
        preferences = mContext?.getSharedPreferences(
            BaseApplication.instance?.packageName, Context.MODE_PRIVATE
        )
        editor = preferences?.edit()
        editor?.apply()
    }


}