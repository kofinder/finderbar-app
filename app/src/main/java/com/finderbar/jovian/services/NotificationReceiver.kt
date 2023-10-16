package com.finderbar.jovian.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.app.ActivityManager



class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("FirebaseDataReceiver", "Key: $ Value: $")

        if (isMyServiceRunning(context!!, NotificationReceiver::class.java)) {
            println("alredy running no need to start again")
        } else {
            val background = Intent(context, NotificationReceiver::class.java)
            context.startService(background)
        }
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)

        if (services != null) {
            for (i in services.indices) {
                if (serviceClass.name == services[i].service.className && services[i].pid != 0) {
                    return true
                }
            }
        }
        return false
    }
}