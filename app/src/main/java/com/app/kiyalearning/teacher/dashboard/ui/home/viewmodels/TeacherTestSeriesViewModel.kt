package com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.chat.pojo.SaveMessageResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.GroupNameResponse
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


class TeacherTestSeriesViewModel : ViewModel() {
    var validationError = MutableLiveData<String>()
    var tests=MutableLiveData<TestSeriesResponse>()
    var uploadTestResponse=MutableLiveData<SaveMessageResponse>()
    var updateScoreResponse=MutableLiveData<SaveMessageResponse>()
    var groupNames=MutableLiveData<GroupNameResponse>()

    fun getTestSeries(context: Context)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,String>()
       // map["status"] = id.toString()


        val call: Call<TestSeriesResponse> =  RestManager.getInstance().getTeacherTestSeries(
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

    fun uploadTestSeries(context: Context,testName : String,date : String,groupId : String,testType : String,marks : String,teacherFile : MultipartBody.Part?)
    {
        val testName1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),testName)
        val date1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),date)
        val groupId1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),groupId)
        val testType1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),testType)
        val marks1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),marks)


        val call: Call<SaveMessageResponse> =  RestManager.getInstance().uploadTestSeries(
            teacherFile, AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),  testName1,date1,groupId1,testType1,marks1)

        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.isSuccessful) {
                    uploadTestResponse.value = response.body()
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

    fun getTestsGroupName(context: Context)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,String>()
        // map["status"] = id.toString()


        val call: Call<GroupNameResponse> =  RestManager.getInstance().getTeacherGroupNames(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<GroupNameResponse> {
            override fun onResponse(call: Call<GroupNameResponse>, response: Response<GroupNameResponse>) {
                if (response.isSuccessful) {
                    groupNames.value = response.body()
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
            override fun onFailure(call: Call<GroupNameResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun updateScore(context: Context,testSeriesId:String,scored:String)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,Any>()
        map["test_series_id"] = testSeriesId
        map["scored"] = scored

        val call: Call<SaveMessageResponse> =  RestManager.getInstance().updateTestScore(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),map)
        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.isSuccessful) {
                    updateScoreResponse.value = response.body()
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
            override fun onFailure(call: Call<SaveMessageResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

}
