package com.app.kiyalearning.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.app.kiyalearning.R


class MyNetworks {
    companion object{
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                true
            } else {
                Toast.makeText(context, context.getString(R.string.error_network),Toast.LENGTH_LONG).show()
                false
            }
        }
}
}