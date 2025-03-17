package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomClassAcceptedJoinBinding
import com.app.kiyalearning.databinding.CustomClassBinding
import com.app.kiyalearning.databinding.DialogAcceptedOrRejectedLayoutBinding
import com.app.kiyalearning.databinding.DialogJoinLinkBtnLayoutBinding
import com.app.kiyalearning.databinding.FragmentClassesBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyClass
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ClassesViewModel
import com.app.kiyalearning.util.UtilClass
import java.text.SimpleDateFormat
import java.util.Date


class MyClassesAdapter(
    private val myList: List<MyClass>,
    private val status: String,
    private val viewModel: ClassesViewModel,
    private val binding: FragmentClassesBinding
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: OnHomeWorkListener

    fun onSetCompleteListener(l: OnHomeWorkListener) {
        listener = l
    }

    interface OnHomeWorkListener {
        fun onUploadFile(mode: MyClass)
        fun onViewFile(mode: MyClass)
    }

    private val CUSTOM_CLASS: Int = 1

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val myHolder: RecyclerView.ViewHolder = when (viewType) {
            CUSTOM_CLASS -> {
                val binding = CustomClassBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyHolder1(binding)
            }

            else -> {
                val binding = CustomClassAcceptedJoinBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyHolder2(binding)
            }
        }
        return myHolder
    }

    // binds the list items to a view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myClass = myList[position]

        when (holder.itemViewType) {
            CUSTOM_CLASS -> {
                //image Layout 2
                // Log.d("DeviceName","Name  : ${Build.DEVICE}")
                val myHolder1 = holder as MyHolder1
                holder.binding.name.text = myClass.name
               // holder.binding.classDate.text = myClass.date
                //holder.binding.classTime.text = myClass.time
                holder.binding.contractedFees.text = "₹" + myClass.fees
                holder.binding.status.text = myClass.status
                holder.binding.receiveOn.text = myClass.createdAt
              //  holder.binding.reClassDate.text = myClass.rescheduleConferenceDate
               // holder.binding.reClassTime.text = myClass.rescheduleConferenceTime
                //Log.d("TAG","reschedule class  $myClass")

                holder.binding.classDate.apply {
                    if (myClass.rescheduleConferenceDate.isNullOrEmpty()) {
                        holder.binding.classDateTextView.text = this.context.getString(R.string.class_date_)
                        text= myClass.date
                    } else{
                        holder.binding.classDateTextView.text = this.context.getString(R.string.reschedule_class_date_)
                        text=myClass.rescheduleConferenceDate
                    }
                }


                holder.binding.classTime.apply {
                    if (myClass.rescheduleConferenceTime.isNullOrEmpty()) {
                        holder.binding.classTimeTextView.text = this.context.getString(R.string.class_time)
                        text = myClass.time
                    } else{
                        holder.binding.classTimeTextView.text = this.context.getString(R.string.reschedule_class_time_)
                        text= myClass.rescheduleConferenceTime
                    }
                }

                if (status == "rejected" || status == "completed") {
                    holder.binding.buttonsLayout.visibility = View.GONE
                }
                holder.binding.tvTeacherUploadLayout.visibility = View.GONE

                holder.binding.apply {
                    when (myClass.status) {
                        "completed" -> {
                            onLeaveTextView.visibility = View.GONE
                            status.setTextColor(holder.binding.root.context.getColor(R.color.dark_green))
                            buttonsLayout.visibility = View.GONE
                            tvTeacherUploadLayout.visibility = View.VISIBLE
                        }

                        "rejected" -> {
                            rlRejectedPoint.visibility = View.VISIBLE
                            tvRejectedBy.text = myClass.rejectedBy
                            tvRejectedReason.text = myClass.rejectedReason
                            onLeaveTextView.visibility = View.GONE
                            status.setTextColor(holder.binding.root.context.getColor(R.color.red))
                            holder.binding.buttonsLayout.visibility = View.GONE
                        }

                        "accepted" -> {
                            holder.binding.status.setTextColor(
                                holder.binding.root.context.getColor(
                                    R.color.dark_green
                                )
                            )
                        }

                        "pending" -> {
                            holder.binding.status.visibility = View.GONE
                            holder.binding.status.setTextColor(
                                holder.binding.root.context.getColor(
                                    R.color.dark_blue
                                )
                            )
                        }

                        "ongoing" -> {
                            holder.binding.status.setTextColor(
                                holder.binding.root.context.getColor(
                                    R.color.dark_green
                                )
                            )
                        }
                    }

                    if (myClass.status == "pending" || myClass.status == "accepted") {
                        if (myClass.hasBreak == 1L) {
                            onLeaveTextView.visibility = View.VISIBLE
                            tvTeacherNotAvailableTXT.visibility = View.VISIBLE
                            status.visibility = View.GONE
                            buttonsLayout.visibility = View.GONE
                            tvPendingBtn.visibility = View.GONE
                        } else {
                            // onLeaveTextView.visibility = View.GONE
                            tvTeacherNotAvailableTXT.visibility = View.GONE
                            holder.binding.status.visibility = View.VISIBLE

                            if (isClassTimeExpired(myClass.classTimestamp)) {
                                buttonsLayout.visibility = View.GONE
                                tvPendingBtn.visibility = View.VISIBLE
                                //  myHolder1.binding.acceptClassButton.visibility = View.GONE
                            } else {
                                buttonsLayout.visibility = View.VISIBLE
                                tvPendingBtn.visibility = View.GONE
                            }
                        }
                    }

                }


                /*if (myClass.isReschedule == 0L) {
                    holder.binding.reClassDate.visibility = View.GONE
                    holder.binding.reClassDateTextView.visibility = View.GONE
                    holder.binding.reClassTimeTextView.visibility = View.GONE
                    holder.binding.reClassTime.visibility = View.GONE
                }*/


                holder.binding.acceptClassButton.setOnClickListener {
                    viewModel.acceptRejectCompletedClass(
                        holder.binding.classDate.context, myClass.id, "accepted", "Android"
                    )
                    binding.loader.pB.visibility = View.VISIBLE
                    showAcceptedRejectedSuccessDialog(
                        it.context,
                        it.context.getString(R.string.successfully_accepted),
                        it.context.getString(R.string.accepted_class_content),
                        it.context.getDrawable(R.drawable.success)!!
                    ).show()
                }

                holder.binding.rejectClassButton.setOnClickListener {
                    viewModel.acceptRejectCompletedClass(
                        holder.binding.classDate.context, myClass.id, "rejected", "Android"
                    )
                    binding.loader.pB.visibility = View.VISIBLE
                    showAcceptedRejectedSuccessDialog(
                        it.context,
                        it.context.getString(R.string.successfully_rejected),
                        it.context.getString(R.string.rejected_class_content),
                        it.context.getDrawable(R.drawable.ic_cross)!!

                    ).show()

                }

                holder.binding.tvUploadHWBtn.setOnClickListener {
                    listener.onUploadFile(myClass)
                }
                holder.binding.tvViewHWBtn.setOnClickListener {
                    listener.onViewFile(myClass)
                }

            }

            else -> {
                //image Layout 2
                val myHolder1 = holder as MyHolder2

                //  Log.d("MyTag", "onBindViewHolder: class $myClass")

                holder.binding.name.text = myClass.name
               // holder.binding.classDate.text = myClass.date
               // holder.binding.classTime.text = myClass.time
                holder.binding.contractedFees.text = "₹" + myClass.fees
                holder.binding.status.text = myClass.status
                holder.binding.receiveOn.text = myClass.createdAt
               // holder.binding.reClassDate.text = myClass.rescheduleConferenceDate
                //holder.binding.reClassTime.text = myClass.rescheduleConferenceTime


                holder.binding.classDate.apply {
                    if (myClass.rescheduleConferenceDate.isNullOrEmpty()) {
                        holder.binding.classDateTextView.text = this.context.getString(R.string.class_date_)
                        text= myClass.date
                    } else{
                        holder.binding.classDateTextView.text = this.context.getString(R.string.reschedule_class_date_)
                        text=myClass.rescheduleConferenceDate
                    }
                }



                holder.binding.classTime.apply {
                    if (myClass.rescheduleConferenceTime.isNullOrEmpty()) {
                        holder.binding.classTimeTextView.text = this.context.getString(R.string.class_time)
                        text = myClass.time
                    } else{
                        holder.binding.classTimeTextView.text = this.context.getString(R.string.reschedule_class_time_)
                        text= myClass.rescheduleConferenceTime
                    }
                }




               /* if (myClass.isReschedule == 0L) {
                    holder.binding.reClassDate.visibility = View.GONE
                    holder.binding.reClassDateTextView.visibility = View.GONE
                    holder.binding.reClassTimeTextView.visibility = View.GONE
                    holder.binding.reClassTime.visibility = View.GONE
                }*/

                if (status == "rejected" || status == "completed") {
                    holder.binding.buttonsLayout.visibility = View.GONE
                }

                holder.binding.apply {
                    when (myClass.status) {
                        "completed" -> {
                            status.setTextColor(holder.binding.root.context.getColor(R.color.dark_green))
                            buttonsLayout.visibility = View.GONE
                        }

                        "rejected" -> {
                            rlRejectedPoint.visibility = View.VISIBLE
                            tvRejectedBy.text = myClass.rejectedBy
                            tvRejectedReason.text = myClass.rejectedReason
                            status.setTextColor(holder.binding.root.context.getColor(R.color.red))
                            holder.binding.buttonsLayout.visibility = View.GONE
                        }

                        "accepted" -> {
                            status.setTextColor(
                                holder.binding.root.context.getColor(
                                    R.color.dark_green
                                )
                            )
                        }

                        "ongoing" -> {
                            holder.binding.status.setTextColor(
                                holder.binding.root.context.getColor(
                                    R.color.dark_green
                                )
                            )
                        }
                    }

                    if (myClass.status == "ongoing") {
                        holder.binding.apply {
                            rlJoinWith.visibility = View.VISIBLE
                            tvJoinWith.text = String.format(
                                "%s %s",
                                myClass.studentClassJoinWith,
                                myClass.joinTimeOfStudent
                            )
                        }
                    } else {
                        holder.binding.rlJoinWith.visibility = View.GONE

                    }

                    /*for handling join button*/
                    val givenTimeStamp = myClass.classTimestamp //3000000 is calculated 5minutes
                    val getCurrentTimeStamp = UtilClass.getCurrentTimeStamp().plus(600)
                    val isJoin = getCurrentTimeStamp >= givenTimeStamp
                    if (isJoin) {
                        tvJoinClassLink.visibility = View.GONE
                        joinClassButton.visibility = View.VISIBLE

                        if (myClass.status == "accepted" || myClass.status == "ongoing") {
                            if (isClassTimeExpired(myClass.classTimestamp)) {
                                joinClassButton.visibility = View.GONE
                                tvPendingBtn.visibility = View.VISIBLE
                                //tvJoinClassLink.visibility = View.GONE
                                //tvJoinClassLink.text = "Pending"
                                status.text = "Pending"
                            } else {
                                tvPendingBtn.visibility = View.GONE
                                // tvJoinClassLink.visibility = View.GONE
                                joinClassButton.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        tvJoinClassLink.visibility = View.VISIBLE
                        joinClassButton.visibility = View.GONE
                        tvPendingBtn.visibility = View.GONE
                    }

                }

                holder.binding.joinClassButton.setOnClickListener {
                    showJoinSuccessDialog(it.context, myClass).show()

                    /* val intent = Intent(Intent.ACTION_VIEW)
                     intent.data = Uri.parse(myClass.classUrl)
                     viewModel.acceptRejectCompletedClass(
                         holder.binding.classDate.context, myClass.id, "completed"
                     )
                     binding.loader.pB.visibility = View.VISIBLE

                     Log.d("MyTag", "onBindViewHolder myClass: $myClass")
                     try {
                         holder.binding.receiveOn.context.startActivity(intent)
                     } catch (exp: ActivityNotFoundException) {
                         Toast.makeText(
                             holder.binding.receiveOn.context,
                             "Url may be broken",
                             Toast.LENGTH_SHORT
                         ).show()
                     }*/
                }
            }


        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    override fun getItemViewType(position: Int): Int {
        val myClass = myList[position]

        return if (myClass.status == "pending" || myClass.status == "rejected" || myClass.status == "completed") 1
        else 2
    }

    // Holds the views for adding it to image and text
    class MyHolder1(customClassBinding: CustomClassBinding) :
        RecyclerView.ViewHolder(customClassBinding.root) {
        val binding = customClassBinding

    }

    class MyHolder2(customClassBinding: CustomClassAcceptedJoinBinding) :
        RecyclerView.ViewHolder(customClassBinding.root) {
        val binding = customClassBinding
    }


    fun convertTimeToDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("hh:mm a");
        val dateString = formatter.format(Date(timestamp))
        return dateString
    }

    private fun isClassTimeExpired(classTimeStamp: Long): Boolean {
        val current = System.currentTimeMillis()
        val thirtyMinutes = 1 * 60 * 60 * 1000
        val classTime = (classTimeStamp * 1000).plus(thirtyMinutes)
        return classTime < current
    }

    fun showAcceptedRejectedSuccessDialog(
        context: Context,
        title: String,
        subTitle: String,
        icon: Drawable
    ): Dialog {

        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogAcceptedOrRejectedLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_accepted_or_rejected_layout,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            ivIcon.background = icon
            tvTitle.text = title
            tbSubTitle.text = subTitle
            tvOkayBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(dB.root)
        dialog.create()
        return dialog
    }

    fun showJoinSuccessDialog(
        context: Context,
        myClass: MyClass
    ): Dialog {

        val dialog = Dialog(context)
        val dB = DataBindingUtil.inflate<DialogJoinLinkBtnLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_join_link_btn_layout,
            null,
            false
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dB.apply {
            tvJoinBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(myClass.classUrl)
                viewModel.acceptRejectCompletedClass(
                    context, myClass.id, "completed", "Android"

                )
                binding.loader.pB.visibility = View.VISIBLE

                Log.d("MyTag", "onBindViewHolder myClass: $myClass")
                try {
                    context.startActivity(intent)
                } catch (exp: ActivityNotFoundException) {
                    Toast.makeText(
                        context,

                        "Url may be broken",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                dialog.dismiss()
            }
            icCross.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(dB.root)
        dialog.create()
        return dialog
    }


}
