package com.app.kiyalearning.student.dashboard.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ActivityAttendanceBinding
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.AttendanceViewModel
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.Attendance
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.applandeo.materialcalendarview.EventDay
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    var binding: ActivityAttendanceBinding?=null
    private val attendanceList =ArrayList<Attendance>()
    private lateinit var viewModel: AttendanceViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)


        setUpViewModel()
        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]
        binding!!.loader.pB.visibility= View.VISIBLE


        viewModel.attendanceResponse.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false

            if (it.status == 200L) {
//                val dateList: HashMap<String, String> = HashMap()
//                for (i in it.attendance.indices)
//                    dateList.putAll(it.attendance[i])
                attendanceList.clear()
                attendanceList.addAll(it.data)


                val events= ArrayList<EventDay>()
                for (item in attendanceList) {

                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    //Parsing the given String to Date object
                    var date: Date?
                    try {
                        date = formatter.parse(item.attendanceDate)
                        val calendar = Calendar.getInstance()
                        if (date != null)
                            calendar.time = date

                        Log.d("MyTag", "date: $date")

                        events.add(EventDay(calendar, R.drawable.circle_green))
//                        when (it.attendance[key]?.isCheckIn) {
//                            "0" ->  events.add(EventDay(calendar, R.drawable.circle_red))
//                            "1" -> events.add(EventDay(calendar, R.drawable.circle_green))
//                            "holiday" -> events.add(EventDay(calendar, R.drawable.circle_reddish_orange))
//                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
                binding!!.calendarView.setEvents(events)

                //update date and month and year also after getting current date from server

                /*      binding.monthAbsentDays.text=it.absent.toString()
                      binding.monthPresentDays.text=it.present.toString()
                      binding.monthWorkingDays.text=it.workingDays.toString()*/

            }else
                Toast.makeText(this, "Error Occurred in Attendance", Toast.LENGTH_SHORT).show()

        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false


            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()

        val map = HashMap<String, Any>()
        map["userid"] = AppPref.getUserId(this)

        //remove after changes
        map["month"] = Calendar.getInstance().get(Calendar.MONTH)+1
        map["year"] =  Calendar.getInstance().get(Calendar.YEAR)

        if(MyNetworks.isNetworkAvailable(this))
        {
            binding!!.loader.pB.visibility=View.VISIBLE
            viewModel.getMonthAttendance(this,map)
        }

    }


    /* private fun dateClick(eventDay: EventDay)
     {

         var dat=""
         if(eventDay.calendar.get(Calendar.DAY_OF_MONTH)<10)
             dat="0"
         dat += eventDay.calendar.get(Calendar.DAY_OF_MONTH)

         var month=""
         val mnth=eventDay.calendar.get(Calendar.MONTH)+1
         if(mnth<10)
             month="0"
         month += mnth.toString()

         val date=dat+"-"+month+"-"+eventDay.calendar.get(Calendar.YEAR)
         val date2=""+eventDay.calendar.get(Calendar.YEAR)+"-"+month+"-"+dat


         val dialog = BottomSheetDialog(requireActivity())
         val bottomSheet = CalendarDateBottomSheetBinding.inflate(LayoutInflater.from(context))
         bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }
         bottomSheet.date.text = date

         //update date and time after getting time and location from api
         if(attendanceList!=null)
         {
             val attendance= attendanceList!![date2]
             bottomSheet.time.text= attendance?.checkInTime
             bottomSheet.location.text=attendance?.checkInAddress
             bottomSheet.checkOutTime.text=attendance?.checkoutTime
             bottomSheet.checkOutLocation.text=attendance?.checkoutAddress
         }else
         {
             bottomSheet.time.text=""
             bottomSheet.location.text=""
             bottomSheet.checkOutTime.text=""
             bottomSheet.checkOutLocation.text=""
         }


         dialog.setContentView(bottomSheet.root)
         dialog.show()


 //        val dialog = BottomSheetDialog(requireActivity().applicationContext, R.style.DialogStyle)
 //        val bottomSheet = CalendarDateBottomSheetBinding.inflate(LayoutInflater.from(context), binding.nestedScrollView, false)
 //        bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }
 //
 ////            Toast.makeText(
 ////                holder.binding.root.context,
 ////                "Api hit for Expert Data",
 ////                Toast.LENGTH_SHORT
 ////            ).show()
 //
 //        val date=""+eventDay.calendar.get(Calendar.DAY_OF_MONTH)+"-"+(eventDay.calendar.get(Calendar.MONTH)+1)+"-"+eventDay.calendar.get(Calendar.YEAR)
 //        bottomSheet.date.text = date
 //
 //        dialog.setContentView(bottomSheet.root)
 //        dialog.show()
     }*/
}
