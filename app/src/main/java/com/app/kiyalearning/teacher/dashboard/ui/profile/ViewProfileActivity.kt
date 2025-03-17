package com.app.kiyalearning.teacher.dashboard.ui.profile

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
import com.app.kiyalearning.databinding.ActivityViewProfileBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyCountry
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ProfileViewModel
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

    lateinit var binding: ActivityViewProfileBinding
    private val pickImage = 100
    private val aadharPickImage = 101
    private val panPickImage = 102
    private val FILE_CHOOSER = 101
    private lateinit var viewModel: ProfileViewModel
    private val countryList= ArrayList<MyCountry>()
    private val countryNameList= ArrayList<String>()
    private var conId="-1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewProfileBinding.inflate(layoutInflater)
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

        binding.aadharCardEdit.setOnClickListener {
            UtilClass.aadharPhotoPath=null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                startActivityForResult(gallery, aadharPickImage)
            } else {

                val permit= Permissions.verifyStoragePermissions(this)
                if(permit== PackageManager.PERMISSION_GRANTED) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, aadharPickImage)
                }
            }
        }

        binding.panCardEdit.setOnClickListener {
            UtilClass.panPhotoPath=null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                startActivityForResult(gallery, panPickImage)
            } else {

                val permit= Permissions.verifyStoragePermissions(this)
                if(permit== PackageManager.PERMISSION_GRANTED) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, panPickImage)
                }
            }

        }

        binding.uploadAadharCard.setOnClickListener {
            UtilClass.aadharPhotoPath=null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                startActivityForResult(gallery, aadharPickImage)
            } else {

                val permit= Permissions.verifyStoragePermissions(this)
                if(permit== PackageManager.PERMISSION_GRANTED) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, aadharPickImage)
                }
            }

        }

        binding.uploadPanCard.setOnClickListener {
            UtilClass.panPhotoPath=null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                startActivityForResult(gallery, panPickImage)
            } else {

                val permit= Permissions.verifyStoragePermissions(this)
                if(permit== PackageManager.PERMISSION_GRANTED) {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, panPickImage)
                }
            }


        }

        binding.updateButton.setOnClickListener{

            val firstName=binding.fNameEdit.text.toString()
            val lastName=binding.lNameEdit.text.toString()
            val gender=binding.genderAutoEdit.text.toString()
            val dob=binding.dob.text.toString()
            val email=binding.emailEdit.text.toString()
            val mobile=binding.mobile.text.toString()
            val location=conId


            if(firstName.isEmpty())
             binding.fNameEdit.error="Field can't be empty"
            else if(lastName.isEmpty())
                binding.lNameEdit.error="Field can't be empty"
            else if(email.isEmpty())
                binding.lNameEdit.error="Field can't be empty"
            else if(mobile.isEmpty() || !AppValidator.isValidMobile(mobile))
                binding.mobile.error="Please Enter Valid Mobile"
            else if(email.isEmpty() || !AppValidator.isValidEmail(email))
                binding.emailEdit.error="Please Enter Valid Email"
            else
            {
                if(MyNetworks.isNetworkAvailable(this))
                {
                    fieldsEnabled(false)
                    binding.loader.pB.visibility = View.VISIBLE
                    //   map["notification_token"] = AppPref.getFirebaseToken(this)

                    val map = HashMap<String, Any>()
                    map["first_name"] = firstName
                    map["last_name"] = lastName
                    map["email"] = email
                    map["location"] = location
                    map["dob"] = dob
                    map["gender"] = gender
                    map["profile"] = ""
                    map["aadhaar_card"] = ""
                    map["pan_card"] = ""
                    map["phone"] = mobile
                   // map["gender"] = binding.genderAutoEdit.text


                    var file= File("")
                    var aadharFile= File("")
                    var panFile= File("")

                    if(UtilClass.currentPhotoPath!=null)
                        file = File(UtilClass.currentPhotoPath)

                    if(UtilClass.aadharPhotoPath!=null)
                        aadharFile = File(UtilClass.aadharPhotoPath)

                    if(UtilClass.panPhotoPath!=null)
                        panFile = File(UtilClass.panPhotoPath)


                    var withPhoto=false
                    if(UtilClass.currentPhotoPath!=null && UtilClass.currentPhotoPath.isNotBlank() && file.exists())
                        withPhoto=true

                    var aadharPhoto=false
                    if(UtilClass.aadharPhotoPath!=null && UtilClass.aadharPhotoPath.isNotBlank() && aadharFile.exists())
                        aadharPhoto=true

                    var panPhoto=false
                    if(UtilClass.panPhotoPath!=null && UtilClass.panPhotoPath.isNotBlank() && panFile.exists())
                        panPhoto=true

                    if(withPhoto)
                    {
                        //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)
                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val imageFileName = "JPEG_$timeStamp.jpeg"
                        val multipartBody = MultipartBody.Part.createFormData("profile", imageFileName, requestFile)


                        if(aadharPhoto)
                        {
                            //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                            val aadharRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),aadharFile)

                            // Create an image file name
                            val aadharImageFileName = "AADHAR_JPEG_$timeStamp.jpeg"
                            val aadharMultipartBody = MultipartBody.Part.createFormData("aadhaar_card", aadharImageFileName, aadharRequestFile)

                            if(panPhoto)
                            {
                                //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                                val panRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),panFile)

                                // Create an image file name
                                val panImageFileName = "PAN_JPEG_$timeStamp.jpeg"
                                val panMultipartBody = MultipartBody.Part.createFormData("pan_card", panImageFileName, panRequestFile)

                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                {
                                    viewModel.updateProfile(map,this,multipartBody,aadharMultipartBody,panMultipartBody)
                                }

                            }else
                            {
                                val body=null
                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                    viewModel.updateProfile(map,this,multipartBody,aadharMultipartBody,body)

                            }


                        }else
                        {
                            val aadharBody=null
                            if(panPhoto)
                            {
                                //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                                val panRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),panFile)

                                // Create an image file name
                                val panImageFileName = "PAN_JPEG_$timeStamp.jpeg"
                                val panMultipartBody = MultipartBody.Part.createFormData("pan_card", panImageFileName, panRequestFile)

                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                {
                                    viewModel.updateProfile(map,this,multipartBody,aadharBody,panMultipartBody)
                                }

                            }else
                            {
                                val panBody=null
                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                    viewModel.updateProfile(map,this,multipartBody,aadharBody,panBody)

                            }

                        }

                    }else
                    {
                        val imageBody=null
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        if(aadharPhoto)
                        {
                            //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                            val aadharRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),aadharFile)

                            // Create an image file name
                            val aadharImageFileName = "AADHAR_JPEG_$timeStamp.jpeg"
                            val aadharMultipartBody = MultipartBody.Part.createFormData("aadhaar_card", aadharImageFileName, aadharRequestFile)

                            if(panPhoto)
                            {
                                Log.d("MyTag", "userImg=null  aadhar=not null  pan= not null ")
                                //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                                val panRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),panFile)

                                // Create an image file name
                                val panImageFileName = "PAN_JPEG_$timeStamp.jpeg"
                                val panMultipartBody = MultipartBody.Part.createFormData("pan_card", panImageFileName, panRequestFile)

                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                {
                                    viewModel.updateProfile(map,this,imageBody,aadharMultipartBody,panMultipartBody)
                                }

                            }else
                            {
                                Log.d("MyTag", "userImg=null  aadhar=not null  pan= null ")
                                val body=null
                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                    viewModel.updateProfile(map,this,imageBody,aadharMultipartBody,body)

                            }


                        }else
                        {
                            val aadharBody=null
                            if(panPhoto)
                            {
                                //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
                                val panRequestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),panFile)

                                // Create an image file name
                                val panImageFileName = "PAN_JPEG_$timeStamp.jpeg"
                                val panMultipartBody = MultipartBody.Part.createFormData("pan_card", panImageFileName, panRequestFile)

                                Log.d("MyTag", "userImg=null  aadhar=null  pan=not null ")

                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                {
                                    viewModel.updateProfile(map,this,imageBody,aadharBody,panMultipartBody)
                                }

                            }else
                            {
                                Log.d("MyTag", "userImg=null  aadhar=null  pan=null ")

                                val panBody=null
                                if(MyNetworks.isNetworkAvailable(applicationContext))
                                    viewModel.updateProfile(map,this,imageBody,aadharBody,panBody)

                            }

                        }


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
        binding.fNameEdit.setText(AppPref.getUserFirstName(this))
        binding.lNameEdit.setText(AppPref.getUserLastName(this))
        binding.genderAutoEdit.setText(AppPref.getUserGender(this))
        binding.dob.setText(AppPref.getUserDob(this))
        binding.emailEdit.setText(AppPref.getUserEmail(this))
        binding.mobile.setText(AppPref.getUserMob(this))
//        binding.address.setText(AppPref.getUserAddress(this))


        if(AppPref.getUserAadharCard(this)=="" )
        {
            binding.aadharCardLayout.visibility=View.GONE
        }else
        {
            binding.uploadAadharCard.visibility=View.GONE
            Glide.with(this)
                .load(AppPref.getUserAadharCard(this))
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(binding.aadharCard)
        }

        if(AppPref.getUserPanCard(this)=="")
        {
            binding.panCardLayout.visibility=View.GONE
        }else
        {
            binding.uploadPanCard.visibility=View.GONE
            Glide.with(this)
                .load(AppPref.getUserPanCard(this))
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(binding.panCard)
        }
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
        else if (resultCode == RESULT_OK && requestCode == aadharPickImage) {
            UtilClass.aadharPhotoPath=
                UtilClass.getPathFromURI(data!!.data,this)
            binding.aadharCard.setImageURI(data.data)

            Glide.with(binding.root)
                .load(data.data)
                .fitCenter()
                .placeholder(R.drawable.logo)
                .into(binding.aadharCard)

            binding.aadharCardLayout.visibility=View.VISIBLE
            binding.uploadAadharCard.visibility=View.GONE

        }
        else if (resultCode == RESULT_OK && requestCode == panPickImage) {
            UtilClass.panPhotoPath=
                UtilClass.getPathFromURI(data!!.data,this)
            binding.panCard.setImageURI(data.data)
            binding.panCardLayout.visibility=View.VISIBLE
            binding.uploadPanCard.visibility=View.GONE
        }
       /* else if (resultCode == RESULT_OK && requestCode == FILE_CHOOSER) {
            UtilClass.currentPhotoPath=
               UtilClass.getPathFromURI(data!!.data,this)
           // mssgImgUrl= data.data



            val imageDataList: MutableList<ByteArray> = ArrayList()
            val imageUri: Uri = data.data!!
            imageDataList.add(UtilClass.getBytes(imageUri,this))
            // pass the byte array list to be uploaded.

                //Toast.makeText(this,"We are working on chatting Module",Toast.LENGTH_SHORT).show()
                if(MyNetworks.isNetworkAvailable(applicationContext))
                {
                    binding.loader.pB.visibility=View.VISIBLE
                    fieldsEnabled(false)
                    //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)

                    val requestFile: RequestBody = RequestBody.create(MediaType.parse("application/pdf"),
                        imageDataList[0]
                    )
                    // Create an image file name
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val imageFileName = "GST_FILE_$timeStamp.pdf"
                    val multipartBody = MultipartBody.Part.createFormData("gst", imageFileName, requestFile)
                    viewModel.updateProfileGST(applicationContext,multipartBody)
                }
        }*/
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
                AppPref.updateTeacherProfileData(this,it.data)
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