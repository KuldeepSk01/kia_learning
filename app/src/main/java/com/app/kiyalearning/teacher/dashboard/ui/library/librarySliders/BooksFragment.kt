package com.app.kiyalearning.teacher.dashboard.ui.library.librarySliders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.FragmentLibraryBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.LibraryData
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.LibraryAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.LibraryViewModel
import com.app.kiyalearning.util.MyNetworks

class BooksFragment :Fragment(){

    private lateinit var binding: FragmentLibraryBinding
    private val libraryList= ArrayList<LibraryData>()
    private lateinit var libAdapter : LibraryAdapter
    private lateinit var viewModel: LibraryViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentLibraryBinding.inflate ( inflater )

        setUpViewModel()

        binding.recycleView.layoutManager = LinearLayoutManager(requireActivity())
        libAdapter= LibraryAdapter(libraryList)
        binding.recycleView.adapter = libAdapter

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getLibraryList(requireActivity(),"Book")
            }
        }

        return binding.root
    }



    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[LibraryViewModel::class.java]

        viewModel.libraryListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                libraryList.clear()
                libraryList.addAll(it.data)

                if(libraryList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                libAdapter.notifyDataSetChanged()
            }
//            else
//                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            viewModel.getLibraryList(requireActivity(),"Book")
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