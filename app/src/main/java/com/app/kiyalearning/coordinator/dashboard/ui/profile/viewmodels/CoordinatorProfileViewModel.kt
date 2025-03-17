package com.app.kiyalearning.coordinator.dashboard.ui.profile.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.student.dashboard.ui.home.pojos.CoordinatorProfileDetailResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.UpdateProfileResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.CountryListResponse
import com.app.kiyalearning.util.AppPref
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CoordinatorProfileViewModel : ViewModel() {
    var validationError = MutableLiveData<String>()
    var updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    var profileDetailResponse = MutableLiveData<CoordinatorProfileDetailResponse>()
    var countryListResponse = MutableLiveData<CountryListResponse>()


    fun updateCoordinatorProfile(map: HashMap<String, Any>,context: Context, image : MultipartBody.Part?)
    {
        val fName: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["first_name"].toString())
        val lName: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["last_name"].toString())
        val email: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["email"].toString())
        val address: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["address"].toString())
        val joiningDate: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["joining_date"].toString())
        val phone: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["phone"].toString())

        val call: Call<UpdateProfileResponse> =  RestManager.getInstance().updateCoordinatorProfile(image ,AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),
            fName,lName, email, address,joiningDate=joiningDate,phone)

        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    updateProfileResponse.value = response.body()
                } else{
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
            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun getCountryList(context: Context)
    {
        val map=HashMap<String,Any>()

        val call: Call<CountryListResponse> =  RestManager.getInstance().getCountryList(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<CountryListResponse> {
            override fun onResponse(call: Call<CountryListResponse>, response: Response<CountryListResponse>) {
                if (response.isSuccessful) {
                    countryListResponse.value = response.body()
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
            override fun onFailure(call: Call<CountryListResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun getCoordinatorProfileDetails(context: Context)
    {
        val call: Call<CoordinatorProfileDetailResponse> = RestManager.getInstance().getCoordinatorProfileDetails(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))

        call.enqueue(object : Callback<CoordinatorProfileDetailResponse> {
            override fun onResponse(call: Call<CoordinatorProfileDetailResponse>, response: Response<CoordinatorProfileDetailResponse>) {
                if (response.isSuccessful) {
                    profileDetailResponse.value = response.body()
                } else{
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
            override fun onFailure(call: Call<CoordinatorProfileDetailResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

}
