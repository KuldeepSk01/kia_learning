package com.app.kiyalearning.student.dashboard.ui.classes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ItemStudentSiblingsBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.StudentSibilingResponse
import com.bumptech.glide.Glide

class AdapterStudentSibling(val list: MutableList<StudentSibilingResponse>,val listener:AdapterStudentSiblingListener):RecyclerView.Adapter<AdapterStudentSibling.StudentSiblingVM>() {
    inner class StudentSiblingVM(val b:ItemStudentSiblingsBinding):ViewHolder(b.root)

    interface AdapterStudentSiblingListener{
        fun onClickStudentSibling(m:StudentSibilingResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentSiblingVM {
        return StudentSiblingVM(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_student_siblings,parent,false)
        )
    }

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: StudentSiblingVM, position: Int) {
        val model = list[position]
        holder.b.apply {
            tvSiblingName.text = model.name
            Glide.with(tvSiblingName.context).load(model.profile).placeholder(R.drawable.logo).into(ivItemSiblingProfile)

            rlItemSibling.setOnClickListener {
                listener.onClickStudentSibling(model)
            }

        }

    }

}