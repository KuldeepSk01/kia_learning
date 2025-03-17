package com.app.kiyalearning.teacher.dashboard.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.CustomWalletListBinding
import com.app.kiyalearning.databinding.CustomWalletListPenalityBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyWallet


class WalletAdapter(private val myList: List<MyWallet>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val CUSTOM_WALLET_LIST:Int=1
    private val CUSTOM_WALLET_LIST_PENALITY:Int=2


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val myHolder:RecyclerView.ViewHolder = when (viewType) {
            CUSTOM_WALLET_LIST -> {
                val binding = CustomWalletListBinding.inflate (
                    LayoutInflater.from ( parent.context),parent,false )
                MyHolder3(binding)
            } else -> {
                val binding = CustomWalletListPenalityBinding.inflate (
                    LayoutInflater.from ( parent.context),parent,false )
                MyHolder4(binding)
            }
        }

        return myHolder
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myClass = myList[position]

        when (holder.itemViewType) {
            CUSTOM_WALLET_LIST -> {
                //image Layout 2
                val myHolder1=holder as MyHolder3

                holder.binding.name.text=myClass.name
                holder.binding.classDate.text=myClass.date
                holder.binding.classTime.text=myClass.time
                //holder.binding.contractedFees.text="$"+myClass.fees
                holder.binding.contractedFees.text="₹"+myClass.fees
                holder.binding.paymentStatus.text=myClass.status

                if(myClass.status=="completed")
                    holder.binding.paymentStatus.setTextColor(holder.binding.root.context.getColor(R.color.green))
                else if(myClass.status=="rejected")
                    holder.binding.paymentStatus.setTextColor(holder.binding.root.context.getColor(R.color.red))
                //holder.binding.receiveOn.text=myClass.createdAt
            }
            else -> {

                //image Layout 2
                val myHolder1=holder as MyHolder4

                holder.binding.name.text=myClass.name
                holder.binding.classDate.text=myClass.date
                holder.binding.classTime.text=myClass.time
                holder.binding.contractedFees.text="₹"+myClass.fees
                holder.binding.status.text=myClass.status
                holder.binding.paymentStatus.text=myClass.status
                if(myClass.status=="completed")
                    holder.binding.paymentStatus.setTextColor(holder.binding.root.context.getColor(R.color.green))
                else if(myClass.status=="rejected")
                    holder.binding.paymentStatus.setTextColor(holder.binding.root.context.getColor(R.color.red))
              //  holder.binding.receiveOn.text=myClass.createdAt
            }
        }



    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return myList.size
    }

    override fun getItemViewType(position:Int):Int
    {
        val retValue:Int = when (position%2) {
            0 -> {
                CUSTOM_WALLET_LIST
            }
            else -> {
                CUSTOM_WALLET_LIST_PENALITY
            }
        }
        return retValue
    }

    // Holds the views for adding it to image and text
    class MyHolder3(customClassBinding: CustomWalletListBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }

    class MyHolder4(customClassBinding: CustomWalletListPenalityBinding) : RecyclerView.ViewHolder(customClassBinding.root) {
        val binding=customClassBinding
    }


}
