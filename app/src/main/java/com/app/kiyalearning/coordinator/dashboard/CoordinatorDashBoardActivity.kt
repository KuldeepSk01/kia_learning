package com.app.kiyalearning.coordinator.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.ChatActivity
import com.app.kiyalearning.chat.pojo.NotificationGroup
import com.app.kiyalearning.coordinator.dashboard.ui.notifications.NotificationActivity
import com.app.kiyalearning.databinding.CoordinatorActivityDashboardBinding
import com.app.kiyalearning.student.dashboard.ui.home.HomeFragment
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.UtilClass
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


class CoordinatorDashBoardActivity : AppCompatActivity() {

    var binding: CoordinatorActivityDashboardBinding?=null
    private lateinit var  navController:NavController
    private var doubleBackToExit= false
    var homeFragment: HomeFragment?=null
    var classesSelectedTab=0

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= CoordinatorActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        checkForUpdate()

        UtilClass.setStatusBarProperty ( this )


        Log.d("MyTag", "onCreate getUserType: "+AppPref.getUserType(this))

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) -> {
                // You can use the API that requires the permission.
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }




//        val appBarConfiguration = AppBarConfiguration.Builder (
//            R.id.navigation_home , R.id.navigation_courses , R.id.navigation_community, R.id.navigation_profile )
//            .build ( )
         navController = Navigation.findNavController ( this , R.id.nav_host_fragment_activity_dashboard )
        // NavigationUI.setupActionBarWithNavController ( this , navController , appBarConfiguration )
        NavigationUI.setupWithNavController ( binding!!.navView , navController )

        binding!!.dashboardNotificationLay.setOnClickListener{
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        if (intent.getBooleanExtra("IS_NOTIFICATION",false))
        {
            binding!!.dashboardNotificationLay.performClick()
        }
       else if (intent.getBooleanExtra("IS_CHAT_ACTIVITY",false))
        {
            try {
                val chat = intent.getSerializableExtra("groupModel") as NotificationGroup
                val myIntenet = Intent(this, ChatActivity::class.java)
                Log.d("Notification","Coordinator Dashboard data $chat")
                myIntenet.putExtra("GROUP_NAME", chat.name)
                myIntenet.putExtra("GROUP_IMAGE", chat.groupIcon)
                myIntenet.putExtra("GROUP_ID", chat.id.toString())
                myIntenet.putExtra("SORT_BY_TEACHER", chat.sortByTeacher)
                myIntenet.putExtra("SORT_BY_STUDENT", chat.sortByStudent)
                myIntenet.putExtra("SORT_BY_COORDINATOR", chat.sortByCoordinator)

                startActivity(myIntenet)
                //finish()
            }catch (e:Exception){
                Log.d("Notification","Student Dashboard data Error ${e.message}")
            }

        }

    }



        // Handle item selection
    override fun onBackPressed() {
        if (binding?.navView!!.selectedItemId != R.id.navigation_home) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            if (doubleBackToExit) {
               finish()
            }
            doubleBackToExit = true
            Toast.makeText(this, getString(R.string.press_back_again_exit), Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExit = false }, 2000)
        }
    }

    private fun checkForUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this@CoordinatorDashBoardActivity)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // an activity result launcher registered via registerForActivityResult
                    activityResultLauncher,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE ).build()
                )
            }
        }
    }


    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            // handle callback
            if (result.resultCode != AppCompatActivity.RESULT_OK) {
                Log.d(
                    "App Update",
                    "Update flow failed! Result code: " + result.resultCode
                );
                // If the update is canceled or fails,
                // you can request to start the update again.
            }else{
                Log.d(
                    "App Update",
                    "App Update is available "
                );
            }


        }


}