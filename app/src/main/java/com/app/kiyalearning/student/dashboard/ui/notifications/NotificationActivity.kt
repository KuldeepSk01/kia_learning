package com.app.kiyalearning.student.dashboard.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.databinding.ActivityNotificationBinding
import com.app.kiyalearning.student.dashboard.ui.notifications.viewmodel.NotificationViewModel
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.MyNotification
import com.app.kiyalearning.teacher.dashboard.ui.notifications.adapters.NotificationsAdapter
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private val notificationList= ArrayList<MyNotification>()
    private lateinit var notificationsAdapter : NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        setUpViewModel()

        binding.notificationsRecycleView.layoutManager = LinearLayoutManager(applicationContext)
        notificationsAdapter= NotificationsAdapter(notificationList)
        binding.notificationsRecycleView.adapter = notificationsAdapter

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                viewModel.getNotificationsList(this)
            }
        }


//        if(MyNetworks.isNetworkAvailable(this))
//            viewModel.getProfileDetails(this)

        binding.backIcon.setOnClickListener{
           onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        //  binding.loader.pB.visibility=View.VISIBLE
        viewModel.notificationsResponse.observe(this) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.status == 200L) {
                notificationList.clear()
                notificationList.addAll(it.data)
                notificationsAdapter.notifyDataSetChanged()
            }
        }

        viewModel.validationError.observe(this) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
        }

    }


    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(applicationContext)){
            viewModel.getNotificationsList(this)
            viewModel.markReadNotification(this)
            binding.loader.pB.visibility=View.VISIBLE
        }
    }

}