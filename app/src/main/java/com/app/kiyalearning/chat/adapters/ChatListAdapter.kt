package com.app.kiyalearning.chat.adapters


import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.ChatActivity
import com.app.kiyalearning.chat.pojo.Group
import com.app.kiyalearning.coordinator.dashboard.ui.groupchat.GroupChatFragment
import com.app.kiyalearning.databinding.CustomContactChatsBinding
import com.app.kiyalearning.databinding.ProfileImageViewBinding
import com.app.kiyalearning.util.AppPref
import com.bumptech.glide.Glide


class ChatListAdapter(private val myList: List<Group>, private val listener: OnGroupPinListener) : RecyclerView.Adapter<ChatListAdapter.MyHolder>() {

    interface OnGroupPinListener{
        fun onPinGroup(group: Group)

    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = CustomContactChatsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyHolder(binding)
    }

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private var mScaleFactor = 1.0f

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val chat = myList[position]
        holder.binding.profileName.text = chat.name
        holder.binding.ivPin.visibility = View.GONE



        if (AppPref.getUserType(holder.binding.profileName.context).contains("student")) {
            holder.binding.unreadMessages.text = chat.studentUnread
            holder.binding.ivPin.visibility = View.VISIBLE

            if (chat.sortByStudent==1){
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_pin)
            }else{
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_unpin)
            }
            if (chat.studentUnread == "0") {
                holder.binding.unreadMessages.visibility = View.GONE
                holder.binding.unreadBackground.visibility = View.GONE
            } else {
                holder.binding.unreadMessages.visibility = View.VISIBLE
                holder.binding.unreadBackground.visibility = View.VISIBLE
            }
        } else if (AppPref.getUserType(holder.binding.profileName.context).contains("teacher")) {
            holder.binding.unreadMessages.text = chat.teacherUnread
            holder.binding.ivPin.visibility = View.VISIBLE

            if (chat.sortByTeacher==1){
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_pin)
            }else{
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_unpin)
            }
            if (chat.teacherUnread == "0") {
                holder.binding.unreadMessages.visibility = View.GONE
                holder.binding.unreadBackground.visibility = View.GONE
            } else {
                holder.binding.unreadMessages.visibility = View.VISIBLE
                holder.binding.unreadBackground.visibility = View.VISIBLE
            }

        } else if (AppPref.getUserType(holder.binding.profileName.context)
                .contains("coordinator")
        ) {
            holder.binding.unreadMessages.text = chat.coordinatorUnread
            holder.binding.ivPin.visibility = View.VISIBLE

            if (chat.sortByCoordinator==1){
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_pin)
            }else{
                holder.binding.ivPin.setBackgroundResource(R.drawable.ic_unpin)
            }
            if (chat.coordinatorUnread == "0") {
                holder.binding.unreadMessages.visibility = View.GONE
                holder.binding.unreadBackground.visibility = View.GONE
            } else {
                holder.binding.unreadMessages.visibility = View.VISIBLE
                holder.binding.unreadBackground.visibility = View.VISIBLE
            }
        } else {
            holder.binding.unreadMessages.visibility = View.GONE
            holder.binding.unreadBackground.visibility = View.GONE
        }


        holder.binding.ivPin.setOnClickListener {
            listener.onPinGroup(chat)
        }


        //  holder.binding.messageTimeTextView.text=chat.time
        //  holder.binding.type.text=chat.

        Glide.with(holder.binding.root)
            .load(chat.groupIcon)
            .placeholder(R.drawable.logo)
            .fitCenter()
            .into(holder.binding.profileImage)


        holder.binding.root.setOnClickListener {
            val intent = Intent(holder.binding.root.context, ChatActivity::class.java)
            intent.putExtra("GROUP_NAME", chat.name)
            intent.putExtra("GROUP_IMAGE", chat.groupIcon)
            intent.putExtra("GROUP_ID", chat.id.toString())

            intent.putExtra("SORT_BY_TEACHER", chat.sortByTeacher)
            intent.putExtra("SORT_BY_STUDENT", chat.sortByStudent)
            intent.putExtra("SORT_BY_COORDINATOR", chat.sortByCoordinator)
//            intent.putExtra("CHAT_USER_ID",chat.id.toString())
//            intent.putExtra("OPPOSITE_SIDE_USER",chat.notificationToken)
//            intent.putExtra("IS_USER_CHAT",true)
            holder.binding.root.context.startActivity(intent)
        }


        holder.binding.profileImage.setOnClickListener {
            val viewBinding =
                ProfileImageViewBinding.inflate(LayoutInflater.from(holder.binding.root.context))

            Glide.with(holder.binding.root)
                .load(chat.groupIcon)
                .placeholder(R.drawable.logo)
                .fitCenter()
                .into(viewBinding.profileImage)


            viewBinding.tvDownloadImg.visibility = View.GONE
            viewBinding.tvCancelImg.visibility = View.GONE

           /* val dialog = Dialog(holder.binding.root.context,android.R.style.Theme_Material)
            dialog.setContentView(viewBinding.root)
            dialog.create()
            dialog.show()*/

            AlertDialog.Builder(holder.binding.root.context)
                .setView(viewBinding.root)
                // The dialog is automatically dismissed when a dialog button is clicked.
                .show()
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    // Holds the views for adding it to image and text
    class MyHolder(customContactChatsBinding: CustomContactChatsBinding) :
        RecyclerView.ViewHolder(customContactChatsBinding.root) {
        val binding = customContactChatsBinding
    }


}