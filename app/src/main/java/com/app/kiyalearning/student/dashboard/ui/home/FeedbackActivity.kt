package com.app.kiyalearning.student.dashboard.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.R
import com.app.kiyalearning.api.Api
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.databinding.ActivityFeedbackBinding
import com.app.kiyalearning.databinding.BottomSheetAddFeedbackBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AddFeedbackResponse
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FeedbackActivity : AppCompatActivity() {

    var binding: ActivityFeedbackBinding?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

      //  setUpViewModel()

        val className=intent.getStringExtra("CLASS_NAME").toString()
        val classSubject=intent.getStringExtra("CLASS_SUBJECT").toString()
        val classDate=intent.getStringExtra("CLASS_DATE").toString()
        val classTime=intent.getStringExtra("CLASS_TIME").toString()
        val classStatus=intent.getStringExtra("CLASS_STATUS").toString()
        val classCreatedDate=intent.getStringExtra("CLASS_CREATED_DATE").toString()
        val classFeedback=intent.getStringExtra("CLASS_FEEDBACK").toString()
        val classProfile=intent.getStringExtra("CLASS_PROFILE").toString()
        val classId=intent.getStringExtra("CLASS_ID").toString()
        val hasFeedBack=intent.getLongExtra("HAS_FEEDBACK",0)


        binding!!.replierName.text=AppPref.getUserName(this)
        binding!!.replierReply.text=classFeedback
        Glide.with(binding!!.root)
            .load(AppPref.getUserImage(this))
            .fitCenter()
            .placeholder(R.drawable.logo)
            .into(binding!!.userImage)

        if(classFeedback.isNotBlank())
        {
            binding!!.feedbackButtonLayout.visibility=View.GONE
            binding!!.feedbackTextView.visibility=View.VISIBLE
            binding!!.replierLayout.visibility=View.VISIBLE
        }else
        {
            binding!!.feedbackButtonLayout.visibility=View.VISIBLE
            binding!!.replierLayout.visibility=View.GONE
            binding!!.feedbackTextView.visibility=View.GONE
        }

        binding!!.addButton.setOnClickListener{
            openAddFeedbackBottomSheet(classId)
        }


        binding!!.name.text=className
        binding!!.subject.text=classSubject
        binding!!.classDate.text=classDate
        binding!!.classTime.text=classTime
        binding!!.status.text=classStatus
        binding!!.receiveOn.text=classCreatedDate

        if(classStatus=="completed")
            binding!!.status.setTextColor(binding!!.root.context.getColor(R.color.green))


        Glide.with(binding!!.root)
            .load(classProfile)
            .fitCenter()
            .placeholder(R.drawable.logo)
            .into(binding!!.userImage)



        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


    private fun openAddFeedbackBottomSheet(classId: String) {
        val dialog = BottomSheetDialog(this, R.style.DialogStyle)
        val bottomSheet = BottomSheetAddFeedbackBinding.inflate(layoutInflater)
        bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }

        bottomSheet.submitButton.setOnClickListener{
            val feedback=bottomSheet.feedback.text.toString()
            if(feedback.isBlank())
                Toast.makeText(bottomSheet.root.context,"Please enter feedback", Toast.LENGTH_SHORT).show()
            else
            {
                if(MyNetworks.isNetworkAvailable(this))
                {
                    // fieldsEnabled(false)
                    bottomSheet.progressBarLayout.visibility=View.VISIBLE
                    val map=HashMap<String,String>()
                    map["class_id"] = classId
                    map["feedback"] = feedback
                    val token= AppPref.getUserToken(this)

                    Log.d("MyTag", "class id: "+map)

                    val api: Api = RestManager.getInstance()
                    val call: Call<AddFeedbackResponse> = api.addFeedback("Bearer $token",map)
                    call.enqueue(object : Callback<AddFeedbackResponse> {
                        override fun onResponse(call: Call<AddFeedbackResponse>, response: Response<AddFeedbackResponse>) {
                            if (response.isSuccessful) {
                                if(response.body()!!.success)
                                {
                                    bottomSheet.progressBarLayout.visibility=View.GONE
                                    dialog.dismiss()
                                    Toast.makeText(this@FeedbackActivity,response.body()!!.message,Toast.LENGTH_LONG).show()
                                    setFeedback(feedback)
                                }else
                                {
                                    bottomSheet.progressBarLayout.visibility=View.GONE
                                    Toast.makeText(this@FeedbackActivity,response.body()!!.message,Toast.LENGTH_LONG).show()
                                }
                            }else
                            {
                                try {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    bottomSheet.progressBarLayout.visibility=View.GONE
                                    Toast.makeText(this@FeedbackActivity,jObjError.getString("message"),Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    bottomSheet.progressBarLayout.visibility=View.GONE
                                    Toast.makeText(this@FeedbackActivity,e.message,Toast.LENGTH_LONG).show()
                                }
                            }


                        }

                        override fun onFailure(call: Call<AddFeedbackResponse>, t: Throwable) {
                            bottomSheet.progressBarLayout.visibility=View.GONE
                            Toast.makeText(this@FeedbackActivity,getString(R.string.server_error),Toast.LENGTH_LONG).show()
                        }
                    })

                }
            }

        }

        dialog.setContentView(bottomSheet.root)
        dialog.show()
    }

    private fun setFeedback(feedback:String) {
        binding!!.replierName.text=AppPref.getUserName(this)
        binding!!.replierReply.text=feedback
        Glide.with(binding!!.root)
            .load(AppPref.getUserImage(this))
            .fitCenter()
            .placeholder(R.drawable.logo)
            .into(binding!!.userImage)

        binding!!.feedbackTextView.visibility=View.VISIBLE
        binding!!.replierLayout.visibility=View.VISIBLE
        binding!!.feedbackButtonLayout.visibility=View.GONE
    }


    /* private fun setUpViewModel(){
         viewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

         viewModel.assessments.observe(this) {
             binding!!.loader.pB.visibility= View.GONE
             binding!!.pullToRefresh.isRefreshing = false
             if (it.status==200L) {
                 assessmentList.clear()
                 assessmentList.addAll(it.data)

                 if (assessmentList.isEmpty())
                     showNoShiftDesign(true)
                 else
                     showNoShiftDesign(false)

                 assessmentAdapter.notifyDataSetChanged()
             }
 //            }else
 //                viewModel.validationError.value=it.message
         }

         viewModel.validationError.observe(this) {
             binding!!.loader.pB.visibility= View.GONE
             binding!!.pullToRefresh.isRefreshing = false
             Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
         }
     }*/


}