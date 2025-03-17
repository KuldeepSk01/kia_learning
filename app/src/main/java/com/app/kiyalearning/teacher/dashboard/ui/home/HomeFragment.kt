package com.app.kiyalearning.teacher.dashboard.ui.home

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.ChatListActivity
import com.app.kiyalearning.databinding.DialogDatePickerBinding
import com.app.kiyalearning.databinding.DialogReportLayoutBinding
import com.app.kiyalearning.databinding.TeacherFragmentHomeBinding
import com.app.kiyalearning.student.dashboard.ui.home.HomeFragment
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentClass
import com.app.kiyalearning.teacher.dashboard.DashBoardActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.DashBoardData
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.HomeViewModel
import com.app.kiyalearning.teacher.dashboard.ui.profile.TermsConditionsActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar


class HomeFragment :Fragment() {

    private var _binding: TeacherFragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = TeacherFragmentHomeBinding.inflate(inflater, container, false)
        val activity=activity as DashBoardActivity
        activity.homeFragment=this
        if(activity.binding!=null)
            activity.binding!!.headerTxt.text=  resources.getString(R.string.title_home)


        setClickListeners()
        setUpViewModel()

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getDashBoardData(requireActivity())
            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        binding.classesOfferedCard.setOnClickListener {
            val activity=activity as DashBoardActivity
            activity.classesSelectedTab=0
            activity.binding!!.navView.selectedItemId=R.id.navigation_classes
        }

        binding.cancelClassesCard.setOnClickListener {
            val activity=activity as DashBoardActivity
            activity.classesSelectedTab=5
            activity.binding!!.navView.selectedItemId=R.id.navigation_classes

        }

        binding.classesTakenCard.setOnClickListener {
            val activity=activity as DashBoardActivity
            activity.classesSelectedTab=4
            activity.binding!!.navView.selectedItemId=R.id.navigation_classes

        }


        binding.attendanceCard.setOnClickListener {
            startActivity(Intent(requireContext(), AttendanceActivity::class.java))
        }

        binding.myChatCard.setOnClickListener {
            startActivity(Intent(requireContext(), ChatListActivity::class.java))
        }

        binding.libraryCard.setOnClickListener {
            val activity=activity as DashBoardActivity
            activity.binding!!.navView.selectedItemId=R.id.navigation_library

        }

        binding.aboutUsCard.setOnClickListener {
            val intent=Intent(requireActivity(), TermsConditionsActivity::class.java)
            intent.putExtra("HEADING","About Us")
            startActivity(intent)
        }

        binding.testSeriesCard.setOnClickListener {
            val intent=Intent(requireActivity(), TeacherTestSeriesActivity::class.java)
            startActivity(intent)
        }

        binding.profileCard.setOnClickListener {
            val activity=activity as DashBoardActivity
            activity.binding!!.navView.selectedItemId=R.id.navigation_profile
        }


        binding.myHelpCard.setOnClickListener {
            showReportDialog(it.context).show()
        }


        Glide
            .with(this)
            .load("")
            .centerCrop()
            .placeholder(R.drawable.logo)
            .into(binding.userImage)

        binding.userName.text=""


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        Log.d("MyTag","Token ${AppPref.getUserToken(requireContext())}")

        viewModel.notificationsResponse.observe(viewLifecycleOwner) {
            if (it.status == 200L) {
                val activity=requireActivity() as DashBoardActivity
                if(it.unreadNotification)
                {
                    activity.binding!!.redDot.visibility=View.VISIBLE
                }else
                {
                    activity.binding!!.redDot.visibility=View.GONE
                }
            }
        }

        viewModel.dashBoardDataResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
               setData(it.data)
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(data: DashBoardData) {
        val name=data.firstName+" "+data.lastName
        binding.userName.text=name
        binding.gender.text=AppPref.getUserGender(requireContext())
        binding.address.text=data.location
        binding.userId.text="UserId : "+data.userUniqueId


        Glide
            .with(requireActivity())
            .load(data.profile)
            .centerCrop()
            .placeholder(R.drawable.ic_profile)
            .into(binding.userImage)


        binding.classesOffered.text=data.classesOffered
        binding.classesTaken.text=data.classesTaken
        binding.cancelClasses.text=data.cancelClasses

//        binding.earning.text=data.earning
//        binding.paid.text=data.paid
//        binding.due.text=data.due



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showReportDialog(
        context: Context
    ): Dialog {

        var issue: String = ""
        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogReportLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_report_layout,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            ivCross.setOnClickListener {
                dialog.dismiss()
            }

            tvReportList.setOnClickListener {
                dropDownPopup(it.context, it, R.menu.report_list_menu, object : OnDropDownListener {
                    override fun onDropDownClick(item: String) {
                        issue = item
                        tvReportList.text = item
                    }
                }).show()
            }

            etClassName.setOnClickListener {
                try {
                    showDatePickerDialog(it.context, object :
                        DatePickerDialogListener {
                        override fun onPicker(d: Dialog, str: String) {
                            etClassName.text = str
                        }
                    }).show()
                }catch (e:Exception){
                    Log.d("MyTag","Select Date Error ${e.message}")
                }
            }

            etClassTime.setOnClickListener {
                showTimePickerDialog(it.context,object : DatePickerDialogListener {
                    override fun onPicker(d: Dialog, str: String) {
                        etClassTime.text = str
                    }
                }).show()
            }



            tvSendBtn.setOnClickListener {
                val classDate = etClassName.text.toString()
                val classTime = etClassTime.text.toString()

                val comment = etReportComment.text.toString()

                if (issue.isNullOrEmpty()){
                    UtilClass.mToast(requireContext(),"Please select issue type!")
                    return@setOnClickListener
                }

                if (classDate.isNullOrEmpty()){
                    UtilClass.mToast(requireContext(),"Please select class Date!")
                    return@setOnClickListener
                }

                if (classTime.isNullOrEmpty()){
                    UtilClass.mToast(requireContext(),"Please select class Time!")
                    return@setOnClickListener
                }

                val className = "$classDate $classTime"

                Log.d("MyTag", "Issue : Usertype Teacher Issue $issue and comment $comment className  $className")
                binding.loader.pB.visibility = View.VISIBLE

                viewModel.addTeacherTechnicalReportStatus(requireContext(),"Teacher",className,issue,comment)
                viewModel.addTechnicalReportStatus.observe(requireActivity(), Observer {
                    try {
                        binding.loader.pB.visibility = View.GONE
                        val msg = it.message
                        Log.d("MyTag", "add technical report successfully $msg")
                        UtilClass.mToast(requireContext(),msg)
                        binding.loader.pB.visibility = View.GONE


                    } catch (exp: ActivityNotFoundException) {
                        UtilClass.mToast(requireContext(),"Something went wrong!")
                    }
                })

                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(dB.root)
        dialog.create()
        return dialog
    }

    fun dropDownPopup(
        context: Context, isBelow: View, menuLayout: Int, listener: OnDropDownListener
    ): PopupMenu {
        val popup = PopupMenu(context, isBelow)
        popup.menuInflater.inflate(menuLayout, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            listener.onDropDownClick(item.title.toString())
            true
        }
        return popup
    }

    interface OnDropDownListener {
        fun onDropDownClick(item: String)
    }

    override fun onResume() {
        super.onResume()

        if (MyNetworks.isNetworkAvailable(requireActivity())){
                viewModel.getDashBoardData(requireActivity())
                viewModel.getNotificationsList(requireContext())
                binding.loader.pB.visibility=View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun showTimePickerDialog(
        context: Context,
        listener: DatePickerDialogListener
    ): Dialog {

        var date = ""

        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogDatePickerBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_date_picker,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            datePicker.visibility = View.GONE
            timePicker.visibility = View.VISIBLE
            timePicker.setIs24HourView(true)
            timePicker.setOnTimeChangedListener(object : TimePicker.OnTimeChangedListener {
                override fun onTimeChanged(p0: TimePicker?, p1: Int, p2: Int) {
                    val c = Calendar.getInstance()
                    c.set(Calendar.HOUR_OF_DAY, p1)
                    c.set(Calendar.MINUTE, p2)
                    val sdf = SimpleDateFormat("HH:mm a")
                    date = sdf.format(c.time)
                    listener.onPicker(dialog, date.toString())
                }
            })


            tvOkayBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(dB.root)
        dialog.create()
        dialog.show()
        return dialog
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePickerDialog(
        context: Context,
        listener: DatePickerDialogListener
    ): Dialog {

        var date = ""

        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogDatePickerBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_date_picker,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            //datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.YEAR, year)

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                // dateTextView.text = date.toString()
                date = sdf.format(c.time)
                // date = getDateFormat(dayOfMonth, monthOfYear, year)
                listener.onPicker(dialog, date)
            }

            tvOkayBtn.setOnClickListener {
                dialog.dismiss()
            }


        }

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(dB.root)
        dialog.create()
        dialog.show()
        return dialog
    }


    interface DatePickerDialogListener {
        fun onPicker(d: Dialog, str: String)
    }


}





