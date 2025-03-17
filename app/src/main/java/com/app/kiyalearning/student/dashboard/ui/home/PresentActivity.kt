package com.app.kiyalearning.student.dashboard.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.ActivityPresentBinding
import com.app.kiyalearning.student.dashboard.ui.home.adapters.AbsentPresentAdapter
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AbsentPresent
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.AbsentPresentViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass

class PresentActivity : AppCompatActivity() {

    var binding: ActivityPresentBinding?=null
    private val absentList= ArrayList<AbsentPresent>()
    private lateinit var absentPresentAdapter : AbsentPresentAdapter
    private lateinit var viewModel: AbsentPresentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPresentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()


        binding!!.recycleView.layoutManager = LinearLayoutManager(this)
        absentPresentAdapter= AbsentPresentAdapter(absentList)
        binding!!.recycleView.adapter = absentPresentAdapter


        binding!!.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                viewModel.getAbsentPresent(this,0)
            }
        }


        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setFakeDate() {
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
        absentList.add(AbsentPresent("Rahul Kumar","10 AM","10 Mar 2023,Fri","Sec-18 Noida","11 AM","10 Mar 2023,Fri","Sec-18 Noida"))
    }


    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[AbsentPresentViewModel::class.java]

        viewModel.absentPresentListListResponse.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                absentList.clear()
                absentList.addAll(it.absentPresent)

                if (absentList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                absentPresentAdapter.notifyDataSetChanged()
            }
//            }else
//                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            setFakeDate()
            absentPresentAdapter.notifyDataSetChanged()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(this)){
            viewModel.getAbsentPresent(this,0)
            binding!!.loader.pB.visibility= View.VISIBLE
        }
    }

    private fun showNoShiftDesign(status:Boolean)
    {
        if(status)
        {
            binding!!.noShiftImage.visibility= View.VISIBLE
            binding!!.noShiftTextView.visibility= View.VISIBLE
        }else
        {
            binding!!.noShiftImage.visibility= View.GONE
            binding!!.noShiftTextView.visibility= View.GONE
        }

    }
}