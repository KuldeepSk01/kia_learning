package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomTeacherTestBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.TestSeries
import com.app.kiyalearning.teacher.dashboard.ui.home.AddScoreActivity
import com.google.gson.Gson


class TeacherTestSeriesAdapter(
    private val myList: List<TestSeries>
) : RecyclerView.Adapter<TeacherTestSeriesAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomTeacherTestBinding.inflate(
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

            if(testSeries.scored.isBlank())
             holder.binding.editScore.visibility=View.VISIBLE
        }

        holder.binding.editScore.setOnClickListener{
                val gson = Gson()
                val intent = Intent(holder.binding.editScore.context, AddScoreActivity::class.java)
                intent.putExtra("test", gson.toJson(testSeries))
                ContextCompat.startActivity(holder.binding.editScore.context,intent,null)
        }

        if(testSeries.studentFile.isBlank())
        {
            holder.binding.completedFile.text="Not Uploaded Yet"
            holder.binding.completedFile.setTextColor(holder.binding.completedFile.context.getColor(R.color.black))
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
            }else
            {
                Toast.makeText(holder.binding.testFile.context,"Student not uploaded file yet",Toast.LENGTH_SHORT).show()
            }
        }





    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomTeacherTestBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
