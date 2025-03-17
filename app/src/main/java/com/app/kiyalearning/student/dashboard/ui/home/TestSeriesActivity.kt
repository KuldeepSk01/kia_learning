package com.app.kiyalearning.student.dashboard.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ActivityStudentTestSeriesBinding
import com.app.kiyalearning.databinding.ChatFileSelectorBinding
import com.app.kiyalearning.student.dashboard.ui.home.adapters.StudentTestSeriesAdapter
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeries
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.TestSeriesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.Permissions
import com.app.kiyalearning.util.UtilClass
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TestSeriesActivity : AppCompatActivity() {

    var binding: ActivityStudentTestSeriesBinding?=null
    private val testList= ArrayList<TestSeries>()
    private lateinit var testSeriesStudentAdapter : StudentTestSeriesAdapter
    private lateinit var viewModel: TestSeriesViewModel
    private val pickImage = 100
    private val FILE_CHOOSER = 101
    var testSeriesId=""
    var selectedTab=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStudentTestSeriesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()


        binding!!.recycleView.layoutManager = LinearLayoutManager(this)
        testSeriesStudentAdapter= StudentTestSeriesAdapter(testList,this)
        binding!!.recycleView.adapter = testSeriesStudentAdapter

        binding!!.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                viewModel.getStudentTestSeries(this)
            }
        }

        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding!!.assignedTextView.setOnClickListener{
            selectedTab=0
            if (MyNetworks.isNetworkAvailable(this)){
                viewModel.getStudentTestSeries(this)
                binding!!.loader.pB.visibility= View.VISIBLE
            }
            binding!!.assignedTextView.setTextColor(getColor(R.color.black))
            binding!!.doneTextView.setTextColor(getColor(R.color.gray2))
        }
        binding!!.doneTextView.setOnClickListener{
            selectedTab=1
            if (MyNetworks.isNetworkAvailable(this)){
                viewModel.getStudentTestSeries(this)
                binding!!.loader.pB.visibility= View.VISIBLE
            }

            binding!!.assignedTextView.setTextColor(getColor(R.color.gray2))
            binding!!.doneTextView.setTextColor(getColor(R.color.black))

        }


        if (MyNetworks.isNetworkAvailable(this)){
            viewModel.getStudentTestSeries(this)
            binding!!.loader.pB.visibility= View.VISIBLE
        }

    }



    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[TestSeriesViewModel::class.java]

        viewModel.tests.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            if (it.success) {
                testList.clear()

                if(selectedTab==1)
                for (item in it.data)
                {
                    if(item.status.equals("done",true))
                    {
                      testList.add(item)
                    }
                }

                if(selectedTab==0)
                    for (item in it.data)
                    {
                        if(!item.status.equals("done",true))
                        {
                            testList.add(item)
                        }
                    }


                if (testList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                testSeriesStudentAdapter.notifyDataSetChanged()
            }
            else
                viewModel.validationError.value=it.message
        }

        viewModel.saveStudentFileResponse.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                if (MyNetworks.isNetworkAvailable(this)){
                    viewModel.getStudentTestSeries(this)
                    binding!!.loader.pB.visibility= View.VISIBLE
                }

            }
//            }else
//                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    }

    private fun showNoShiftDesign(status:Boolean)
    {
        if(status)
        {
            binding!!.noShiftImage.visibility= View.VISIBLE
            binding!!.noShiftTextView.visibility= View.VISIBLE
        }else
        {
            binding!!.noShiftImage.visibility= View.GONE
            binding!!.noShiftTextView.visibility= View.GONE
        }

    }

    fun uploadFile(testSeriesId:String) {
        this.testSeriesId=testSeriesId
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {

            UtilClass.currentPhotoPath=UtilClass.getPathFromURI(data!!.data,this)
            //mssgImgUrl= data.data

            var file= File("")
            if(UtilClass.currentPhotoPath!=null)
                file = File(UtilClass.currentPhotoPath)

            var withPhoto=false
            Log.d("MyTag", "file.exists(): "+file.exists())
            Log.d("MyTag", "UtilClass.currentPhotoPath==${UtilClass.currentPhotoPath}")


            if(UtilClass.currentPhotoPath!=null && UtilClass.currentPhotoPath.isNotBlank() && file.exists())
                withPhoto=true

            if(withPhoto)
            {
                if(MyNetworks.isNetworkAvailable(applicationContext))
                    {
                        binding!!.loader.pB.visibility=View.VISIBLE
                        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)

                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val imageFileName = "JPEG_$timeStamp.jpeg"
                        val multipartBody = MultipartBody.Part.createFormData("student_file", imageFileName, requestFile)

                        viewModel.saveStudentFile( applicationContext, testSeriesId = testSeriesId
                            ,multipartBody)
                    }
            }else
            {
                /*val body=null
                Log.d("MyTag", "withPhoto: $withPhoto")
                viewModel.callProfileUpdateImageApi(map,this,body)*/
                Toast.makeText(this,R.string.server_error,Toast.LENGTH_SHORT).show()
            }

        }
        else if (resultCode == RESULT_OK && requestCode == FILE_CHOOSER) {

            //media type used for end fcm message
            UtilClass.currentPhotoPath=UtilClass.getPathFromURI(data!!.data,this)

            val imageDataList: MutableList<ByteArray> = java.util.ArrayList()
            val imageUri: Uri = data.data!!
            imageDataList.add(UtilClass.getBytes(imageUri,this))
            // pass the byte array list to be uploaded.


                //Toast.makeText(this,"We are working on chatting Module",Toast.LENGTH_SHORT).show()
            if(MyNetworks.isNetworkAvailable(applicationContext))
                {
                    binding!!.loader.pB.visibility=View.VISIBLE
                    //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)

                    val requestFile: RequestBody = RequestBody.create(
                        "application/pdf".toMediaTypeOrNull(),
                        imageDataList[0]
                    )
                    // Create an image file name
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val imageFileName = "JPEG_$timeStamp.pdf"
                    val multipartBody = MultipartBody.Part.createFormData("student_file", imageFileName, requestFile)

                    viewModel.saveStudentFile( applicationContext, testSeriesId,multipartBody)
                }
        }
        else
        {
            Toast.makeText(this,"Nothing  selected",Toast.LENGTH_SHORT).show()
        }
    }

}