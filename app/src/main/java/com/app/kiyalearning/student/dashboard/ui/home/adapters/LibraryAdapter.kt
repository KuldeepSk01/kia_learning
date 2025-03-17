package com.app.kiyalearning.student.dashboard.ui.home.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.dashboard.ui.library.ViewPDFActivity
import com.app.kiyalearning.databinding.CustomLibraryBinding
import com.app.kiyalearning.student.dashboard.ui.home.pojos.LibraryData


class LibraryAdapter(private val myList: List<LibraryData>) : RecyclerView.Adapter<LibraryAdapter.MyHolder>() {


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {

        val binding = CustomLibraryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )


        return MyHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val library = myList[position]

        holder.binding.fileName.text=library.title
        holder.binding.country.text=library.country
        holder.binding.curriculum.text=library.curriculum
        holder.binding.grade.text=library.garde
        holder.binding.subject.text=library.subject



        holder.binding.viewPdfButton.setOnClickListener{
            val pdfIntent=Intent(holder.binding.viewPdfButton.context,ViewPDFActivity::class.java)
            pdfIntent.putExtra("PDF_LINK",library.pdf)
            pdfIntent.putExtra("PDF_FILE_NAME",library.title)
            holder.binding.viewPdfButton.context.startActivity(pdfIntent)
        }

    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }


    // Holds the views for adding it to image and text
    class MyHolder(customClassBinding: CustomLibraryBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
