package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.chat.pojo.SaveMessageResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeriesResponse
import com.app.kiyalearning.util.AppPref
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TestSeriesViewModel : ViewModel() {
    var validationError = MutableLiveData<String>()
    var tests=MutableLiveData<TestSeriesResponse>()
    var saveStudentFileResponse=MutableLiveData<SaveMessageResponse>()

    fun getStudentTestSeries(context: Context)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,String>()
       // map["status"] = id.toString()


        val call: Call<TestSeriesResponse> =  RestManager.getInstance().getStudentTestSeries(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<TestSeriesResponse> {
            override fun onResponse(call: Call<TestSeriesResponse>, response: Response<TestSeriesResponse>) {
                if (response.isSuccessful) {
                    tests.value = response.body()
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
            override fun onFailure(call: Call<TestSeriesResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun saveStudentFile(context: Context,testSeriesId : String,image : MultipartBody.Part?)
    {
        val testSeriesId1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),testSeriesId)

        val call: Call<SaveMessageResponse> =  RestManager.getInstance().saveStudentTestFile(
            image, AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),  testSeriesId1)

        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.isSuccessful) {
                    saveStudentFileResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<SaveMessageResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

}
