package com.app.kiyalearning.student.dashboard.ui.home.slider

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.BottomSheetStudentFilterLayoutBinding
import com.app.kiyalearning.databinding.DialogJoinLinkBtnLayoutBinding
import com.app.kiyalearning.databinding.FragmentClassesBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.student.dashboard.ui.classes.adapters.MyClassesAdapter
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentClass
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.ClassesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.OpenDefaultApp.getUpcomingDate
import com.app.kiyalearning.util.UtilClass
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar

class NewFragment : MyFragment(), MyClassesAdapter.OnViewHomeWorkListener {

    private lateinit var binding: FragmentClassesBinding
    private val classList = ArrayList<StudentClass>()
    private val todayClasses = ArrayList<StudentClass>()
    private val upcomingClasses = ArrayList<StudentClass>()

    private lateinit var classesAdapter: MyClassesAdapter
    private lateinit var todayClassesAdapter: MyClassesAdapter
    private lateinit var upcomingClassesAdapter: MyClassesAdapter

    private lateinit var viewModel: ClassesViewModel
    private var handler = Handler(Looper.getMainLooper())
    private val autoRefreshApiCounterTime = 300000L

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            Log.d("MyTag", "onReceive broadcastReceiver message " + message)
            // on below line we are updating the data in our text view.
            onResume()
        }
    }
    var dateTextViewId = ""
    var subjectTextViewId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater)
        binding.tvClassCount.visibility = View.GONE

        setUpViewModel()
        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvTodayClass.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvUpcomingClass.layoutManager = LinearLayoutManager(requireActivity())

        todayClassesAdapter = MyClassesAdapter(todayClasses, this@NewFragment)
        classesAdapter = MyClassesAdapter(classList, this@NewFragment)
        upcomingClassesAdapter = MyClassesAdapter(upcomingClasses, this@NewFragment)
        //classesAdapter.onSetJoinCLassListener(this@NewFragment)

        binding.recycleView.adapter = classesAdapter
        binding.rvTodayClass.adapter = todayClassesAdapter
        binding.rvUpcomingClass.adapter = upcomingClassesAdapter



        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getClassesList(
                    requireActivity(),
                    "pending",
                    dateTextViewId,
                    subjectTextViewId
                )
            }
        }

        binding.searchGroup.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                subjectTextViewId = s.toString()
                callApis()
            }
        })
        binding.dateChooserTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (binding.dateChooserTextView.text != "Select Date")
                    dateTextViewId = binding.dateChooserTextView.text.toString()
            }
        })

        binding.datePickerBox.setOnClickListener {
            val newFragment =
                DatePickerFragment(binding.dateChooserTextView, binding.root.context, this)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        return binding.root
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ClassesViewModel::class.java]

        viewModel.classesListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                classList.clear()
                todayClasses.clear()
                upcomingClasses.clear()

                classList.addAll(it.data)
                Log.d("MyTag", "Class ${classList.toString()}")
                showNoShiftDesign(false)

                val todayList =
                    it.data.filter { stClass -> (if (stClass.rescheduleConferenceDate.isNullOrEmpty()) stClass.date else stClass.rescheduleConferenceDate) == UtilClass.getCurrentDateFormat() }

                todayClasses.addAll(todayList)
                classList.removeAll(todayClasses)


                classList.forEach {
                    val date =
                        if (it.rescheduleConferenceDate.isNullOrEmpty()) it.date else it.rescheduleConferenceDate


                    if (getUpcomingDate(date)) {
                        upcomingClasses.add(it)
                        Log.d("UpComingDate", "Upcoming classes : $it")
                    }

                }
                classList.removeAll(upcomingClasses)



                if (todayClasses.isEmpty()) {
                    binding.rvTodayClass.visibility = View.GONE
                    binding.tvNoTodayClass.visibility = View.VISIBLE
                } else {
                    binding.rvTodayClass.visibility = View.VISIBLE
                    binding.tvNoTodayClass.visibility = View.GONE
                }


                if (upcomingClasses.isEmpty()) {
                    binding.rlUpcomingClasses.visibility = View.GONE
                    binding.rvUpcomingClass.visibility = View.GONE
                    binding.tvNoUpcomingClass.visibility = View.VISIBLE
                } else {
                    binding.rlUpcomingClasses.visibility = View.VISIBLE
                    binding.rvUpcomingClass.visibility = View.VISIBLE
                    binding.tvNoUpcomingClass.visibility = View.GONE
                }


                binding.tvTodayClassTXT.text = String.format(
                    "%s %s",
                    getString(R.string.today_class),
                    "( ${todayClasses.size} )"
                )
                binding.tvPreviousClassTXT.text = String.format(
                    "%s %s",
                    getString(R.string.previous_class),
                    "( ${classList.size} )"
                )

                binding.tvUpcomingClassTXT.text = String.format(
                    "%s %s",
                    getString(R.string.upcoming_classes),
                    "( ${upcomingClasses.size} )"
                )
//                if (classList.isEmpty()) {
//                    showNoShiftDesign(false)
//                } else {
//                    showNoShiftDesign(true)
//                }

                upcomingClassesAdapter.notifyDataSetChanged()
                classesAdapter.notifyDataSetChanged()
                todayClassesAdapter.notifyDataSetChanged()
            } else
                viewModel.validationError.value = it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.acceptRejectCompletedResponse.observe(viewLifecycleOwner) {
            viewModel.getClassesList(
                requireActivity(),
                "pending",
                dateTextViewId,
                subjectTextViewId
            )
            binding.loader.pB.visibility = View.VISIBLE
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()

        callApis()
        val activity = requireActivity() as DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
            val bottomSheet = BottomSheetStudentFilterLayoutBinding.inflate(layoutInflater)
            bottomSheet.cancelTextView.setOnClickListener { dialog.dismiss() }

            bottomSheet.resetTextView.setOnClickListener {
                subjectTextViewId = ""
                dateTextViewId = ""
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
                callApis()
            }

            bottomSheet.byDateFilteredTextView.setOnClickListener {
                subjectTextViewId = ""
                dateTextViewId = ""
                binding.dateChooserTextView.text = "Select Date"
                binding.datePickerBox.visibility = View.VISIBLE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
            }

            bottomSheet.byNameFilteredTextView.setOnClickListener {
                subjectTextViewId = ""
                dateTextViewId = ""
                binding.searchGroup.setText("")
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.VISIBLE
                dialog.dismiss()
            }

            dialog.setContentView(bottomSheet.root)
            dialog.show()
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("join_class"))
        binding.rlTodayClasses.visibility = View.VISIBLE
        binding.tvPreviousClassTXT.visibility = View.VISIBLE
        autoRefreshApi()

    }

    override fun onPause() {
        super.onPause()
        val activity = requireActivity() as DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener(null)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        handler.removeCallbacks(autoRefreshRunnable)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoRefreshRunnable)
    }

    private fun showNoShiftDesign(status: Boolean) {
        if (status) {
            binding.noShiftImage.visibility = View.VISIBLE
            binding.noShiftTextView.visibility = View.VISIBLE
        } else {
            binding.noShiftImage.visibility = View.GONE
            binding.noShiftTextView.visibility = View.GONE
        }

    }

    override fun callApis() {
        if (MyNetworks.isNetworkAvailable(requireActivity())) {
            viewModel.getClassesList(
                requireActivity(),
                "pending",
                dateTextViewId,
                subjectTextViewId
            )
            binding.loader.pB.visibility = View.VISIBLE
        }
    }

    /*created by Kuldeep singh*/
    private fun autoRefreshApi() {
        handler.postDelayed(autoRefreshRunnable, autoRefreshApiCounterTime)
    }

    private val autoRefreshRunnable = Runnable {
        onResume()
    }

    override fun onViewFile(mode: StudentClass) {

    }

    fun showJoinSuccessDialog(
        context: Context,
        myClass: StudentClass
    ): Dialog {
        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogJoinLinkBtnLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_join_link_btn_layout,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            tvJoinBtn.setOnClickListener {
                dialog.dismiss()
                binding.loader.pB.visibility = View.VISIBLE

                viewModel.studentJoinClassStatus(requireContext(), myClass.id.toInt(), "Android")
                viewModel.studentJoinClassResponse.observe(requireActivity(), Observer {
                    try {
                        val msg = it.message
                        Log.d("MyTag", "onJoinClass $msg")
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(myClass.classUrl)
                        startActivity(intent)
                        binding.loader.pB.visibility = View.GONE


                        //    Log.d("MyTag", "onBindViewHolder myClass: $myClass")
                    } catch (exp: ActivityNotFoundException) {
                        binding.loader.pB.visibility = View.GONE

                        Toast.makeText(
                            requireContext(),
                            "Url may be broken",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            icCross.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(dB.root)
        dialog.create()
        return dialog
    }

    override fun onJoin(mode: StudentClass) {
        Log.d("MyTag", "onJoinClass")
        showJoinSuccessDialog(requireContext(), mode).show()
    }


}

abstract class MyFragment : Fragment() {
    abstract fun callApis()
}

class DatePickerFragment(
    private val dateTextView: TextView,
    private val context2: Context,
    private val frag: MyFragment
) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context2, this, year, month, day)
        //   datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 10000
        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog
    }

    override fun onDateSet(
        view: android.widget.DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.YEAR, year)

        //val date = StringBuilder()
        // date.append(year).append("-").append(month + 1).append("-").append(dayOfMonth)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        // dateTextView.text = date.toString()
        dateTextView.text = sdf.format(c.time)
        frag.callApis()
    }

}



