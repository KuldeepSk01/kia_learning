package com.app.kiyalearning.teacher.dashboard.ui.notifications.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomNotificationBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyNotification


class NotificationsAdapter(private val myList: List<MyNotification>) : RecyclerView.Adapter<NotificationsAdapter.MyHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = CustomNotificationBinding.inflate (
            LayoutInflater.from ( parent.context),parent,false )
        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val notification = myList[position]

        holder.binding.notificationHeading.text=notification.title
        holder.binding.date.text=notification.date


        Glide.with(holder.binding.root.context)
            .load(notification.image)
            .centerCrop()
            .placeholder(R.drawable.logo)
            .into(holder.binding.notificationLogo)

    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    // Holds the views for adding it to image and text
    class MyHolder(customNotificationBinding: CustomNotificationBinding) : RecyclerView.ViewHolder(customNotificationBinding.root) {
        val binding=customNotificationBinding
    }


}
