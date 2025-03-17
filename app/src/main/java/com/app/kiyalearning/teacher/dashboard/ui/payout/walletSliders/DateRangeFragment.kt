package com.app.kiyalearning.teacher.dashboard.ui.payout.walletSliders

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.app.kiyalearning.databinding.FragmentDateRangeBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.WalletAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyWallet
import com.app.kiyalearning.teacher.dashboard.ui.payout.viewmodel.WalletViewModel
import com.app.kiyalearning.util.MyNetworks
import java.util.*

class DateRangeFragment :Fragment(){

    private lateinit var binding: FragmentDateRangeBinding
    private val walletList= ArrayList<MyWallet>()
    private lateinit var classesAdapter : WalletAdapter
    private lateinit var viewModel: WalletViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentDateRangeBinding.inflate ( inflater )

        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        classesAdapter= WalletAdapter(walletList)
        binding.recycleView.adapter = classesAdapter

        binding.pullToRefresh.setOnRefreshListener {
           binding.pullToRefresh.isRefreshing=false
        }

        binding.from.setOnClickListener {
            val newFragment = DatePickerFragment( binding.from,binding.root.context)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.to.setOnClickListener {
            val newFragment = DatePickerFragment( binding.to,binding.root.context)
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.submitButton.setOnClickListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                fieldsEnabled(false)

                val from =binding.from.text.toString()
                val to =binding.to.text.toString()

                if(from.isBlank() || to.isBlank())
                    Toast.makeText(requireContext(),"Please select date first",Toast.LENGTH_SHORT).show()
                else
                {
                    fieldsEnabled(false)
                    viewModel.getWalletData(requireActivity(),"date-range",from,to)
                }
            }
        }

        return binding.root
    }


    private fun fieldsEnabled(status:Boolean)
    {
        if(status)
        {
            binding.loader.pB.visibility= View.GONE
        }else
        {
            binding.loader.pB.visibility= View.VISIBLE
        }


        binding.submitButton.isEnabled=status
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[WalletViewModel::class.java]

        viewModel.walletListResponse.observe(viewLifecycleOwner) {
           fieldsEnabled(true)
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                walletList.clear()
                walletList.addAll(it.data)


                Log.d("MyTag", "setUpViewModel: "+it.data)

                if(walletList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)


                classesAdapter.notifyDataSetChanged()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            fieldsEnabled(true)
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            val from =binding.from.text.toString()
            val to =binding.from.text.toString()

            if(from.isNotBlank() || to.isNotBlank())
            {
                fieldsEnabled(false)
                viewModel.getWalletData(requireActivity(),"date-range",from,to)
            }
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

}


class DatePickerFragment(private val dateTextView : TextView, private val context2 : Context) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(context2, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val date = StringBuilder()
        date.append(year).append("-").append(month+1).append("-").append(day)
        dateTextView.text = date.toString()
    }

}