package com.app.kiyalearning.student.dashboard.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.StudentActivityViewProfileBinding
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.ProfileViewModel
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyCountry
import com.app.kiyalearning.util.*
import com.bumptech.glide.Glide
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ViewProfileActivity : AppCompatActivity() {

    lateinit var binding: StudentActivityViewProfileBinding
    private val pickImage = 100
    private lateinit var viewModel: ProfileViewModel
    private val countryList= ArrayList<MyCountry>()
    private val countryNameList= ArrayList<String>()
    private var conId="-1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= StudentActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        setProfileData()
        setUpViewModel()


        binding.userImage.setOnClickListener {
            UtilClass.aadharPhotoPath=null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                startActivityForResult(gallery, pickImage)
            } else {
                val permit= Permissions.verifyStoragePermissions(this)
                if(permit== PackageManager.PERMISSION_GRANTED) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }
            }

        }

        binding.updateButton.setOnClickListener{

            val firstName=binding.fNameEdit.text.toString()
            val gender=binding.genderAutoEdit.text.toString()
            val dob=binding.dob.text.toString()
            val email=binding.emailEdit.text.toString()
            val mobile=binding.mobile.text.toString()
            val location=conId


            if(firstName.isEmpty())
             binding.fNameEdit.error="Field can't be empty"
            else if(mobile.isEmpty() || !AppValidator.isValidMobile(mobile))
                binding.mobile.error="Please Enter Valid Mobile"
            else if(email.isEmpty() || !AppValidator.isValidEmail(email))
                binding.mobile.error="Please Enter Valid Email"
            else
            {
                if(MyNetworks.isNetworkAvailable(this))
                {
                    fieldsEnabled(false)
                    binding.loader.pB.visibility = View.VISIBLE
                    //   map["notification_token"] = AppPref.getFirebaseToken(this)

                    val map = HashMap<String, Any>()
                    map["name"] = firstName
                    map["email"] = email
                    map["location"] = location
                    map["dob"] = dob
                    map["gender"] = gender
                    map["phone"] = mobile
                    map["profile"] = ""
                   // map["gender"] = binding.genderAutoEdit.text


                    var file= File("")
                    if(UtilClass.currentPhotoPath!=null)
                        file = File(UtilClass.currentPhotoPath)


                    var withPhoto=false
                    if(UtilClass.currentPhotoPath!=null && UtilClass.currentPhotoPath.isNotBlank() && file.exists())
                        withPhoto=true


                    if(withPhoto)
                    {
                        //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)

                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val imageFileName = "JPEG_$timeStamp.jpeg"
                        val multipartBody = MultipartBody.Part.createFormData("profile", imageFileName, requestFile)

                        if(MyNetworks.isNetworkAvailable(applicationContext))
                            viewModel.updateProfile(map,this,multipartBody)

                    }else
                    {
                        val imageBody=null
                        if(MyNetworks.isNetworkAvailable(applicationContext))
                            viewModel.updateProfile(map,this,imageBody)

                    }
                }

            }
        }

        binding.backIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.countrySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if(position!=0)
                if(countryList.size>(position-1))
                   conId=countryList[position-1].id
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }



        //for pdf file
        /*binding.editButton.setOnClickListener{
                   UtilClass.currentPhotoPath=null
                    val permit= Permissions.verifyStoragePermissions(this)
                    if(permit== PackageManager.PERMISSION_GRANTED)
                    {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "application/pdf"
                        startActivityForResult(Intent.createChooser(intent, "Select PDF file"), FILE_CHOOSER)
                    }
        }*/


        setEventListener(this, KeyboardVisibilityEventListener {
               val layout = binding.nestedScrollView
               val params  = layout.layoutParams as ViewGroup.MarginLayoutParams

               val dip = 200f
               val r: Resources = resources
               var px = TypedValue.applyDimension(
                   TypedValue.COMPLEX_UNIT_DIP,
                   dip,
                   r.displayMetrics
               )
               if(!it)
                   px=0f

               params.bottomMargin=px.toInt()
               layout.layoutParams = params
           })

    }

    private fun setSpinners() {
        val profileAdapter= MySpinnerAdapter(this,R.layout.my_spinner_item, countryNameList)
        profileAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown)
        binding.countrySpinner.adapter = profileAdapter
        // show hint
        // show hint
        // binding.genderSpinner.setSelection(genderAdapter.count)

    }

    private fun fieldsEnabled(status:Boolean)
    {
        binding.updateButton.isEnabled=status
    }


    private fun setProfileData() {
        Glide.with(binding.root)
            .load(AppPref.getUserImage(this))
            .fitCenter()
            .placeholder(R.drawable.user_dp)
            .into(binding.userImage)

        binding.fNameEdit.setText(AppPref.getUserName(this))
        binding.genderAutoEdit.setText(AppPref.getUserGender(this))
        binding.dob.setText(AppPref.getUserDob(this))
        binding.emailEdit.setText(AppPref.getUserEmail(this))
        binding.mobile.setText(AppPref.getUserMob(this))
//        binding.address.setText(AppPref.getUserAddress(this))


    }

    override fun onDestroy() {
        super.onDestroy()
        UtilClass.currentPhotoPath=null
        UtilClass.aadharPhotoPath=null
        UtilClass.panPhotoPath=null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            UtilClass.currentPhotoPath=
                UtilClass.getPathFromURI(data!!.data,this)
            binding.userImage.setImageURI(data.data)

        }
        else
            Toast.makeText(this,"Nothing  selected",Toast.LENGTH_SHORT).show()

    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.updateProfileResponse.observe(this) {
            binding.loader.pB.visibility=View.GONE
            fieldsEnabled(true)
            if (it.success) {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            } else {
                viewModel.validationError.value=it.message
            }
        }


        viewModel.validationError.observe(this) {
            fieldsEnabled(true)
            binding.loader.pB.visibility=View.GONE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.profileDetailResponse.observe(this) {
            binding.loader.pB.visibility=View.GONE
            if (it.success) {
                AppPref.updateProfileData(this,it.data)
                setProfileData()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.countryListResponse.observe(this) {
            binding.loader.pB.visibility=View.GONE
            if (it.success) {
                countryList.clear()
                countryNameList.clear()
                countryList.addAll(it.data)
                countryNameList.add("Select Country")
                Log.d("MyTag", "setUpViewModel: $countryList")
                for(item in countryList)
                {
                    countryNameList.add(item.location)
                }
                setSpinners()

               for ((index,name) in countryNameList.withIndex())
               {
                   if(AppPref.getUserAddress(this)==name)
                   {
                       binding.countrySpinner.setSelection(index)
                       conId=countryList[index-1].id
                   }
               }

            }else
                viewModel.validationError.value=it.message
        }

    }

    override fun onResume() {
        super.onResume()
        if(MyNetworks.isNetworkAvailable(this))
        {
            binding.loader.pB.visibility=View.VISIBLE
            viewModel.getCountryList(this)
        }
    }


}