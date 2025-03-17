package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.student.dashboard.ui.home.pojos.ProfileDetailResponse
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


class ProfileViewModel : ViewModel() {
    var validationError = MutableLiveData<String>()
    var updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    var profileDetailResponse = MutableLiveData<ProfileDetailResponse>()
    var countryListResponse = MutableLiveData<CountryListResponse>()


    fun updateProfile(map: HashMap<String, Any>,context: Context, image : MultipartBody.Part?)
    {
        val fName: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["name"].toString())
        val email: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["email"].toString())
        val address: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["location"].toString())
        val gender: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["gender"].toString())
        val dob: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["dob"].toString())
        val phone: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["phone"].toString())

        val call: Call<UpdateProfileResponse> =  RestManager.getInstance().updateStudentProfile(image ,AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),
            fName, email, address,gender,dob=dob,phone)

        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    updateProfileResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
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

    fun getProfileDetails(context: Context)
    {
        val call: Call<ProfileDetailResponse> = RestManager.getInstance().getStudentProfileDetails(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))

        call.enqueue(object : Callback<ProfileDetailResponse> {
            override fun onResponse(call: Call<ProfileDetailResponse>, response: Response<ProfileDetailResponse>) {
                if (response.isSuccessful) {
                    profileDetailResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<ProfileDetailResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
