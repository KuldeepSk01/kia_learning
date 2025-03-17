package com.app.kiyalearning

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    }

    companion object {
        var mFirebaseAnalytics: FirebaseAnalytics? = null
    }


}