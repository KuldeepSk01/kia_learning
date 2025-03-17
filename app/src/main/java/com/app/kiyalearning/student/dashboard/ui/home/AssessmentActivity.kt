package com.app.kiyalearning.student.dashboard.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.ActivityAssessmentBinding
import com.app.kiyalearning.student.dashboard.ui.home.adapters.AssessmentAdapter
import com.app.kiyalearning.student.dashboard.ui.home.pojos.Assessment
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.AssessmentViewModel
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass



class AssessmentActivity : AppCompatActivity() {

    var binding: ActivityAssessmentBinding?=null
    private val assessmentList= ArrayList<Assessment>()
    private lateinit var assessmentAdapter : AssessmentAdapter
    private lateinit var viewModel: AssessmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAssessmentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()


        binding!!.recycleView.layoutManager = LinearLayoutManager(this)
        assessmentAdapter= AssessmentAdapter(assessmentList)
        binding!!.recycleView.adapter = assessmentAdapter


        binding!!.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                viewModel.getAssessments(this)
            }
        }


        binding!!.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }



    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[AssessmentViewModel::class.java]

        viewModel.assessments.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            if (it.status==200L) {
                assessmentList.clear()
                assessmentList.addAll(it.data)

                if (assessmentList.isEmpty())
                    showNoShiftDesign(true)
                else
                    showNoShiftDesign(false)

                assessmentAdapter.notifyDataSetChanged()
            }
//            }else
//                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(this) {
            binding!!.loader.pB.visibility= View.GONE
            binding!!.pullToRefresh.isRefreshing = false
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(this)){
            viewModel.getAssessments(this)
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