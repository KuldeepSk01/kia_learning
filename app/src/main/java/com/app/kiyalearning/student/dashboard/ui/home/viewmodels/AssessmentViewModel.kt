package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AssessmentResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AssessmentViewModel : ViewModel() {


    var validationError = MutableLiveData<String>()
    var assessments=MutableLiveData<AssessmentResponse>()

    fun getAssessments(context: Context)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,String>()
       // map["status"] = id.toString()


        val call: Call<AssessmentResponse> =  RestManager.getInstance().getAssessments(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<AssessmentResponse> {
            override fun onResponse(call: Call<AssessmentResponse>, response: Response<AssessmentResponse>) {
                if (response.isSuccessful) {
                    assessments.value = response.body()
                }else
                {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<AssessmentResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
