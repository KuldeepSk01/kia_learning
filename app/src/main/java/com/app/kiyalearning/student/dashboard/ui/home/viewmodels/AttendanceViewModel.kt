package com.app.kiyalearning.student.dashboard.ui.home.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentAttendanceResponse
import com.app.kiyalearning.util.AppPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AttendanceViewModel : ViewModel() {

    var attendanceResponse = MutableLiveData<StudentAttendanceResponse>()
    var validationError = MutableLiveData<Int>()

    //  lateinit var selectedImageUrl: String

    fun getMonthAttendance(context: Context,map: HashMap<String, Any>)
    {
        val call: Call<StudentAttendanceResponse> =  RestManager.getInstance().getStudentAttendance(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<StudentAttendanceResponse> {
            override fun onResponse(call: Call<StudentAttendanceResponse>, response: Response<StudentAttendanceResponse>) {
                if (response.body() != null) {
                    attendanceResponse.value = response.body()
                } else{
                    Toast.makeText(context,response.message(),Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<StudentAttendanceResponse>, t: Throwable) {
                validationError.value = R.string.server_error
            }
        })
    }
}
