package com.finderbar.jovian.utilities
import android.content.Context
import android.net.ConnectivityManager
import com.finderbar.jovian.utilities.AppConstants.NETWORK_STATUS_MOBILE
import com.finderbar.jovian.utilities.AppConstants.NETWORK_STATUS_NOT_CONNECTED
import com.finderbar.jovian.utilities.AppConstants.TYPE_WIFI
import com.finderbar.jovian.utilities.AppConstants.TYPE_MOBILE
import com.finderbar.jovian.utilities.AppConstants.TYPE_NOT_CONNECTED
import com.finderbar.jovian.utilities.AppConstants.NETWORK_STATUS_WIFI


object NetworkUtil {
    private fun getConnectivityStatus(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): Int {
        val conn = getConnectivityStatus(context)
        var status = 0
        when (conn) {
            TYPE_WIFI -> status = NETWORK_STATUS_WIFI
            TYPE_MOBILE -> status = NETWORK_STATUS_MOBILE
            TYPE_NOT_CONNECTED -> status = NETWORK_STATUS_NOT_CONNECTED
        }
        return status
    }
}