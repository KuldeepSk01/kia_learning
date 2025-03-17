package com.app.kiyalearning.teacher.dashboard.ui.home

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.databinding.ActivityAddTestScoreBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeries
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.TeacherTestSeriesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.google.gson.Gson
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class AddScoreActivity : AppCompatActivity() {

    var binding: ActivityAddTestScoreBinding?=null
    private lateinit var viewModel: TeacherTestSeriesViewModel
    private var test: TestSeries? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddTestScoreBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()

        setData()

        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

//        if(test==null)
//            onBackPressedDispatcher.onBackPressed()

        binding!!.testFile.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(test!!.teacherFile))
            startActivity(browserIntent)
        }

        binding!!.studentFile.setOnClickListener{
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(test!!.studentFile))
                startActivity(browserIntent)
        }

        binding!!.updateButton.setOnClickListener{

            val obtainMark=binding!!.obtainedMarks.text.toString()

           if(obtainMark.isEmpty())
                binding!!.obtainedMarks.error="Field can't be empty"
            else
            {
                //   map["notification_token"] = AppPref.getFirebaseToken(this)

                if(MyNetworks.isNetworkAvailable(this)) {
                    fieldsEnabled(false)
                    binding!!.loader.pB.visibility = View.VISIBLE
                    viewModel.updateScore(this,test!!.id,obtainMark)
                }
            }
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

    private fun setData() {
        val gson = Gson()
        val strObj = intent.getStringExtra("test")
        test = gson.fromJson(strObj, TestSeries::class.java)

        binding!!.groupName.setText(test!!.groupName)
        binding!!.testName.setText(test!!.testName)
        binding!!.testType.setText(test!!.testType)
        binding!!.dateChooserTextView.text = test!!.date
        binding!!.totalMarks.setText(test!!.marks)
        binding!!.testFile.text = test!!.teacherFile
        binding!!.studentFile.text = test!!.studentFile

    }

    private fun fieldsEnabled(b: Boolean) {
        binding!!.updateButton.isEnabled=b
    }


    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[TeacherTestSeriesViewModel::class.java]

        viewModel.updateScoreResponse.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            fieldsEnabled(true)
            if (it.success) {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                binding!!.obtainedMarks.isEnabled=false
            }
            else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            fieldsEnabled(true)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }



    }





}
