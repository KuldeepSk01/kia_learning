package com.app.kiyalearning.chat

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.chat.adapters.ChatListAdapter
import com.app.kiyalearning.chat.pojo.Group
import com.app.kiyalearning.chat.viewmodel.ChatViewModels
import com.app.kiyalearning.databinding.ActivityChatListBinding
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass


class ChatListActivity : AppCompatActivity(), ChatListAdapter.OnGroupPinListener {

    lateinit var binding: ActivityChatListBinding
    private val chatList = ArrayList<Group>()
    private lateinit var chatAdapter: ChatListAdapter
    lateinit var viewModel: ChatViewModels
    var isFromFirebaseMessage: Boolean = false
    var timer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        binding.backIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setUpViewModel()
        timer = object : CountDownTimer(40000000, 30000) {
            //40000 milli seconds is total time, 1000 milli seconds is time interval
            override fun onTick(millisUntilFinished: Long) {
                if (MyNetworks.isNetworkAvailable(this@ChatListActivity)) {
                    Log.d("MyTag", "onTick: ")
                    binding.loader.pB.visibility = View.VISIBLE
                    viewModel.getChatGroups(
                        this@ChatListActivity,
                        binding.searchGroup.text.toString()
                    )
                }
            }

            override fun onFinish() {}
        }

        isFromFirebaseMessage = intent.getBooleanExtra("FIREBASE_MESSAGE", false)

        binding.contactsChatsRecycleView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatListAdapter(chatList, this@ChatListActivity)
        binding.contactsChatsRecycleView.adapter = chatAdapter


        binding.searchGroup.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                // if (s.length != 0)
                if (MyNetworks.isNetworkAvailable(this@ChatListActivity)) {
                    binding.loader.pB.visibility = View.VISIBLE
                    viewModel.getChatGroups(this@ChatListActivity, s.toString())
                }
            }
        })

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ChatViewModels::class.java]

        viewModel.chatListResponse.observe(this) {
            binding.loader.pB.visibility = View.GONE
            if (it.status == 200L) {
                chatList.clear()
                val list = it.data as ArrayList<Group>
                var unreadList = listOf<Group>()
                if (AppPref.getUserType(this@ChatListActivity).contains("teacher")) {
                    Log.d("MyTag","Group ${list.toString()}")
                    unreadList = list.filter { data -> data.teacherUnread.toInt() >= 1 }
                        .sortedByDescending { data -> data.teacherUnread }
                } else if (AppPref.getUserType(this@ChatListActivity).contains("student")) {
                    unreadList = list.filter { data -> data.studentUnread.toInt() >= 1 }
                        .sortedByDescending { data -> data.studentUnread }
                } else {
                    unreadList = list.filter { data -> data.coordinatorUnread.toInt() >= 1 }
                        .sortedByDescending { data -> data.coordinatorUnread }
                }

                list.removeAll(unreadList)
                chatList.addAll(unreadList)
                chatList.addAll(list)
                chatAdapter.notifyDataSetChanged()

                /*  Log.d("MyTag", "isFromFirebaseMessage: $isFromFirebaseMessage")
                  Log.d("MyTag", "USER_ID: "+intent.getStringExtra("USER_ID"))

                  if(isFromFirebaseMessage)
                  {
                      val userId=intent.getStringExtra("USER_ID")
                      Log.d("MyTag", "USER_ID: $userId")
                      var itemIndex=-1
                      for ( (index,users) in chatList.withIndex())
                      {
                          Log.d("MyTag", "USER_ID==: "+users.id)
                          if(users.id.toString().equals(userId,true))
                          {

                              itemIndex=index
                          }
                      }

                      Log.d("MyTag", "itemIndex: $itemIndex")

                      if(itemIndex!=-1)
                      {
                          val intent= Intent(this, ChatActivity::class.java)
                          intent.putExtra("CHAT_USER_NAME",chatList[itemIndex].name)
                          intent.putExtra("CHAT_USER_IMAGE",chatList[itemIndex].photo)
                          intent.putExtra("CHAT_USER_ID",chatList[itemIndex].id.toString())
                          intent.putExtra("OPPOSITE_SIDE_USER",chatList[itemIndex].notificationToken)
                          intent.putExtra("IS_USER_CHAT",true)
                          startActivity(intent)
                      }
                  }*/

            } else
                Toast.makeText(this, "Error Occurred in Chat List", Toast.LENGTH_SHORT).show()

        }

        viewModel.validationError.observe(this) {
            binding.loader.pB.visibility = View.GONE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.pinUnpinGroupResponse.observe(this) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.getChatGroups(this, binding.searchGroup.text.toString())
        }

    }

    override fun onResume() {
        super.onResume()

        timer?.start()

        if (MyNetworks.isNetworkAvailable(this)) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.getChatGroups(this, binding.searchGroup.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        isFromFirebaseMessage = false
    }

    override fun onPinGroup(group: Group) {
        if (MyNetworks.isNetworkAvailable(this)) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.pinUnpinGroup(this, group.id.toString())
        }
    }

}