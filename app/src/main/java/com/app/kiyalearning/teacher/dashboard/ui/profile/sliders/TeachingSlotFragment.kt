package com.app.kiyalearning.teacher.dashboard.ui.profile.sliders

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.student.dashboard.ui.home.adapters.TeacherSlotAdapter
import com.app.kiyalearning.databinding.FragmentTeacherSlotBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ProfileViewModel
import java.util.*

class TeachingSlotFragment :Fragment(){

    private lateinit var binding: FragmentTeacherSlotBinding
    private val slotsList= ArrayList<String>()
    private lateinit var teachersAdapter : TeacherSlotAdapter
    private lateinit var viewModel: ProfileViewModel
    private var myCalendar=Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTeacherSlotBinding.inflate ( inflater )


        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        teachersAdapter= TeacherSlotAdapter(slotsList)
        binding.recycleView.adapter = teachersAdapter

        binding.dateChooserTextView.text = "Select Date"


        binding.datePickerBox.setOnClickListener {
            val newFragment = DatePickerFragment(requireContext(),myCalendar,binding.dateChooserTextView,viewModel)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.pullToRefresh.setOnRefreshListener {
           binding.pullToRefresh.isRefreshing=false
        }

        return binding.root
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.teacherSlotsResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                slotsList.clear()
                slotsList.addAll(it.data)

                if(slotsList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                teachersAdapter.notifyDataSetChanged()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
           binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
         //   classesAdapter.notifyDataSetChanged()
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }


    private fun showNoShiftDesign(status:Boolean)
    {
        if(status)
        {
            binding.noShiftImage.visibility=View.VISIBLE
            binding.noShiftTextView.visibility=View.VISIBLE
        }else
        {
            binding.noShiftImage.visibility=View.GONE
            binding.noShiftTextView.visibility=View.GONE
        }
    }

    override fun onResume() {
        super.onResume()



        var months=(myCalendar.get(Calendar.MONTH)+1).toString()
        var days=myCalendar.get(Calendar.DAY_OF_MONTH).toString()

        if((myCalendar.get(Calendar.MONTH)+1)<10)
            months="0"+(myCalendar.get(Calendar.MONTH)+1).toString()
        if(myCalendar.get(Calendar.DAY_OF_MONTH)<10)
            days= "0"+myCalendar.get(Calendar.DAY_OF_MONTH)


        val date = StringBuilder()
        date.append(myCalendar.get(Calendar.YEAR)).append("-").append(months).append("-").append(days)
        binding.dateChooserTextView.text = date.toString()
        viewModel.getSlots(requireContext(),myCalendar.get(Calendar.YEAR),months,days)
    }

}


class DatePickerFragment(private val context1:Context
,private val myCalendar: Calendar
,private val dateChooserTextView: TextView
,private val viewModel: ProfileViewModel
) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(context1, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user


//        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute = calendar.get(Calendar.MINUTE)
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)

//        var wrongDate =false
//
//        val calendar=Calendar.getInstance()
//
//        if(year<calendar.get(Calendar.YEAR))
//            wrongDate=true
//        else if(year==calendar.get(Calendar.YEAR) &&  month<calendar.get(Calendar.MONTH))
//            wrongDate=true
//        else if(year==calendar.get(Calendar.YEAR) &&  month==calendar.get(Calendar.MONTH) && day<calendar.get(
//                Calendar.DAY_OF_MONTH))
//            wrongDate=true
//
//        if(wrongDate)
//            Toast.makeText(context1,"Cannot Select Past Date",Toast.LENGTH_LONG).show()
//        else
//        {
//
//
//        }

        var months=(month+1).toString()
        var days=day.toString()

        if((month+1)<10)
            months="0"+(month+1).toString()
        if(day<10)
            days= "0$day"

        myCalendar.set(year,month,day)


        val date = StringBuilder()
        date.append(year).append("-").append(months).append("-").append(days)
        dateChooserTextView.text = date.toString()
        viewModel.getSlots(context = context1,year,months,days)


    }
}