package com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.auth.pojos.HomeWorkFileResponse
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.AcceptRejectResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.ClassesListResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.WalletListResponse
import com.app.kiyalearning.util.AppPref
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClassesViewModel : ViewModel() {


    var validationError = MutableLiveData<String>()
    var classesListResponse = MutableLiveData<ClassesListResponse>()
    var walletListResponse = MutableLiveData<WalletListResponse>()
    var acceptRejectCompletedResponse = MutableLiveData<AcceptRejectResponse>()
    var homeWorkFileResponse = MutableLiveData<HomeWorkFileResponse>()

    fun getClassesList(context: Context, id: String, date: String, studentName: String) {
        val map = HashMap<String, Any>()

        map["status"] = id
        map["filter_by_student_name"] = studentName
        map["filter_by_class_date"] = date

        Log.d("Teacher", "map $map")


        val call: Call<ClassesListResponse> = RestManager.getInstance().getClassesList(
            AppPref.getTokenType(context) + " " + AppPref.getUserToken(context), map
        )
        call.enqueue(object : Callback<ClassesListResponse> {
            override fun onResponse(
                call: Call<ClassesListResponse>,
                response: Response<ClassesListResponse>
            ) {
                if (response.isSuccessful) {
                    classesListResponse.value = response.body()
                    Log.d("Teacher", "Teacher classes ${response.body()?.data.toString()}")
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val msg = jObjError.getString("message")
                        if (msg.equals("Unauthenticated.")) {
                            AppPref.userLogout(context)
                            val intent = Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            validationError.value = msg
                        }
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<ClassesListResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


    fun getWalletData(context: Context, id: String) {
        val map = HashMap<String, Any>()
        map["status"] = id

        val call: Call<WalletListResponse> = RestManager.getInstance().getTeacherWalletList(
            AppPref.getTokenType(context) + " " + AppPref.getUserToken(context), map
        )
        call.enqueue(object : Callback<WalletListResponse> {
            override fun onResponse(
                call: Call<WalletListResponse>,
                response: Response<WalletListResponse>
            ) {
                if (response.isSuccessful) {
                    walletListResponse.value = response.body()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value = jObjError.getString("message")
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

    fun acceptRejectCompletedClass(
        context: Context,
        id: String,
        status: String,
        deviceName: String
    ) {
        val map = HashMap<String, Any>()
        map["booked_slot_id"] = id
        map["status"] = status
        map["class_join_with"] = deviceName

        val call: Call<AcceptRejectResponse> =
            RestManager.getInstance().acceptRejectCompletedClasses(
                AppPref.getTokenType(context) + " " + AppPref.getUserToken(context), map
            )
        call.enqueue(object : Callback<AcceptRejectResponse> {
            override fun onResponse(
                call: Call<AcceptRejectResponse>,
                response: Response<AcceptRejectResponse>
            ) {
                if (response.isSuccessful) {
                    acceptRejectCompletedResponse.value = response.body()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value = jObjError.getString("message")
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

    fun uploadHomework(context: Context, classId: String, multipartBody: MultipartBody.Part) {
        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), classId)

        /* val requestBody: RequestBody =
             RequestBody.create(MediaType.parse("multipart/form-data"), file)
         val hWMultiPartBody = MultipartBody.Part.createFormData("home_work_file", file.name, requestBody)
 */
        val token = AppPref.getTokenType(context) + " " + AppPref.getUserToken(context)

        Log.d("Complete", "uploadHomework: file $multipartBody id $classId token $token")
        val call: Call<HomeWorkFileResponse> = RestManager.getInstance().addTeacherClassHW(
            AppPref.getTokenType(context) + " " + AppPref.getUserToken(context), id, multipartBody
        )

        call.enqueue(object : Callback<HomeWorkFileResponse> {
            override fun onResponse(
                call: Call<HomeWorkFileResponse>,
                response: Response<HomeWorkFileResponse>
            ) {
                if (response.body()?.success!!) {
                    homeWorkFileResponse.postValue(response.body())
                } else {
                    Log.d("OnComplete", "onSuccess failure: ")
                    validationError.value = response.body()?.message
//                    try {
//                        val jObjError = JSONObject(response.errorBody()!!.string())
//                        validationError.value =jObjError.getString("message")
//                    } catch (e: Exception) {
//                        validationError.value = e.message
//                        Log.d("OnComplete", "onSuccess failure: ")
//
//                    }
                }
            }

            override fun onFailure(call: Call<HomeWorkFileResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
                Log.d("OnComplete", "onFailure: ${t.message}")
            }

        })

    }

    fun viewHomework() {
        Log.d("TAG", "uploadHomework: view...")

    }


}
