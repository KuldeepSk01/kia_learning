package com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.*
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
    var feesListResponse=MutableLiveData<FeesListResponse>()
    var teacherSlotsResponse=MutableLiveData<TeacherSlotsResponse>()
    var updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    var profileDetailResponse = MutableLiveData<ProfileDetailResponse>()
    var countryListResponse = MutableLiveData<CountryListResponse>()


    fun updateProfile(map: HashMap<String, Any>,context: Context, image : MultipartBody.Part?, aadharImage : MultipartBody.Part?, panImage : MultipartBody.Part?)
    {
        val fName: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["first_name"].toString())
        val lName: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["last_name"].toString())
        val email: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["email"].toString())
        val address: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["location"].toString())
        val gender: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["gender"].toString())
        val dob: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["dob"].toString())
        val phone: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),map["phone"].toString())

        val call: Call<UpdateProfileResponse> =  RestManager.getInstance().updateTeacherProfile(image, aadhaar_card = aadharImage, pan_card = panImage,
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),
            fName,lName, email, address,gender,dob=dob,phone)

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

    fun getFeesList(context: Context,id:Long)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val map=HashMap<String,String>()
        map["status"] = id.toString()


        val call: Call<FeesListResponse> =  RestManager.getInstance().getFeesList(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<FeesListResponse> {
            override fun onResponse(call: Call<FeesListResponse>, response: Response<FeesListResponse>) {
                if (response.isSuccessful) {
                    feesListResponse.value = response.body()
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
            override fun onFailure(call: Call<FeesListResponse>, t: Throwable) {
                Log.d("MyTag", "onFailure: "+t.message)
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun getSlots(context: Context, year: Int, month: String, date: String)
    {
        //1 for requested
        //2 for rejected
        // 3 for approved
        //4 for ongoing
        //5 for completed
        val dat=year.toString()+month+date

        Log.d("MyTag", "dat: "+dat)

        val map=HashMap<String,Any>()
       // map["status"] = id.toString()
        map["date"] = dat


        val call: Call<TeacherSlotsResponse> =  RestManager.getInstance().getTeacherSlots(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),map)
        call.enqueue(object : Callback<TeacherSlotsResponse> {
            override fun onResponse(call: Call<TeacherSlotsResponse>, response: Response<TeacherSlotsResponse>) {
                if (response.isSuccessful) {
                    teacherSlotsResponse.value = response.body()
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
            override fun onFailure(call: Call<TeacherSlotsResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

   /* fun updateProfile(map: HashMap<String, Any>,context: Context, image : MultipartBody.Part?)
    {
        val fName: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["fName"].toString())
        val lName: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["lName"].toString())
        val email: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["email"].toString())
        val address: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["address"].toString())
        val gender: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["gender"].toString())
        val hotelName: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["hotel_name"].toString())
        val branchName: RequestBody = RequestBody.create(MediaType.parse("text/plain"),map["hotel_branch"].toString())


        val call: Call<UpdateProfileResponse> =  RestManager.getInstance().updateProfile(image, AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context),
            fName,lName, email, address,gender,hotelName=hotelName,branchName=branchName)

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
    }*/

    fun getProfileDetails(context: Context)
    {
        val call: Call<ProfileDetailResponse> = RestManager.getInstance().getTeacherProfileDetails(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))

        call.enqueue(object : Callback<ProfileDetailResponse> {
            override fun onResponse(call: Call<ProfileDetailResponse>, response: Response<ProfileDetailResponse>) {
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
            override fun onFailure(call: Call<ProfileDetailResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
