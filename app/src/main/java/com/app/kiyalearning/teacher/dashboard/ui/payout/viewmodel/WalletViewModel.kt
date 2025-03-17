package com.app.kiyalearning.teacher.dashboard.ui.payout.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.WalletListResponse
import com.app.kiyalearning.util.AppPref
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WalletViewModel : ViewModel() {


    var validationError = MutableLiveData<String>()
    var walletListResponse=MutableLiveData<WalletListResponse>()

    fun getWalletData(context: Context,id:String,monthName:String)
    {
        val map=HashMap<String,Any>()
        map["status"] = id
        map["month"] = monthName

        val call: Call<WalletListResponse> =  RestManager.getInstance().getTeacherWalletList(
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

    fun getWalletData(context: Context,id:String,from:String,to:String)
    {
        val map=HashMap<String,Any>()
        map["status"] = id
        map["from"] = from
        map["to"] = to
    //    map["from"] = "2023-4-12"
   //     map["to"] = "2023-4-30"

        Log.d("MyTag", "from: "+from)
        Log.d("MyTag", "to: "+to)
        Log.d("MyTag", "map: "+map)

        val call: Call<WalletListResponse> =  RestManager.getInstance().getTeacherWalletList(
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

}
