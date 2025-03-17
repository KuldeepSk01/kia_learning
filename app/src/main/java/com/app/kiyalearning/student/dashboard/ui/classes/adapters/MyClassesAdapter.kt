package com.app.kiyalearning.student.dashboard.ui.classes.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomStudentClass2Binding
import com.app.kiyalearning.databinding.CustomStudentClassBinding
import com.app.kiyalearning.student.dashboard.ui.home.FeedbackActivity
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentClass
import com.app.kiyalearning.util.UtilClass
import com.bumptech.glide.Glide


class MyClassesAdapter(
    private val myList: List<StudentClass>,
    private val listener: OnViewHomeWorkListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CUSTOM_CLASS: Int = 1

    interface OnViewHomeWorkListener {
        fun onViewFile(mode: StudentClass)
        fun onJoin(mode: StudentClass)

    }
//    interface OnClassJoinListener {
//        fun onJoin(mode: StudentClass)
//    }
//    fun onSetCompleteListener(l: OnViewHomeWorkListener) {
//        listener = l
//    }

//    fun onSetJoinCLassListener(l: OnClassJoinListener) {
//        joinClassListener = l
//    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val myHolder: RecyclerView.ViewHolder = when (viewType) {
            CUSTOM_CLASS -> {
                val binding1 = CustomStudentClassBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyHolder(binding1)
            }

            else -> {
                val binding2 = CustomStudentClass2Binding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyHolder2(binding2)
            }
        }

        return myHolder

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myClass = myList[position]

        when (holder.itemViewType) {
            CUSTOM_CLASS -> {
                //image Layout 1  for status--pending,accepted,reject

                val myHolder1 = holder as MyHolder

                holder.binding.name.text = myClass.name
                holder.binding.subject.text = myClass.subject

                holder.binding.classDate.apply {
                    if (myClass.rescheduleConferenceDate.isNullOrEmpty()) {
                        holder.binding.classDateTextView.text =
                            this.context.getString(R.string.class_date_)
                        text = myClass.date
                    } else {
                        holder.binding.classDateTextView.text =
                            this.context.getString(R.string.reschedule_class_date_)
                        text = myClass.rescheduleConferenceDate
                    }
                }

                holder.binding.classTime.apply {
                    if (myClass.rescheduleConferenceTime.isNullOrEmpty()) {
                        holder.binding.classTimeTextView.text =
                            this.context.getString(R.string.class_time)
                        text = myClass.time
                    } else {
                        holder.binding.classTimeTextView.text =
                            this.context.getString(R.string.reschedule_class_time_)
                        text = myClass.rescheduleConferenceTime
                    }
                }

                /* holder.binding.classDate.text = if (myClass.rescheduleConferenceDate.isNullOrEmpty()) myClass.date else myClass.rescheduleConferenceDate
                 holder.binding.classTime.text = if (myClass.rescheduleConferenceTime.isNullOrEmpty()) myClass.time else myClass.rescheduleConferenceTime
 */
                holder.binding.status.text = myClass.status


                // holder.binding.receiveOn.text = myClass.createdAt
                //holder.binding.reClassDate.text = myClass.rescheduleConferenceDate
                //holder.binding.reClassTime.text = myClass.rescheduleConferenceTime
                holder.binding.joinClassButton.setOnClickListener {
                    listener.onJoin(myClass)
                }
                //holder.binding.tvGeneratingLink.visibility = View.GONE
                holder.binding.userImage.apply {
                    Glide.with(this.context).load(myClass.profile).placeholder(R.drawable.logo)
                        .into(this)
                }
                /* holder.binding.tvTeacherName.text =
                     String.format("%s : %s", "Teacher", myClass.name)
 */
                /*   holder.binding.tvTeacherName.text =
                       String.format("%s\n%s", myClass.name,myClass.subject)
   */

                holder.binding.tvViewHomeWorkTXT.visibility = View.GONE

                if (myClass.status == "completed") {
                    holder.binding.status.setTextColor(holder.binding.root.context.getColor(R.color.green))
                    holder.binding.onLeaveTextView.visibility = View.GONE
                    if (myClass.isFeedBack == 1L) {
                        holder.binding.feedbackImageView.visibility = View.VISIBLE
                        holder.binding.root.setOnClickListener {
                            val intent =
                                Intent(holder.binding.root.context, FeedbackActivity::class.java)
                            intent.putExtra("CLASS_NAME", myClass.name)
                            intent.putExtra("CLASS_SUBJECT", myClass.subject)
                            intent.putExtra("CLASS_DATE", myClass.date)
                            intent.putExtra("CLASS_TIME", myClass.time)
                            intent.putExtra("CLASS_STATUS", myClass.status)
                            intent.putExtra("CLASS_CREATED_DATE", myClass.createdAt)
                            intent.putExtra("CLASS_FEEDBACK", myClass.feedback)
                            intent.putExtra("CLASS_PROFILE", myClass.profile)
                            intent.putExtra("HAS_FEEDBACK", myClass.isFeedBack)
                            intent.putExtra("CLASS_ID", myClass.id)

                            holder.binding.root.context.startActivity(intent)
                        }
                    }

                    holder.binding.tvViewHomeWorkTXT.visibility = View.VISIBLE

                } else if (myClass.status == "rejected") {
                    holder.binding.apply {
                        buttonsLayout.visibility = View.GONE
                        onLeaveTextView.visibility = View.GONE
                        rlRejectedPoint.visibility = View.VISIBLE
                        tvRejectedBy.text = myClass.rejectedBy
                        tvRejectedReason.text = myClass.rejectedReason
                        status.setTextColor(holder.binding.root.context.getColor(R.color.red))
                    }

                } else {
                    if (myClass.hasBreak == 1L) {
                        // holder.binding.tvGeneratingLink.visibility = View.GONE
                        holder.binding.onLeaveTextView.visibility = View.VISIBLE
                        //  holder.binding.tvPendingBtn.visibility = View.GONE
                        holder.binding.tvTeacherNotAvailableTXT.visibility = View.VISIBLE
                    } else {
                        //holder.binding.tvPendingBtn.visibility = View.VISIBLE
                        //  holder.binding.tvGeneratingLink.visibility = View.VISIBLE
                        holder.binding.tvTeacherNotAvailableTXT.visibility = View.GONE
                        holder.binding.onLeaveTextView.visibility = View.GONE

                    }
                }

                /* if (myClass.isReschedule == 0L) {
                    // holder.binding.reClassDate.visibility = View.GONE
                     //holder.binding.reClassDateTextView.visibility = View.GONE
                     //holder.binding.reClassTimeTextView.visibility = View.GONE
                     //holder.binding.reClassTime.visibility = View.GONE
                 }*/

//                if (isClassTimeExpired(myClass.classTimestamp)) {
//                    holder.binding.tvPendingBtn.visibility = View.VISIBLE
//                } else {
//                    holder.binding.tvPendingBtn.visibility = View.GONE
//                }

                /*  Glide.with(holder.binding.root)
                      .load(myClass.profile)
                      .fitCenter()
                      .placeholder(R.drawable.ic_google_meet)
                      .into(holder.binding.userImage)*/

                handlingStudentClassJoinClickBtn(holder.binding, myClass)

                holder.binding.tvViewHomeWorkTXT.setOnClickListener {
                    listener.onViewFile(myClass)
                }
                holder.binding.joinClassButton.setOnClickListener {
                    listener.onJoin(myClass)
                }

                Log.d("Adapter", "Custom Class 1:${myClass.status} ")

            }

            else -> {
                //image Layout 2  for status-- ongoing
                val myHolder = holder as MyHolder2
                holder.binding.name.text = myClass.name
                holder.binding.subject.text = myClass.subject


                holder.binding.classDate.apply {
                    if (myClass.rescheduleConferenceDate.isNullOrEmpty()) {
                        holder.binding.classDateTextView.text =
                            this.context.getString(R.string.class_date_)
                        text = myClass.date
                    } else {
                        holder.binding.classDateTextView.text =
                            this.context.getString(R.string.reschedule_class_date_)
                        text = myClass.rescheduleConferenceDate
                    }
                }


                holder.binding.classTime.apply {
                    if (myClass.rescheduleConferenceTime.isNullOrEmpty()) {
                        holder.binding.classTimeTextView.text =
                            this.context.getString(R.string.class_time)
                        text = myClass.time
                    } else {
                        holder.binding.classTimeTextView.text =
                            this.context.getString(R.string.reschedule_class_time_)
                        text = myClass.rescheduleConferenceTime
                    }
                }

                /*
                    holder.binding.classDate.text = if (myClass.rescheduleConferenceDate.isNullOrEmpty()) myClass.date else myClass.rescheduleConferenceDate
                    holder.binding.classTime.text = if (myClass.resc

                    heduleConferenceTime.isNullOrEmpty()) myClass.time else myClass.rescheduleConferenceTime
                   */

                holder.binding.status.text = myClass.status
                //  holder.binding.receiveOn.text = myClass.createdAt
                //holder.binding.reClassDate.text = myClass.rescheduleConferenceDate
                // holder.binding.reClassTime.text = myClass.rescheduleConferenceTime


                /*  holder.binding.tvTeacherName.text =
                      String.format("%s\n%s", myClass.name,myClass.subject)*/

                holder.binding.userImage.apply {
                    Glide.with(this.context).load(myClass.profile).placeholder(R.drawable.logo)
                        .into(this)
                }
                handlingStudentClass2JoinClickBtn(holder.binding, myClass)
                Log.d("Adapter", "Custom Class 2:${myClass.status} ")


                /*for handling join button*/
                /*     val givenTimeStamp = myClass.classTimestamp //3000000 is calculated 5minutes
                     val getCurrentTimeStamp = UtilClass.getCurrentTimeStamp().plus(300)

                     val isJoin = getCurrentTimeStamp >= givenTimeStamp*/

                if (myClass.status == "completed") {

                    holder.binding.status.setTextColor(holder.binding.root.context.getColor(R.color.green))
                    holder.binding.onLeaveTextView.visibility = View.GONE

                    if (myClass.isFeedBack == 1L) {
                        holder.binding.feedbackImageView.visibility = View.VISIBLE
                        holder.binding.root.setOnClickListener {
                            val intent =
                                Intent(holder.binding.root.context, FeedbackActivity::class.java)
                            intent.putExtra("CLASS_NAME", myClass.name)
                            intent.putExtra("CLASS_SUBJECT", myClass.subject)
                            intent.putExtra("CLASS_DATE", myClass.date)
                            intent.putExtra("CLASS_TIME", myClass.time)
                            intent.putExtra("CLASS_STATUS", myClass.status)
                            intent.putExtra("CLASS_CREATED_DATE", myClass.createdAt)
                            intent.putExtra("CLASS_FEEDBACK", myClass.feedback)
                            intent.putExtra("CLASS_PROFILE", myClass.profile)
                            intent.putExtra("HAS_FEEDBACK", myClass.isFeedBack)
                            intent.putExtra("CLASS_ID", myClass.id)

                            holder.binding.root.context.startActivity(intent)
                        }
                    }
                } else if (myClass.status == "rejected") {
                    holder.binding.apply {
                        rlRejectedPoint.visibility = View.VISIBLE
                        tvRejectedBy.text = myClass.rejectedBy
                        tvRejectedReason.text = myClass.rejectedReason
                        onLeaveTextView.visibility = View.GONE
                        status.setTextColor(holder.binding.root.context.getColor(R.color.red))
                    }

                } else {
                    if (myClass.hasBreak == 1L) {
                        holder.binding.onLeaveTextView.visibility = View.VISIBLE
                        holder.binding.tvTeacherNotAvailableTXT.visibility = View.VISIBLE
                        // holder.binding.tvGeneratingLink.visibility = View.GONE
                    }

                }

                if (myClass.status == "ongoing") {
                    holder.binding.apply {
                        rlJoinWith.visibility = View.VISIBLE
                        tvJoinWith.text = String.format(
                            "%s %s",
                            myClass.teacherClassJoinWith,
                            myClass.joinTimeOfTeacher
                        )
                    }
                } else {
                    holder.binding.rlJoinWith.visibility = View.GONE

                }

                /* if (myClass.isReschedule == 0L) {
                     holder.binding.reClassDate.visibility = View.GONE
                     holder.binding.reClassDateTextView.visibility = View.GONE
                     holder.binding.reClassTimeTextView.visibility = View.GONE
                     holder.binding.reClassTime.visibility = View.GONE
                 }*/

                holder.binding.joinClassButton.setOnClickListener {
                    listener.onJoin(myClass)
                }

                /* Glide.with(holder.binding.root)
                     .load(myClass.profile)
                     .fitCenter()
                     .placeholder(R.drawable.logo)
                     .into(holder.binding.userImage)*/

            }
        }


    }

    private fun handlingStudentClassJoinClickBtn(
        binding: CustomStudentClassBinding,
        myClass: StudentClass
    ) {
        binding.apply {
            // Log.d("Adapter", "Class: ${myClass.hasBreak}")

            if (!myClass.status.equals("completed", ignoreCase = true)) {
                if (isClassJoinLink(myClass.classTimestamp)) {

                    if (isClassTimeExpired(myClass.classTimestamp)) {
                        buttonsLayout.visibility = View.GONE
                        /* buttonsLayout.visibility = View.VISIBLE
                         tvPendingBtn.visibility = View.VISIBLE
                         joinClassButton.visibility = View.GONE
                         tvGeneratingLink.visibility = View.GONE*/

                    } else {

                        buttonsLayout.visibility = View.VISIBLE
                        joinClassButton.visibility = View.VISIBLE
                        /* if (myClass.isTeacherJoinClass == 1) {
                             buttonsLayout.visibility = View.VISIBLE
                             tvGeneratingLink.visibility = View.GONE
                             joinClassButton.visibility = View.VISIBLE
                         } else {
                             buttonsLayout.visibility = View.VISIBLE
                             tvGeneratingLink.visibility = View.VISIBLE
                             joinClassButton.visibility = View.GONE
                         }*/

                    }
                } else {
                    buttonsLayout.visibility = View.GONE
                }

                if (myClass.hasBreak == 1L) {
                    joinClassButton.visibility = View.GONE
                    buttonsLayout.visibility = View.VISIBLE
                    onLeaveTextView.visibility = View.VISIBLE
                    tvTeacherNotAvailableTXT.visibility = View.VISIBLE
                    //  tvPendingBtn.visibility = View.GONE

                } else {
                    // buttonsLayout.visibility = View.GONE
                    onLeaveTextView.visibility = View.GONE
                    tvTeacherNotAvailableTXT.visibility = View.GONE
                }

            } else {
                buttonsLayout.visibility = View.GONE
            }

        }

    }

    private fun handlingStudentClass2JoinClickBtn(
        binding: CustomStudentClass2Binding,
        myClass: StudentClass
    ) {
        binding.apply {
            if (!myClass.status.equals("completed", ignoreCase = true)) {
                if (isClassJoinLink(myClass.classTimestamp)) {

                    if (isClassTimeExpired(myClass.classTimestamp)) {
                        buttonsLayout.visibility = View.GONE
                        /* buttonsLayout.visibility = View.VISIBLE
                         tvPendingBtn.visibility = View.VISIBLE
                         joinClassButton.visibility = View.GONE
                         tvGeneratingLink.visibility = View.GONE*/

                    } else {

                        buttonsLayout.visibility = View.VISIBLE
                        joinClassButton.visibility = View.VISIBLE
                        /* if (myClass.isTeacherJoinClass == 1) {
                             buttonsLayout.visibility = View.VISIBLE
                             tvGeneratingLink.visibility = View.GONE
                             joinClassButton.visibility = View.VISIBLE
                         } else {
                             buttonsLayout.visibility = View.VISIBLE
                             tvGeneratingLink.visibility = View.VISIBLE
                             joinClassButton.visibility = View.GONE
                         }*/

                    }
                } else {
                    buttonsLayout.visibility = View.GONE
                }

                /*    if (isClassTimeExpired(myClass.classTimestamp)) {
                        tvPendingBtn.visibility = View.VISIBLE
                        joinClassButton.visibility = View.GONE
                        tvGeneratingLink.visibility = View.GONE

                    } else {
                        if (myClass.isTeacherJoinClass == 1) {
                            buttonsLayout.visibility = View.VISIBLE
                            tvGeneratingLink.visibility = View.GONE
                            joinClassButton.visibility = View.VISIBLE

                        } else {
                            buttonsLayout.visibility = View.GONE
                            tvGeneratingLink.visibility = View.VISIBLE
                            joinClassButton.visibility = View.GONE
                        }

                        *//*   tvPendingBtn.visibility = View.GONE
                       joinClassButton.visibility = View.VISIBLE*//*
                }*/


                /* if (myClass.isTeacherJoinClass == 1) {
                     buttonsLayout.visibility = View.VISIBLE
                     tvGeneratingLink.visibility = View.GONE
                     if (isClassTimeExpired(myClass.classTimestamp)) {
                         tvPendingBtn.visibility = View.VISIBLE
                         joinClassButton.visibility = View.GONE
                         tvGeneratingLink.visibility = View.GONE

                     } else {
                         tvPendingBtn.visibility = View.GONE
                         joinClassButton.visibility = View.VISIBLE
                     }
                 } else {
                     buttonsLayout.visibility = View.VISIBLE
                     tvGeneratingLink.visibility = View.VISIBLE
                     joinClassButton.visibility = View.GONE
                 }*/
            } else {
                buttonsLayout.visibility = View.GONE

            }

        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    override fun getItemViewType(position: Int): Int {
        val myClass = myList[position]

        return if (myClass.status != "ongoing")
            1
        else
            2
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomStudentClassBinding) :
        RecyclerView.ViewHolder(customClassBinding.root) {
        val binding = customClassBinding
    }

    class MyHolder2(customClassBinding: CustomStudentClass2Binding) :
        RecyclerView.ViewHolder(customClassBinding.root) {
        val binding = customClassBinding
    }

    private fun isClassTimeExpired(classTimeStamp: Long): Boolean {
        val current = System.currentTimeMillis()
        val thirtyMinutes = 1 * 60 * 60 * 1000
        val classTime = (classTimeStamp * 1000).plus(thirtyMinutes)
        return classTime < current
    }

    private fun isClassJoinLink(classTimeStamp: Long): Boolean {
        val givenTimeStamp = classTimeStamp //600 is calculated 10minutes
        val getCurrentTimeStamp = UtilClass.getCurrentTimeStamp().plus(600)
        return getCurrentTimeStamp >= classTimeStamp
    }


}


