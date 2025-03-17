package com.app.kiyalearning.student.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.databinding.CustomAssessmentBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.Assessment


class AssessmentAdapter(private val myList: List<Assessment>) : RecyclerView.Adapter<AssessmentAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomAssessmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )


        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val assessment = myList[position]

        holder.binding.testName.text=assessment.testName
        holder.binding.coordinator.text=assessment.coordinatorName
        holder.binding.obtainedMarks.text=assessment.obtainedMarks
        holder.binding.totalMarks.text=assessment.totalMarks
        holder.binding.grade.text=assessment.grade
        holder.binding.subject.text=assessment.subject
        holder.binding.date.text=assessment.assessmentDate

    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomAssessmentBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
