package com.app.kiyalearning.student.dashboard.ui.home.slider

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.BottomSheetStudentFilterLayoutBinding
import com.app.kiyalearning.databinding.FragmentCancelledClassesBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.student.dashboard.ui.classes.adapters.MyClassesAdapter
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentClass
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.ClassesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.google.android.material.bottomsheet.BottomSheetDialog

class CancelledFragment :MyFragment(), MyClassesAdapter.OnViewHomeWorkListener {

    private lateinit var binding: FragmentCancelledClassesBinding
    private val classList= ArrayList<StudentClass>()
    private lateinit var classesAdapter : MyClassesAdapter
    private lateinit var viewModel: ClassesViewModel
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            // on below line we are updating the data in our text view.
            onResume()
        }
    }
    var dateTextViewId= ""
    var subjectTextViewId= ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentCancelledClassesBinding.inflate ( inflater )

        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        classesAdapter= MyClassesAdapter(classList,this@CancelledFragment)
        binding.recycleView.adapter = classesAdapter

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getClassesList(requireActivity(),"rejected",dateTextViewId,subjectTextViewId)
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
                subjectTextViewId=s.toString()
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
                if(binding.dateChooserTextView.text!="Select Date")
                    dateTextViewId=binding.dateChooserTextView.text.toString()
            }
        })

        binding.datePickerBox.setOnClickListener{
            val newFragment = DatePickerFragment( binding.dateChooserTextView,binding.root.context,this)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        return binding.root
    }



    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ClassesViewModel::class.java]

        viewModel.classesListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                classList.clear()
                classList.addAll(it.data)

                Log.d("MyTag", "setUpViewModel: "+it.data.size)

                if(classList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                classesAdapter.notifyDataSetChanged()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.acceptRejectCompletedResponse.observe(viewLifecycleOwner) {
            viewModel.getClassesList(requireActivity(),"rejected",dateTextViewId,subjectTextViewId)
            binding.loader.pB.visibility=View.VISIBLE
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        callApis()
        val activity= requireActivity() as DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener{
            val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
            val bottomSheet = BottomSheetStudentFilterLayoutBinding.inflate(layoutInflater)
            bottomSheet.cancelTextView.setOnClickListener { dialog.dismiss() }

            bottomSheet.resetTextView.setOnClickListener{
                subjectTextViewId=""
                dateTextViewId=""
                binding.datePickerBox.visibility=View.GONE
                binding.searchGroup.visibility=View.GONE
                dialog.dismiss()
                callApis()
            }

            bottomSheet.byDateFilteredTextView.setOnClickListener{
                subjectTextViewId=""
                dateTextViewId=""
                binding.dateChooserTextView.text="Select Date"
                binding.datePickerBox.visibility=View.VISIBLE
                binding.searchGroup.visibility=View.GONE
                dialog.dismiss()
            }

            bottomSheet.byNameFilteredTextView.setOnClickListener{
                subjectTextViewId=""
                dateTextViewId=""
                binding.searchGroup.setText("")
                binding.datePickerBox.visibility=View.GONE
                binding.searchGroup.visibility=View.VISIBLE
                dialog.dismiss()
            }

            dialog.setContentView(bottomSheet.root)
            dialog.show()
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter("join_class"))
    }

    override fun onPause() {
        super.onPause()
        val activity= requireActivity() as DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener(null)

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
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

    override fun callApis() {
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            viewModel.getClassesList(requireActivity(),"rejected",dateTextViewId,subjectTextViewId)
            binding.loader.pB.visibility=View.VISIBLE
        }
    }

    override fun onViewFile(mode: StudentClass) {

    }

    override fun onJoin(mode: StudentClass) {

    }


}