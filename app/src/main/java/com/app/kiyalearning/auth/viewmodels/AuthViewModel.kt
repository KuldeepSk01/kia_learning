package com.app.kiyalearning.auth.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.auth.pojos.ForgetPasswordResponse
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.auth.pojos.ReSendOTPResponse
import com.app.kiyalearning.auth.pojos.SignUpResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthViewModel : ViewModel() {

    var validationError = MutableLiveData<String>()
    var loginResponse = MutableLiveData<LoginResponse>()
    var reSendResponse = MutableLiveData<ReSendOTPResponse>()
    var signUpResponse = MutableLiveData<SignUpResponse>()
    var loginResponseWithOtp = MutableLiveData<LoginResponse>()
    var forgetPasswordResponse = MutableLiveData<ForgetPasswordResponse>()


    fun forgetPassword(context: Context,email:String,pass:String)
    {
        val map=HashMap<String,Any>()
        map["email"] = email
        map["password"] = pass

        val call: Call<ForgetPasswordResponse> =  RestManager.getInstance().forgetPassword(map)
        call.enqueue(object : Callback<ForgetPasswordResponse> {
            override fun onResponse(call: Call<ForgetPasswordResponse>, response: Response<ForgetPasswordResponse>) {
                if (response.isSuccessful) {
                    forgetPasswordResponse.value = response.body()
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
            override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


    fun login(context: Context,userId:String,pass:String)
    {
        val map=HashMap<String,Any>()
        map["user_unique_id"] = userId
        map["password"] = pass
        map["device_token"] = AppPref.getFirebaseToken(context)
        map["device_type"] = "android"

        val call: Call<LoginResponse> =  RestManager.getInstance().login(map)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResponse.value = response.body()
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

    fun reSendOTP(context: Context,phone:String)
    {
        val map=HashMap<String,Any>()
        map["phone"] = phone
        map["request_for"] = "send_otp"

        val call: Call<ReSendOTPResponse> =  RestManager.getInstance().reSendOTP(map)
        call.enqueue(object : Callback<ReSendOTPResponse> {
            override fun onResponse(call: Call<ReSendOTPResponse>, response: Response<ReSendOTPResponse>) {
                if (response.isSuccessful) {
                    reSendResponse.value = response.body()
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
            override fun onFailure(call: Call<ReSendOTPResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun verifyOtp(context: Context,phone:String,otp:String)
    {
        val map=HashMap<String,Any>()
        map["mobile_no"] = phone
        map["request_for"] = "verify_otp"
        map["otp"] = otp
      //  map["firebase_token"] = AppPref.getFirebaseToken(context)

        val call: Call<LoginResponse> =  RestManager.getInstance().loginWithOtp(map)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResponseWithOtp.value = response.body()
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

 /*   fun verifyOtpSignUp(context: Context,phone:String,otp:String)
    {
        val map=HashMap<String,Any>()
        map["phone"] = phone
        map["request_for"] = "verify_otp"
        map["otp"] = otp
        map["firebase_token"] = AppPref.getFirebaseToken(context)

        val call: Call<LoginResponse> =  RestManager.getInstance().signUpVerify(map)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    loginResponseWithOtp.value = response.body()
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
    }*/

    fun signUp(context: Context,map:HashMap<String,Any>)
    {
        val call: Call<SignUpResponse> =  RestManager.getInstance().signUp(map)
        call.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    signUpResponse.value = response.body()
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
            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}
