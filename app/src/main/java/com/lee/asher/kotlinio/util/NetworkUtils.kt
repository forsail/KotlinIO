package com.lee.asher.kotlinio.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by lihong on 2017/8/21.
 */
object NetworkUtils {

    fun isNetConnected(context: Context): Boolean {
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectManager.activeNetworkInfo
        if (networkInfo == null) {
            return false
        } else {
            return networkInfo.isAvailable && networkInfo.isConnected
        }
    }

    fun isNetConnectedForType(context: Context, type: Int): Boolean {
        if (!isNetConnected(context)) {
            return false
        }
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo = connectManager.getNetworkInfo(type)
        if (networkInfo == null) {
            return false
        } else {
            return networkInfo.isAvailable && networkInfo.isConnected
        }
    }

    fun isPhoneNetConnected(context: Context): Boolean {
        val type = ConnectivityManager.TYPE_MOBILE
        return isNetConnectedForType(context, type)
    }

    fun isWifiNetConnected(context: Context): Boolean {
        val type = ConnectivityManager.TYPE_WIFI
        return isNetConnectedForType(context, type)
    }

}