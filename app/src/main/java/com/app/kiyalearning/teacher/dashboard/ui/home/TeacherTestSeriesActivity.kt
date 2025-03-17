package com.app.kiyalearning.teacher.dashboard.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ActivityTeaacherTestSeriesBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeries
import com.app.kiyalearning.teacher.dashboard.ui.home.adapters.TeacherTestSeriesAdapter
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.TeacherTestSeriesViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass


class TeacherTestSeriesActivity : AppCompatActivity() {

    var binding: ActivityTeaacherTestSeriesBinding?=null
    private val testList= ArrayList<TestSeries>()
    private lateinit var testSeriesAdapter : TeacherTestSeriesAdapter
    private lateinit var viewModel: TeacherTestSeriesViewModel
    var selectedTab=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTeaacherTestSeriesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()

        binding!!.recycleView.layoutManager = LinearLayoutManager(this)
        testSeriesAdapter= TeacherTestSeriesAdapter(testList)
        binding!!.recycleView.adapter = testSeriesAdapter

        binding!!.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                viewModel.getTestSeries(this)
            }
        }

        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding!!.addButton.setOnClickListener {
            startActivity(Intent(this,AddTestSeriesActivity::class.java))
        }

        binding!!.assignedTextView.setOnClickListener{
            selectedTab=0
            if (MyNetworks.isNetworkAvailable(this)){
                viewModel.getTestSeries(this)
                binding!!.loader.pB.visibility= View.VISIBLE
            }
            binding!!.assignedTextView.setTextColor(getColor(R.color.gray2))
            binding!!.doneTextView.setTextColor(getColor(R.color.black))
        }

        binding!!.doneTextView.setOnClickListener{
            selectedTab=1
            if (MyNetworks.isNetworkAvailable(this)){
                viewModel.getTestSeries(this)
                binding!!.loader.pB.visibility= View.VISIBLE
            }

            binding!!.assignedTextView.setTextColor(getColor(R.color.black))
            binding!!.doneTextView.setTextColor(getColor(R.color.gray2))

        }


    }



    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[TeacherTestSeriesViewModel::class.java]

        viewModel.tests.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            if (it.success) {
                testList.clear()
                testSeriesAdapter.notifyDataSetChanged()

                if(selectedTab==1)
                    for (item in it.data)
                    {
                        if(item.status.equals("done",true))
                        {
                            testList.add(item)
                        }
                    }

                if(selectedTab==0)
                    for (item in it.data)
                    {
                        if(!item.status.equals("done",true))
                        {
                            testList.add(item)
                        }
                    }


                if (testList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                testSeriesAdapter.notifyDataSetChanged()
            }
            else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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


    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(this)){
            viewModel.getTestSeries(this)
            binding!!.loader.pB.visibility= View.VISIBLE
        }
    }

}