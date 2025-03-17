package com.app.kiyalearning.teacher.dashboard.ui.home.slider

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.BottomSheetStudentFilterLayoutBinding
import com.app.kiyalearning.databinding.ChatFileSelectorBinding
import com.app.kiyalearning.databinding.FragmentClassesBinding
import com.app.kiyalearning.student.dashboard.ui.home.slider.DatePickerFragment
import com.app.kiyalearning.student.dashboard.ui.home.slider.MyFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.MyClassesAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyClass
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ClassesViewModel
import com.app.kiyalearning.util.ContentUriUtils
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.Permissions
import com.app.kiyalearning.util.UtilClass
import com.atwa.filepicker.core.FilePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
/*
import okhttp3.internal.Util
*/
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class CompletedFragment : MyFragment(), MyClassesAdapter.OnHomeWorkListener {

    private lateinit var binding: FragmentClassesBinding
    private val classList = ArrayList<MyClass>()
    private lateinit var classesAdapter: MyClassesAdapter
    private lateinit var viewModel: ClassesViewModel

    private lateinit var homeWorkFile: Uri
    private lateinit var myClass: MyClass
    var dateTextViewId = ""
    var studentName = ""

    private val filePicker = FilePicker.getInstance(this)


    private val FILE_CHOOSER = 101
    private val pickImage = 100

    private var cursorPath=""
    private var mediaType=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater)
        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        classesAdapter = MyClassesAdapter(classList, "completed", viewModel, binding)
        binding.recycleView.adapter = classesAdapter

        classesAdapter.onSetCompleteListener(this@CompletedFragment)

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getClassesList(
                    requireActivity(), "completed", dateTextViewId, studentName
                )
            }
        }

        binding.searchGroup.hint = getString(R.string.search_by_student_name)

        binding.searchGroup.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                studentName = s.toString()
                callApis()
            }
        })

        binding.dateChooserTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (binding.dateChooserTextView.text != "Select Date") dateTextViewId =
                    binding.dateChooserTextView.text.toString()
            }
        })

        binding.datePickerBox.setOnClickListener {
            val newFragment =
                DatePickerFragment(binding.dateChooserTextView, binding.root.context, this)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }


        return binding.root
    }

    override fun callApis() {
        if (MyNetworks.isNetworkAvailable(requireActivity())) {
            viewModel.getClassesList(requireActivity(), "completed", dateTextViewId, studentName)
            binding.loader.pB.visibility = View.VISIBLE
        }
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ClassesViewModel::class.java]

        viewModel.classesListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                classList.clear()
                classList.addAll(it.data)

                /*val timeoutClass =
                    classList.filter { UtilClass.isClassTimeExpired(it.classTimestamp) == true }  //if class will not be expired it will  return list and remove from current list
                classList.removeAll(timeoutClass)
*/
                binding.tvClassCount.text = String.format("%s %s",getString(R.string.class_count),"(${classList.size.toString()})")


                Log.d("MyTag", "complete class list : $classList ")

                if (classList.isEmpty()) showNoShiftDesign(true)
                else showNoShiftDesign(false)

                classesAdapter.notifyDataSetChanged()
            } else viewModel.validationError.value = it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.acceptRejectCompletedResponse.observe(viewLifecycleOwner) {
            viewModel.getClassesList(requireActivity(), "completed", dateTextViewId, studentName)
            binding.loader.pB.visibility = View.VISIBLE
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }

        /*for add technical report teacher can add technical report*/
       /* viewModel.addTechnicalReportStatus.observe(viewLifecycleOwner) {
            UtilClass.mToast(requireActivity(),it.message)
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }*/

    }

    override fun onResume() {
        super.onResume()
        callApis()

        val activity = requireActivity() as com.app.kiyalearning.teacher.dashboard.DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
            val bottomSheet = BottomSheetStudentFilterLayoutBinding.inflate(layoutInflater)
            bottomSheet.byNameFilteredTextView.text = "By Student Name"
            bottomSheet.cancelTextView.setOnClickListener { dialog.dismiss() }

            bottomSheet.resetTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
                callApis()
            }

            bottomSheet.byDateFilteredTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.dateChooserTextView.text = "Select Date"
                binding.datePickerBox.visibility = View.VISIBLE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
            }

            bottomSheet.byNameFilteredTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.searchGroup.setText("")
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.VISIBLE
                dialog.dismiss()
            }

            dialog.setContentView(bottomSheet.root)
            dialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        val activity = requireActivity() as com.app.kiyalearning.teacher.dashboard.DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener(null)
    }

    private fun showNoShiftDesign(status: Boolean) {
        if (status) {
            binding.noShiftImage.visibility = View.VISIBLE
            binding.noShiftTextView.visibility = View.VISIBLE
        } else {
            binding.noShiftImage.visibility = View.GONE
            binding.noShiftTextView.visibility = View.GONE
        }
    }

    private val imageSelectActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("MyTAG", "selected image uri ${it.data} ")
            if (it.resultCode==RESULT_OK && it.data!=null){

                mediaType = "image"

                val data = it?.data
                val realFilePathWithoutCompress =  UtilClass.getPathFromURI(data!!.data,requireContext())
                val realFilePath = compressImageFilePath(BitmapFactory.decodeFile(realFilePathWithoutCompress),requireContext())

                if(MyNetworks.isNetworkAvailable(requireActivity().applicationContext))
                {
                    val file = File(realFilePath)
                    if(UtilClass.checkFileSize(file) < 10){
                        binding.loader.pB.visibility=View.VISIBLE
                        val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)

                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        var imageFileName = "JPEG_$timeStamp.jpeg"
                        //for getting real name
                        imageFileName= UtilClass.getFileName(requireActivity().contentResolver,data.data)

                        val multipartBody = MultipartBody.Part.createFormData("home_work_file", imageFileName, requestFile)
                        Log.d("MyTag", "ImageFilePath: $file")
                        viewModel.uploadHomework(requireContext(),myClass.id,multipartBody)
                        viewModel.homeWorkFileResponse.observe(requireActivity(),Observer{
                            Log.d("MyTag", "Image uploaded successfully: ${it.message}")
                        })
                    }else{
                        Toast.makeText(requireContext(),"Image should be less then or equal to 10MB",Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(),"Nothing  selected",Toast.LENGTH_SHORT).show()
            }
        }

    override fun onUploadFile(mClass: MyClass) {
        Log.d("MyTag","onclick class $mClass....")

        myClass = mClass
        val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val bottomSheet = ChatFileSelectorBinding.inflate(layoutInflater)
        bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }
        bottomSheet.imageFile.setOnClickListener {
            UtilClass.currentPhotoPath = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                Log.d("MyTag", "setInitialSetup: PERMISSION_GRANTED 13")
                imageSelectActivityLauncher.launch(gallery)
            } else {
                val permit = Permissions.verifyStoragePermissions(requireActivity())
                if (permit != PackageManager.PERMISSION_GRANTED)
                else {
                    //   val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    Log.d(
                        "MyTag",
                        "setInitialSetup: PERMISSION_GRANTED" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    )
                    imageSelectActivityLauncher.launch(gallery)
                }
            }
            dialog.dismiss()

        }
        bottomSheet.pdfFile.setOnClickListener{
            try {
                filePicker.pickFile { meta ->
                    val name : String? = meta?.name
                    val sizeKb : Int? = meta?.sizeKb
                    val file : File? = meta?.file
                    if (file != null) {
                        // UtilClass.mToast(requireContext(),"Pick file $file")
                        selectedPdf(file)
                    }else{
                        UtilClass.mToast(requireContext(),"Nothing selected")
                    }
                }
            }catch(e:Exception){
                Log.d("MyTag","Select Pdf Error ${e.message}")
            }

            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet.root)
        dialog.show()
    }

    private fun selectedPdf(files: File) {
        UtilClass.currentPhotoPath = ContentUriUtils.getFilePath(requireContext(),Uri.fromFile(files))
        mediaType = "pdf"
        var file = File("")
        if (UtilClass.currentPhotoPath != null)
            file = File(UtilClass.currentPhotoPath)
        if (UtilClass.checkFileSize(file) < 10) {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                binding.loader.pB.visibility = View.VISIBLE
                val requestFile: RequestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                var imageFileName = "JPEG_$timeStamp.jpeg"

                imageFileName = file.name
                val multipartBody = MultipartBody.Part.createFormData("home_work_file", imageFileName, requestFile)
                viewModel.uploadHomework(requireContext(),myClass.id,multipartBody)
                viewModel.homeWorkFileResponse.observe(requireActivity(), Observer {
                    Log.d("MyTag", "successfully pdf upload: ${it.message}")

                })
            }

        } else {
            Toast.makeText(
                requireContext(),
                "Pdf should be less then or equal 10MB",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onViewFile(mClass: MyClass) {
        if (mClass.classHomeWork?.isNotEmpty()!!) {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(mClass.classHomeWork))
            startActivity(i)
        } else {
            Toast.makeText(requireActivity(), "No Homework uploaded !", Toast.LENGTH_SHORT).show()
        }
    }


    private fun compressImageFilePath(bm: Bitmap, context: Context): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File(context.filesDir, timeStamp + ".png")
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 50, fos)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(fos != null)
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file.path
    }

}