package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.databinding.CustomFeesBinding


class FeesAdapter(private val myList: List<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.Fees>) : RecyclerView.Adapter<FeesAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomFeesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val fees = myList[position]

        holder.binding.schoolName.text=fees.school
        holder.binding.country.text=fees.country
        holder.binding.curriculum.text=fees.curriculum
        holder.binding.grade.text=fees.garde
        holder.binding.subject.text=fees.subject
        holder.binding.perHrFees.text=fees.rate


    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomFeesBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
