package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AcceptRejectResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentClassesListResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentJoinClassResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.WalletListResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClassesViewModel : ViewModel() {


    var validationError = MutableLiveData<String>()
    var classesListResponse=MutableLiveData<StudentClassesListResponse>()
    var walletListResponse=MutableLiveData<WalletListResponse>()
    var acceptRejectCompletedResponse=MutableLiveData<AcceptRejectResponse>()
    var studentJoinClassResponse=MutableLiveData<StudentJoinClassResponse>()

    fun getClassesList(context: Context,id:String,date:String,subject:String)
    {
        val map=HashMap<String,Any>()
        map["status"] = id
        map["filter_by_subject_name"] = subject
        map["filter_by_class_date"] = date

        Log.d("Classes", "getClassesList: $map")


        val call: Call<StudentClassesListResponse> =  RestManager.getInstance().getStudentClassesList(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),map)
        call.enqueue(object : Callback<StudentClassesListResponse> {
            override fun onResponse(call: Call<StudentClassesListResponse>, response: Response<StudentClassesListResponse>) {
                if (response.isSuccessful) {
                    classesListResponse.value = response.body()

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
            override fun onFailure(call: Call<StudentClassesListResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun getWalletData(context: Context,id:String)
    {
        val map=HashMap<String,Any>()
        map["status"] = id

        val call: Call<WalletListResponse> =  RestManager.getInstance().getStudentWalletList(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),map)
        call.enqueue(object : Callback<WalletListResponse> {
            override fun onResponse(call: Call<WalletListResponse>, response: Response<WalletListResponse>) {
                if (response.isSuccessful) {
                    walletListResponse.value = response.body()
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
            override fun onFailure(call: Call<WalletListResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun acceptRejectCompletedClass(context: Context,id:String,status:String)
    {
        val map=HashMap<String,Any>()
        map["booked_slot_id"] = id
        map["status"] = status

        val call: Call<AcceptRejectResponse> =  RestManager.getInstance().studentAcceptRejectCompletedClasses(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),map)
        call.enqueue(object : Callback<AcceptRejectResponse> {
            override fun onResponse(call: Call<AcceptRejectResponse>, response: Response<AcceptRejectResponse>) {
                if (response.isSuccessful) {
                    acceptRejectCompletedResponse.value = response.body()
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
            override fun onFailure(call: Call<AcceptRejectResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun studentJoinClassStatus(context: Context,classId:Int,deviceName:String)
    {
        Log.d("STUDENT", ":Join Class id $classId")

        val call: Call<StudentJoinClassResponse> =  RestManager.getInstance().studentJoinClassStatus(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),classId,deviceName)
        call.enqueue(object : Callback<StudentJoinClassResponse> {
            override fun onResponse(call: Call<StudentJoinClassResponse>, response: Response<StudentJoinClassResponse>) {
                if (response.isSuccessful) {
                    studentJoinClassResponse.value = response.body()
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
                        Log.d("MyTag","error ${e.message}")
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<StudentJoinClassResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
