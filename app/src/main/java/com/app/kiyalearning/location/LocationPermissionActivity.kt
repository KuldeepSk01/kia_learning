package com.app.kiyalearning.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.kiyalearning.databinding.ActivityLocationPermissionBinding
import com.app.kiyalearning.util.UtilClass
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class LocationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val isCheckOut=intent.getBooleanExtra("IS_CHECK_OUT",false)
            val intent=Intent(this, MapsLocationActivity::class.java)
            intent.putExtra("IS_CHECK_OUT",isCheckOut)
            startActivity(intent)
            finish()
        }


        binding.allowLocationAccessBtn.setOnClickListener { requestLocationPermission() }
    }


    private fun requestLocationPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(this@LocationPermissionActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                        openScreen()
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private fun openScreen(){
        Handler(Looper.getMainLooper()).postDelayed({
         //   val isCheckOut=intent.getBooleanExtra("IS_CHECK_OUT",false)
            val intent=Intent(this, MapsLocationActivity::class.java)
           // intent.putExtra("IS_CHECK_OUT",isCheckOut)
            startActivity(intent)
            finish()
        }, 200)
    }


    override fun onBackPressed() {
        finish()
    }
}