package com.app.kiyalearning.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.kiyalearning.R
import com.app.kiyalearning.auth.LoginActivity
import com.app.kiyalearning.databinding.ActivityIntroBinding
import com.app.kiyalearning.util.UtilClass
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class IntroActivity : AppCompatActivity() {
    companion object{
        fun  getNumPages(): Int{ return 3 }
    }

    private lateinit var viewPager: ViewPager2
    lateinit var binding: ActivityIntroBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForUpdate()

        //transparent status bar
        UtilClass.setStatusBarProperty(this)

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = binding.introViewPager2

        val pagerAdapter: FragmentStateAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.firstSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.app_theme_color)
                        )
                        binding.secondSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray)
                        )
                        binding.thirdSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray)
                        )
                    }
                    1 -> {
                        binding.firstSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray)
                        )
                        binding.secondSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.app_theme_color)
                        )
                        binding.thirdSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray))

                    }
                    else -> {
                        binding.firstSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray)
                        )
                        binding.secondSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.much_light_gray)
                        )
                        binding.thirdSlideFragmentPoint.setCardBackgroundColor(
                            ContextCompat.getColor(applicationContext, R.color.app_theme_color))

                    }
                }
            }
        })

        binding.skipButton.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            if(binding.loginButton.text.toString()==getString(R.string.login))
            {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
                else
                    viewPager.currentItem=viewPager.currentItem+1

        }


        //firebase token
      /*  FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("MyTag", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
           // val msg = getString(R.string.msg_token_fmt, token)
            Log.d("MyTag", "token=$token")
         //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })*/



    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            onBackPressedDispatcher.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
        onBackPressedDispatcher.onBackPressed()
    }


    private fun checkForUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this@IntroActivity)
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



private class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return IntroActivity.getNumPages()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment:Fragment = when (position) {
            0 -> ScreenSliderFragment1()
            1 -> ScreenSliderFragment2 ()
            else -> ScreenSliderFragment3 ()
        }
        return fragment
    }

}
