package com.app.kiyalearning.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.kiyalearning.R


class MySpinnerAdapter : ArrayAdapter<String?> {

    var textSize:Float=0f

    constructor(context: Context, resource: Int) : super(context, resource) {}

    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId) {}

    constructor(context: Context, resource: Int, objects: Array<String?>?) : super(context, resource, objects!!) {
    }

    //defined by me
    constructor(context: Context, resource: Int, objects: Array<String?>?, textSize:Float) : super(context, resource, objects!!) {
        this.textSize=textSize
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<String?>?) : super(context, resource, textViewResourceId, objects!!) {
    }

    //define by me
    constructor(context: Context, resource: Int, objects: List<String?>?, textSize:Float) : super(context, resource, objects!!) {
        this.textSize=textSize
    }

    constructor(context: Context, resource: Int, objects: List<String?>?) : super(context, resource, objects!!) {
    }

    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: List<String?>?) : super(context, resource, textViewResourceId, objects!!) {
    }

    override fun isEnabled(position: Int): Boolean {
        // Disable the first item from Spinner
        // First item will be used for hint
        return position != 0
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
        //set the color of first item in the drop down list to gray
        if(position == 0) {
            view.setTextColor(Color.GRAY)
        } else {
            view.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))

            //here it is possible to define color for other items by
            //view.setTextColor(Color.RED)
        }
        return view
    }
}