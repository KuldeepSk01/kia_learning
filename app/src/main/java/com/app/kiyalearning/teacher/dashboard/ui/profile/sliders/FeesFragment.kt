package com.app.kiyalearning.teacher.dashboard.ui.profile.sliders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.FragmentProfileRecycleBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.FeesAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.Fees
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.FeesViewModel
import com.app.kiyalearning.util.MyNetworks

class FeesFragment :Fragment(){

    private lateinit var binding: FragmentProfileRecycleBinding
    private val feesList= ArrayList<Fees>()
    private lateinit var feesAdapter : FeesAdapter
    private lateinit var viewModel: FeesViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfileRecycleBinding.inflate ( inflater )

        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        feesAdapter= FeesAdapter(feesList)
        binding.recycleView.adapter = feesAdapter

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getFeesList(requireActivity(),0)
            }
        }

        return binding.root
    }




    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[FeesViewModel::class.java]

        viewModel.feesListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                feesList.clear()
                feesList.addAll(it.data)

                if(feesList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                feesAdapter.notifyDataSetChanged()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            feesAdapter.notifyDataSetChanged()

            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            viewModel.getFeesList(requireActivity(),0)
            binding.loader.pB.visibility=View.VISIBLE
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