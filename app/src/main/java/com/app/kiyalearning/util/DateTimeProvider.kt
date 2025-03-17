package com.app.kiyalearning.util
import android.util.Log
import java.net.URL
import java.util.*

class DateTimeProvider {

    val dateTime: Date
        get() {
            val d = internetTime
            return d ?: Date()
        }//Get website datetime (timestamp)

    //convert to standard time object
//Generate connection object
    //issue the connection
//Get resource object
    /*public  Date getSystemTime()
       {
             if(date==null)
           { return new Date(); }
           else
               return date;


       }

       public Date getSetTime()
       {
          if(date==null)
           { return new Date(); }
           else
               return date;
       }
       */

    private val internetTime: Date?
        get() = try {
            val url = URL("https://www.google.com") //Get resource object
            val conn = url.openConnection() //Generate connection object
            conn.connect() //issue the connection
            conn.connectTimeout = 10000
            val dateL = conn.date //Get website datetime (timestamp)
            Log.d("MyTag", ": internet time")
            Date(dateL) //convert to standard time object
        } catch (ex: Exception) {
            null
        }
}