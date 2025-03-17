package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AbsentPresent
import com.app.kiyalearning.databinding.CustomAbsentPresentBinding


class AbsentPresentAdapter(private val myList: List<AbsentPresent>) : RecyclerView.Adapter<AbsentPresentAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomAbsentPresentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )


        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val absentPresentData = myList[position]

        holder.binding.name.text=absentPresentData.name
        holder.binding.checkIn.text=absentPresentData.checkIn
        holder.binding.checkOut.text=absentPresentData.checkOut
        holder.binding.checkInDate.text=absentPresentData.checkInDate
        holder.binding.checkInAddress.text=absentPresentData.checkInAddress
        holder.binding.checkOutDate.text=absentPresentData.checkOutDate
        holder.binding.checkOutAddress.text=absentPresentData.checkOutAddress


    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomAbsentPresentBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
