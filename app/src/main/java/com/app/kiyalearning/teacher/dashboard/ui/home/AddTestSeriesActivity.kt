package com.app.kiyalearning.teacher.dashboard.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ActivityAddTestSeriesBinding
import com.app.kiyalearning.databinding.ChatFileSelectorBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.Group
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.TeacherTestSeriesViewModel
import com.app.kiyalearning.util.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddTestSeriesActivity : AppCompatActivity() {

    var binding: ActivityAddTestSeriesBinding?=null
    private val groupNameList= ArrayList<Group>()
    private lateinit var viewModel: TeacherTestSeriesViewModel
    private val pickImage = 100
    private val FILE_CHOOSER = 101
    private var mediaType=""
    var imageDataList: MutableList<ByteArray> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddTestSeriesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()

        if (MyNetworks.isNetworkAvailable(this)){
            binding!!.loader.pB.visibility= View.VISIBLE
            viewModel.getTestsGroupName(this)
        }

        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding!!.datePickerBox.setOnClickListener {
            val newFragment = DatePickerFragment( binding!!.dateChooserTextView,binding!!.root.context)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding!!.uploadTestFileCard.setOnClickListener {
            val dialog = BottomSheetDialog(this, R.style.DialogStyle)
            val bottomSheet = ChatFileSelectorBinding.inflate(layoutInflater)
            bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }
            bottomSheet.imageFile.setOnClickListener{
                UtilClass.currentPhotoPath=null
                val permit= Permissions.verifyStoragePermissions(this)
                if(permit!= PackageManager.PERMISSION_GRANTED)
                else
                {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }
                dialog.dismiss()
            }
            bottomSheet.pdfFile.setOnClickListener{
                UtilClass.currentPhotoPath=null
                val permit= Permissions.verifyStoragePermissions(this)
                if(permit!= PackageManager.PERMISSION_GRANTED)
                else
                {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    startActivityForResult(Intent.createChooser(intent, "Select PDF file"), FILE_CHOOSER)

                }
                dialog.dismiss()
            }
            dialog.setContentView(bottomSheet.root)
            dialog.show()
        }

        binding!!.aadharCardEdit.setOnClickListener {
            binding!!.uploadTestFileCard.performClick()
        }

        binding!!.pdfEdit.setOnClickListener {
            binding!!.uploadTestFileCard.performClick()
        }

        binding!!.updateButton.setOnClickListener{

            val testName=binding!!.testName.text.toString()
            val testType=binding!!.testType.text.toString()
            val totalMarks=binding!!.totalMarks.text.toString()
            val date=binding!!.dateChooserTextView.text.toString()
            var groupId=-1L

            if(binding!!.groupNameSpinner.selectedItemPosition==0)
            {
             Toast.makeText(this,"Select Group Name",Toast.LENGTH_SHORT).show()
            }
            else if(testName.isEmpty())
                binding!!.testName.error="Field can't be empty"
            else if(testType.isEmpty())
                binding!!.testType.error="Field can't be empty"
            else if(totalMarks.isEmpty())
                binding!!.totalMarks.error="Field can't be empty"
            else if(date.isEmpty())
                binding!!.dateChooserTextView.error="Field can't be empty"
            else
            {
                groupId=groupNameList[binding!!.groupNameSpinner.selectedItemPosition-1].id

                //   map["notification_token"] = AppPref.getFirebaseToken(this)

                    if(mediaType.contains("image",true))
                     {
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
                            val multipartBody = MultipartBody.Part.createFormData("teacher_file", imageFileName, requestFile)

                            if(MyNetworks.isNetworkAvailable(this)) {
                                fieldsEnabled(false)
                                binding!!.loader.pB.visibility = View.VISIBLE
                                viewModel.uploadTestSeries(this,testName,date, groupId =groupId.toString(),testType,totalMarks,multipartBody )
                            }
                        }

                    }
                else
                    {
                        val requestFile: RequestBody = RequestBody.create(
                            "application/pdf".toMediaTypeOrNull(),
                            imageDataList[0]
                        )
                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val imageFileName = "JPEG_$timeStamp.pdf"
                        val multipartBody = MultipartBody.Part.createFormData("teacher_file", imageFileName, requestFile)

                        if(MyNetworks.isNetworkAvailable(this)) {
                            fieldsEnabled(false)
                            binding!!.loader.pB.visibility = View.VISIBLE
                            viewModel.uploadTestSeries(this,testName,date, groupId =groupId.toString(),testType,totalMarks,multipartBody )
                        }
                    }
            }
        }

        if (MyNetworks.isNetworkAvailable(this)){
            viewModel.getTestSeries(this)
            binding!!.loader.pB.visibility= View.VISIBLE
        }

        KeyboardVisibilityEvent.setEventListener(this) {
            val layout = binding!!.nestedScrollView
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams

            val dip = 200f
            val r: Resources = resources
            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            if (!it)
                px = 0f

            params.bottomMargin = px.toInt()
            layout.layoutParams = params
        }
    }

    private fun fieldsEnabled(b: Boolean) {
        binding!!.updateButton.isEnabled=b
    }


    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[TeacherTestSeriesViewModel::class.java]

        viewModel.uploadTestResponse.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            fieldsEnabled(true)
            if (it.success) {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                onBackPressedDispatcher.onBackPressed()
            }
            else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            fieldsEnabled(true)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.groupNames.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            if (it.success) {
                groupNameList.clear()
                groupNameList.addAll(it.data)
                val groupNames= ArrayList<String>()
                groupNames.add("Select Group")
                for(name in groupNameList)
                {
                    groupNames.add(name.groupName)
                }

                val groupAdapter= MySpinnerAdapter(this, R.layout.my_spinner_item, groupNames)
                groupAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown)
                binding!!.groupNameSpinner.adapter = groupAdapter
            }
            else
                viewModel.validationError.value=it.message
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        UtilClass.currentPhotoPath=null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {

            mediaType="image"
            UtilClass.currentPhotoPath=UtilClass.getPathFromURI(data!!.data,this)

            Glide.with(binding!!.root)
                .load(UtilClass.currentPhotoPath)
                .fitCenter()
                .into(binding!!.docImage)

            binding!!.imageLayout.visibility=View.VISIBLE
            binding!!.pdfLayout.visibility=View.GONE
            binding!!.uploadTestFileCard.visibility=View.GONE

        }
        else if (resultCode == RESULT_OK && requestCode == FILE_CHOOSER) {
            //media type used for end fcm message
            mediaType="pdf"
            UtilClass.currentPhotoPath=UtilClass.getPathFromURI(data!!.data,this)

            val imageDataList: MutableList<ByteArray> = ArrayList()
            val imageUri: Uri = data.data!!
            imageDataList.add(UtilClass.getBytes(imageUri,this))
            this.imageDataList=imageDataList

            binding!!.imageLayout.visibility=View.GONE
            binding!!.pdfLayout.visibility=View.VISIBLE
            binding!!.uploadTestFileCard.visibility=View.GONE
        }
        else
        {
            Toast.makeText(this,"Nothing  selected",Toast.LENGTH_SHORT).show()
        }
    }

}

class DatePickerFragment(private val dateTextView : TextView, private val context2 : Context) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog=DatePickerDialog(context2, this, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 10000
        // Create a new instance of DatePickerDialog and return it

        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val date = StringBuilder()
        date.append(year).append("-").append(month+1).append("-").append(day)
        dateTextView.text = date.toString()
    }

}