package com.app.kiyalearning.teacher.dashboard.ui.payout.walletSliders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.FragmentMonthlyBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.WalletAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MonthsName
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyWallet
import com.app.kiyalearning.teacher.dashboard.ui.payout.viewmodel.WalletViewModel
import com.app.kiyalearning.util.MyNetworks

class MonthlyFragment :Fragment(){

    private lateinit var binding: FragmentMonthlyBinding
    private val walletList= ArrayList<MyWallet>()
    private lateinit var classesAdapter : WalletAdapter
    private lateinit var viewModel: WalletViewModel
    private val monthNames= ArrayList<MonthsName>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMonthlyBinding.inflate ( inflater )

        setUpViewModel()
        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        classesAdapter= WalletAdapter(walletList)
        binding.recycleView.adapter = classesAdapter


        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                var monthName=""
                if(monthNames.isNotEmpty())
                    for (item in monthNames)
                    {
                        if(item.isSelected)
                        {
                            monthName=item.name
                            break
                        }
                    }
                viewModel.getWalletData(requireActivity(),"monthly",monthName)
            }
        }

        binding.nextMonth.setOnClickListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                var nextMonthName=""
                for ((index,month) in monthNames.withIndex())
                {
                    if(month.isSelected)
                    {
                        if(index==monthNames.size-1)
                            Toast.makeText(requireContext(),"Data Not Available",Toast.LENGTH_SHORT).show()
                        else
                        {
                            monthNames[index+1].isSelected=true
                            monthNames[index].isSelected=false
                            nextMonthName=monthNames[index+1].name
                        }
                        break
                    }
                }

                if(nextMonthName.isNotBlank())
                if (MyNetworks.isNetworkAvailable(requireActivity())) {
                    fieldsEnabled(false)
                    viewModel.getWalletData(requireActivity(),"monthly",nextMonthName)
                }
            }
        }

        binding.previousMonth.setOnClickListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                var lastMonthName=""
                for ((index,month) in monthNames.withIndex())
                {
                    if(month.isSelected)
                    {
                        if(index==0)
                            Toast.makeText(requireContext(),"Data Not Available",Toast.LENGTH_SHORT).show()
                        else
                        {
                            monthNames[index-1].isSelected=true
                            monthNames[index].isSelected=false
                            lastMonthName=monthNames[index-1].name
                        }
                        break
                    }
                }

                if(lastMonthName.isNotBlank())
                    if (MyNetworks.isNetworkAvailable(requireActivity())) {
                        fieldsEnabled(false)
                        viewModel.getWalletData(requireActivity(),"monthly",lastMonthName)
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


        binding.nextMonth.isEnabled=status
        binding.previousMonth.isEnabled=status
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[WalletViewModel::class.java]

        viewModel.walletListResponse.observe(viewLifecycleOwner) {
            fieldsEnabled(true)
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                walletList.clear()
                walletList.addAll(it.data)
                monthNames.clear()
                monthNames.addAll(it.monthNames)

                for (month in monthNames)
                    if(month.isSelected)
                        binding.monthName.text=month.name

                if(walletList.isEmpty())
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
            fieldsEnabled(true)
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            var monthName=""
            if(monthNames.isNotEmpty())
                for (item in monthNames)
                {
                    if(item.isSelected)
                    {
                        monthName=item.name
                        break
                    }
                }
            viewModel.getWalletData(requireActivity(),"monthly",monthName)
            fieldsEnabled(false)
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