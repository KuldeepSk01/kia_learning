package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AcceptRejectResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.DashBoardDataResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.NotificationsResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {


    var validationError = MutableLiveData<String>()
    var dashBoardDataResponse=MutableLiveData<DashBoardDataResponse>()

    var notificationsResponse = MutableLiveData<NotificationsResponse>()
    var addTechnicalReportStatus=MutableLiveData<AcceptRejectResponse>()

    var loginWithSiblingIdResponse = MutableLiveData<LoginResponse>()



    fun getNotificationsList(context: Context)
    {
        val map=HashMap<String,Any>()
        //  map["consumption_id"] = consumptionId

        val call: Call<NotificationsResponse> =  RestManager.getInstance().getStudentNotifications(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                if (response.body() != null) {
                    notificationsResponse.value = response.body()
                }else
                {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val msg = jObjError.getString("message")
                        if (msg.equals("Unauthenticated.")){
                            AppPref.userLogout(context)
                            val intent =  Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        }else{
                            validationError.value =msg
                        }
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


    /*fun getShiftList(context: Context)
    {
        val map=HashMap<String,String>()
        //  map["consumption_id"] = consumptionId
        val call: Call<ShiftListResponse> =  RestManager.getInstance().getShiftList(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<ShiftListResponse> {
            override fun onResponse(call: Call<ShiftListResponse>, response: Response<ShiftListResponse>) {
                if (response.isSuccessful) {
                    shiftListResponse.value = response.body()
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
            override fun onFailure(call: Call<ShiftListResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }*/

    fun getDashBoardData(context: Context)
    {
        val map=HashMap<String,String>()
        //  map["consumption_id"] = consumptionId
        val call: Call<DashBoardDataResponse> =  RestManager.getInstance().getStudentDashBoardData(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<DashBoardDataResponse> {
            override fun onResponse(call: Call<DashBoardDataResponse>, response: Response<DashBoardDataResponse>) {
                if (response.isSuccessful) {
                    dashBoardDataResponse.value = response.body()
                }else
                {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val msg = jObjError.getString("message")
                        if (msg.equals("Unauthenticated.")){
                            AppPref.userLogout(context)
                            val intent =  Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)

                        }else{
                            validationError.value =msg
                        }
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<DashBoardDataResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }



    fun loginWithSiblingId(context: Context,userId:String)
    {
        val map=HashMap<String,Any>()
        map["user_id"] = userId
        map["user_type"] = "student"
        map["device_token"] = AppPref.getFirebaseToken(context)
        map["device_type"] = "android"

        Log.d("TAG","login with id ${map.toString()}")

        val call: Call<LoginResponse> =  RestManager.getInstance().loginWithSibling(map)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginWithSiblingIdResponse.value = response.body()
                    Log.d("MyTag","token ${response.body()?.data?.token}")
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
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


    fun addStudentTechnicalReportStatus(context: Context,userType:String,className:String,issue:String,comment:String)
    {
        Log.d("TEACHER", ":Technical report $userType, $className, $issue,$comment")

        val call: Call<AcceptRejectResponse> =  RestManager.getInstance().addTechnicalStatusReport(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),userType,issue,className,comment)

        call.enqueue(object : Callback<AcceptRejectResponse> {
            override fun onResponse(call: Call<AcceptRejectResponse>, response: Response<AcceptRejectResponse>) {
                if (response.isSuccessful) {
                    addTechnicalReportStatus.value = response.body()
                }else
                {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val msg = jObjError.getString("message")
                        if (msg.equals("Unauthenticated.")){
                            AppPref.userLogout(context)
                            val intent =  Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        }else{
                            validationError.value =msg
                        }
                    } catch (e: Exception) {
                        Log.d("TEACHER","error ${e.message}")
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<AcceptRejectResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
