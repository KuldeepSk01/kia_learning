package com.app.kiyalearning.student.dashboard.ui.home.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomStudentTestBinding
import com.app.kiyalearning.student.dashboard.ui.home.TestSeriesActivity
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeries


class StudentTestSeriesAdapter(
    private val myList: List<TestSeries>,
    private val testSeriesActivity: TestSeriesActivity
) : RecyclerView.Adapter<StudentTestSeriesAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomStudentTestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val testSeries = myList[position]

        holder.binding.groupName.text=testSeries.groupName
        holder.binding.testName.text=testSeries.testName
        holder.binding.testType.text=testSeries.testType
        holder.binding.date.text=testSeries.date
        holder.binding.subDate.text=testSeries.submissionDate
        holder.binding.testFile.text=testSeries.teacherFile
        holder.binding.completedFile.text=testSeries.studentFile
        holder.binding.obtainedMarks.text=testSeries.scored
        holder.binding.totalMarks.text=testSeries.marks
        holder.binding.status.text=testSeries.status


        if(testSeries.status.equals("done",true))
        {
            holder.binding.status.setTextColor(holder.binding.testFile.context.getColor(R.color.green_card))
        }

        if(testSeries.studentFile.isBlank())
        {
            holder.binding.completedFile.visibility=View.INVISIBLE
            holder.binding.uploadFile.visibility=View.VISIBLE
        }else
        {
            holder.binding.completedFile.visibility=View.VISIBLE
            holder.binding.uploadFile.visibility=View.INVISIBLE
        }

        holder.binding.uploadFile.setOnClickListener{
            testSeriesActivity.uploadFile(testSeriesId = testSeries.id)
        }

        holder.binding.testFile.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(testSeries.teacherFile))
            holder.binding.testFile.context.startActivity(browserIntent)
        }

        holder.binding.completedFile.setOnClickListener{
            if(testSeries.studentFile.isNotBlank())
            {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(testSeries.studentFile))
                holder.binding.testFile.context.startActivity(browserIntent)
            }
        }


    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomStudentTestBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
