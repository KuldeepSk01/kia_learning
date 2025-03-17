package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.databinding.CustomTeacherSlotsBinding


class TeacherSlotAdapter(private val myList: List<String>) : RecyclerView.Adapter<TeacherSlotAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomTeacherSlotsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.binding.content.text=myList[position]
    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomTeacherSlotsBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
