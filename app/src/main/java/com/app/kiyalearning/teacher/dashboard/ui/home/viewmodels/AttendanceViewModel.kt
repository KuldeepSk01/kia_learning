package com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.TeacherAttendanceResponse
import com.app.kiyalearning.util.AppPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AttendanceViewModel : ViewModel() {

    var attendanceResponse = MutableLiveData<TeacherAttendanceResponse>()
    var validationError = MutableLiveData<Int>()

    //  lateinit var selectedImageUrl: String

    fun getTeacherAttendance(context: Context)
    {
        val call: Call<TeacherAttendanceResponse> =  RestManager.getInstance().getTeacherAttendance(
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<TeacherAttendanceResponse> {
            override fun onResponse(call: Call<TeacherAttendanceResponse>, response: Response<TeacherAttendanceResponse>) {
                if (response.body() != null) {
                    attendanceResponse.value = response.body()
                } else{
                    Toast.makeText(context,response.message(),Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<TeacherAttendanceResponse>, t: Throwable) {
                validationError.value = R.string.server_error
            }
        })
    }

}
