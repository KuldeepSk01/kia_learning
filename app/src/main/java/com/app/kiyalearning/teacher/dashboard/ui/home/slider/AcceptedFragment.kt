package com.app.kiyalearning.teacher.dashboard.ui.home.slider

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.BottomSheetStudentFilterLayoutBinding
import com.app.kiyalearning.databinding.FragmentClassesBinding
import com.app.kiyalearning.student.dashboard.ui.home.slider.DatePickerFragment
import com.app.kiyalearning.student.dashboard.ui.home.slider.MyFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.MyClassesAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyClass
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ClassesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.google.android.material.bottomsheet.BottomSheetDialog


class AcceptedFragment : MyFragment() {
    private lateinit var binding: FragmentClassesBinding
    private val classList = ArrayList<MyClass>()
    private lateinit var classesAdapter: MyClassesAdapter
    private lateinit var viewModel: ClassesViewModel
    var dateTextViewId = ""
    var studentName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater)

        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        classesAdapter = MyClassesAdapter(classList, "accepted", viewModel, binding)
        binding.recycleView.adapter = classesAdapter
        binding.tvIfYouJoinClassText.visibility = View.VISIBLE


        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getClassesList(requireActivity(), "accepted", dateTextViewId, studentName)
            }
        }

        binding.searchGroup.hint = getString(R.string.search_by_student_name)

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
                studentName = s.toString()
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

        return binding.root
    }

    override fun callApis() {
        if (MyNetworks.isNetworkAvailable(requireActivity())) {
            viewModel.getClassesList(requireActivity(), "accepted", dateTextViewId, studentName)
            binding.loader.pB.visibility = View.VISIBLE
        }
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ClassesViewModel::class.java]

        viewModel.classesListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                classList.clear()
                // classList.addAll(it.data)
                classList.addAll(it.data)

                /*created by kuldeep*/
                //
                // classList.sortByDescending { classList->classList.classTimestamp }

                //this is for sorting the list by the date Descending order
                classList.sortByDescending { classList -> classList.rescheduleConferenceDate

                }

                val timeoutClass =
                    classList.filter { UtilClass.isClassTimeExpired(it.classTimestamp) == true }  //if class will not be expired it will  return list and remove from current list
                classList.removeAll(timeoutClass)
                binding.tvClassCount.text = String.format("%s %s",getString(R.string.class_count),"(${classList.size.toString()})")


                Log.d("MyTag", "setUpViewModel: " + it.data.size)

                if (classList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                classesAdapter.notifyDataSetChanged()
            } else
                viewModel.validationError.value = it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.acceptRejectCompletedResponse.observe(viewLifecycleOwner) {
            viewModel.getClassesList(requireActivity(), "accepted", dateTextViewId, studentName)
            binding.loader.pB.visibility = View.VISIBLE
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        callApis()
        val activity = requireActivity() as com.app.kiyalearning.teacher.dashboard.DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener {
            Log.d("MyTag", "classesFilterLay")
            val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
            val bottomSheet = BottomSheetStudentFilterLayoutBinding.inflate(layoutInflater)
            bottomSheet.cancelTextView.setOnClickListener { dialog.dismiss() }
            bottomSheet.byNameFilteredTextView.text = "By Student Name"

            bottomSheet.resetTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
                callApis()
            }

            bottomSheet.byDateFilteredTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.dateChooserTextView.text = "Select Date"
                binding.datePickerBox.visibility = View.VISIBLE
                binding.searchGroup.visibility = View.GONE
                dialog.dismiss()
            }

            bottomSheet.byNameFilteredTextView.setOnClickListener {
                studentName = ""
                dateTextViewId = ""
                binding.searchGroup.setText("")
                binding.datePickerBox.visibility = View.GONE
                binding.searchGroup.visibility = View.VISIBLE
                dialog.dismiss()
            }

            dialog.setContentView(bottomSheet.root)
            dialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        val activity = requireActivity() as com.app.kiyalearning.teacher.dashboard.DashBoardActivity
        activity.binding!!.classesFilterLay.setOnClickListener(null)
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
}