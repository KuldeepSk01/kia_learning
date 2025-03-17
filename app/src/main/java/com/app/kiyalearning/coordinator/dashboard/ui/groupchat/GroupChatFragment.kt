package com.app.kiyalearning.coordinator.dashboard.ui.groupchat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.adapters.ChatListAdapter
import com.app.kiyalearning.chat.pojo.Group
import com.app.kiyalearning.chat.viewmodel.ChatViewModels
import com.app.kiyalearning.coordinator.dashboard.CoordinatorDashBoardActivity
import com.app.kiyalearning.databinding.FragmentCooChatListBinding
import com.app.kiyalearning.util.MyNetworks


class GroupChatFragment : Fragment(), ChatListAdapter.OnGroupPinListener {

    private var _binding: FragmentCooChatListBinding? = null
    private val chatList = ArrayList<Group>()
    private lateinit var chatAdapter: ChatListAdapter
    lateinit var viewModel: ChatViewModels
    var isFromFirebaseMessage: Boolean = false


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCooChatListBinding.inflate(inflater, container, false)
        val activity = activity as CoordinatorDashBoardActivity
        if (activity.binding != null)
            activity.binding!!.headerTxt.text = resources.getString(R.string.group_chat)


        setUpViewModel()

        isFromFirebaseMessage = activity.intent.getBooleanExtra("FIREBASE_MESSAGE", false)

        binding.contactsChatsRecycleView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatListAdapter(chatList,this@GroupChatFragment)
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
                if (MyNetworks.isNetworkAvailable(requireContext())) {
                    binding.loader.pB.visibility = View.VISIBLE
                    viewModel.getChatGroups(requireContext(), s.toString())
                }

            }
        })

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ChatViewModels::class.java]

        viewModel.chatListResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            if (it.status == 200L) {
                chatList.clear()
                val list = it.data as ArrayList<Group>

                val unreadList =
                    list.filter { data -> data.coordinatorUnread.toInt() >= 1 }.sortedByDescending { data -> data.coordinatorUnread.toInt() }


               //val sortedList =  unreadList.sortedBy { data -> data.coordinatorUnread.toInt() }
                list.removeAll(unreadList)
                Log.d("MyTag","Sorted $unreadList")

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
                Toast.makeText(requireContext(), "Error Occurred in Chat List", Toast.LENGTH_SHORT)
                    .show()

        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.GONE
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.notificationsResponse.observe(viewLifecycleOwner) {
            if (it.status == 200L) {
                val activity = requireActivity() as CoordinatorDashBoardActivity
                if (it.unreadNotification) {
                    activity.binding!!.redDot.visibility = View.VISIBLE
                } else {
                    activity.binding!!.redDot.visibility = View.GONE
                }
            }
        }

        viewModel.pinUnpinGroupResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.getChatGroups(requireContext(), binding.searchGroup.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        if (MyNetworks.isNetworkAvailable(requireContext())) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.getNotificationsList(requireContext())
            viewModel.getChatGroups(requireContext(), binding.searchGroup.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        isFromFirebaseMessage = false
    }

    override fun onPinGroup(group: Group) {
        if (MyNetworks.isNetworkAvailable(requireContext())) {
            binding.loader.pB.visibility = View.VISIBLE
            viewModel.pinUnpinGroup(requireContext(), group.id.toString())
        }
    }


}





