package com.app.kiyalearning.student.dashboard.ui.notifications.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.chat.pojo.RetrieveChatResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.NotificationsResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationViewModel : ViewModel() {

   // var profileDetailsResponse = MutableLiveData<ProfileDetailsResponse>()
    var validationError = MutableLiveData<String>()
    var notificationsResponse = MutableLiveData<NotificationsResponse>()

  //  lateinit var selectedImageUrl: String

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
                        validationError.value =jObjError.getString("message")
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

    fun markReadNotification(context: Context)
    {
        val map=HashMap<String,Any>()
        map["user_type"] = AppPref.getUserType(context)

        val call: Call<RetrieveChatResponse> = RestManager.getInstance().markReadChat(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
            map)
        call.enqueue(object : Callback<RetrieveChatResponse> {
            override fun onResponse(call: Call<RetrieveChatResponse>, response: Response<RetrieveChatResponse>) {
                // Log.d("MyTag", "response raw="+response.raw())
                // Log.d("MyTag", "response body="+response.body())
                if (response.body() != null) {
                    //  retrieveChatResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<RetrieveChatResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

}
