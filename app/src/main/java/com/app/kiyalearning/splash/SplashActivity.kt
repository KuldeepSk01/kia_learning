package com.app.kiyalearning.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.BuildConfig
import com.app.kiyalearning.chat.pojo.Group
import com.app.kiyalearning.chat.pojo.GroupChannel
import com.app.kiyalearning.chat.pojo.NotificationGroup
import com.app.kiyalearning.coordinator.dashboard.CoordinatorDashBoardActivity
import com.app.kiyalearning.databinding.ActivitySplashBinding
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.UtilClass
import com.google.gson.Gson

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)
        binding.versionTextView.text = String.format("%s %s", "Version", BuildConfig.VERSION_NAME)


//        val pixel= windowManager.defaultDisplay.width
//        val dp =pixel/ resources.displayMetrics.density
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (AppPref.getUserEmail(applicationContext) == "")
                startActivity(Intent(applicationContext, IntroActivity::class.java))
            else {
                // if(AppPref.getUserType(applicationContext))
                //      startActivity(Intent(applicationContext, DashBoardActivity::class.java))
                //  else
                //    startActivity(Intent(applicationContext, LocationPermissionActivity::class.java))
                var actInt = Intent(
                    applicationContext,
                    com.app.kiyalearning.teacher.dashboard.DashBoardActivity::class.java
                )
                if (AppPref.getUserType(this) == "student") {
                    actInt = Intent(binding.root.context, DashBoardActivity::class.java)
                } else if (AppPref.getUserType(this) == "coordinator" || AppPref.getUserType(this) == "sub admin") {
                    actInt = Intent(binding.root.context, CoordinatorDashBoardActivity::class.java)

                    try {

                        val data =  intent.getStringExtra("group_data")
                        val groupIcon =  intent.getStringExtra("group_icon")
                        val gson = Gson()
                        val nData = gson.fromJson(data, NotificationGroup::class.java)
                        nData.groupIcon = groupIcon

                        /// Log.d("Notification", "Group info from notif $data")
                        Log.d("Notification", "converted data from notif $nData")
                        actInt.putExtra("IS_CHAT_ACTIVITY", true)
                        actInt.putExtra("groupModel", nData)
                        // actInt.putExtra("group_icon", groupIcon)
                        Log.d("Notificaton","data from notif splash $nData")

                    }catch (e:Exception){
                        Log.d("MyTag","Group Chat Exception ${e.message}")
                    }
                }

                if (intent.getStringExtra("data_type").equals("notification", true)) {
                    actInt.putExtra("IS_NOTIFICATION", true)
                } else if (intent.getStringExtra("data_type").equals("message", true)) {
                    try {

                        val data =  intent.getStringExtra("group_data")
                        val groupIcon =  intent.getStringExtra("group_icon")
                        val gson = Gson()
                        val nData = gson.fromJson(data, NotificationGroup::class.java)
                        nData.groupIcon = groupIcon

                       /// Log.d("Notification", "Group info from notif $data")
                        Log.d("Notification", "converted data from notif $nData")
                        actInt.putExtra("IS_CHAT_ACTIVITY", true)
                        actInt.putExtra("groupModel", nData)
                       // actInt.putExtra("group_icon", groupIcon)
                        Log.d("Notificaton","data from notif splash $nData")

                    }catch (e:Exception){
                        Log.d("MyTag","Group Chat Exception ${e.message}")
                    }


                } else if (intent.getStringExtra("data_type").equals("join_class", true)) {
                    actInt.putExtra("OPEN_CLASSES_TAB", true)
                }

                startActivity(actInt)
            }
            finish()
        }
        handler.postDelayed(runnable, 2500)
    }


    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }


}




