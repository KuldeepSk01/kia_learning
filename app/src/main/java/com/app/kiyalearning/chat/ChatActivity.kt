package com.app.kiyalearning.chat

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.adapters.CustomChatAdapter
import com.app.kiyalearning.chat.pojo.MessageUser
import com.app.kiyalearning.chat.pojo.MyMessage
import com.app.kiyalearning.chat.viewmodel.ChatViewModels
import com.app.kiyalearning.databinding.ActivityChatBinding
import com.app.kiyalearning.databinding.ChatFileSelectorBinding
import com.app.kiyalearning.databinding.ProfileImageViewBinding
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.ContentUriUtils
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.Permissions
import com.app.kiyalearning.util.UtilClass
import com.atwa.filepicker.core.FilePicker
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannel
import io.agora.rtm.RtmChannelAttribute
import io.agora.rtm.RtmChannelListener
import io.agora.rtm.RtmChannelMember
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmMessage
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.zip.DeflaterOutputStream
import kotlin.properties.Delegates


class ChatActivity : AppCompatActivity(), CustomChatAdapter.OnChatReplyListener {

    lateinit var binding: ActivityChatBinding
    lateinit var viewModel: ChatViewModels
    private var AppID: String? = null
    private var mRtmClient: RtmClient? = null
    private var mRtmChannel: RtmChannel? = null
    private var agoraToken: String? = null
    private var agoraUserId: String? = null
    private var channelName: String? = null
    private var messageHistory = StringBuilder()
    private var isLoginSuccess: Boolean = true
    private val pickImage = 100
    private val FILE_CHOOSER = 101

    private var groupId: String = ""
    private var coordinatorId: String = ""
    private var teacherId: String = ""
    private var studentId: String = ""
    private var coordinatorName: String = ""
    private var teacherName: String = ""
    private var studentName: String = ""


    private val messagesList = ArrayList<MyMessage>()
    private lateinit var chatAdapter: CustomChatAdapter
    private var cursorPath = ""
    private var mediaType = ""
    private var isChannelJoin = false
    private var isReloadChat = false

    private val filePicker = FilePicker.getInstance(this)

    private lateinit var compressByteArray: ByteArray
    private var isRecursion = true

    private var sortByTeacher by Delegates.notNull<Int>()
    private var sortByStudent by Delegates.notNull<Int>()
    private var sortByCoordinator by Delegates.notNull<Int>()

    private var isReplied = 0
    private var isSelfReplied = 0
    private var repliedMessageId = 0L
    private var repliedUserType = ""
    private var replyMessage: MyMessage? = null


    companion object {
        var teacherImage = ""
        var studentImage = ""
        var coordinatorImage = ""
    }

    override fun onReplyMsg(myMessage: MyMessage) {
        binding.rlMsgReplyingLayout.visibility = View.VISIBLE
        if (myMessage.messageMediaType.isNullOrEmpty()) {
            binding.tvUserMsgReplying.text = myMessage.message

        } else {
            binding.tvUserMsgReplying.text = myMessage.messageMediaName
        }
        binding.tvUserNameReplying.text = myMessage.user?.name
        Log.d(
            "MyTag",
            "replying msg ${myMessage.message} id ${myMessage.id} userType ${myMessage.userType} GroupId $groupId"
        )

        isReplied = 1
        isSelfReplied = 0
        repliedMessageId = myMessage.id
        repliedUserType = myMessage.userType
        replyMessage = myMessage

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        val chatUserName = intent.getStringExtra("GROUP_NAME")
        groupId = intent.getStringExtra("GROUP_ID").toString()
        sortByTeacher = intent.getIntExtra("SORT_BY_TEACHER", 0)
        sortByStudent = intent.getIntExtra("SORT_BY_STUDENT", 0)
        sortByCoordinator = intent.getIntExtra("SORT_BY_COORDINATOR", 0)

        Log.d(
            "MyTag",
            "AllData $groupId $sortByCoordinator $sortByStudent $sortByTeacher  $chatUserName"
        )

        binding.chatUserName.text = chatUserName

        setInitialSetup()
        setUpViewModel()

        binding.cvReplyingCrossBtn.setOnClickListener {
            isReplied = 0
            isReplied = 0
            repliedMessageId = 0L
            repliedUserType = ""
            replyMessage = null
            binding.rlMsgReplyingLayout.visibility = View.GONE

        }
        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(this)) {
                if (isLoginSuccess) {
                    if (!cursorPath.isNullOrEmpty()) {
                        Handler(Looper.getMainLooper()).post {
                            // binding.loader.pB.visibility = View.VISIBLE
                            isReloadChat = true
                            viewModel.retrieveChat(applicationContext, groupId, cursorPath)
                        }
                    } else {
                        binding.pullToRefresh.isRefreshing = false
                    }
                }
            }
        }
        binding.ivGroupPin.apply {
            this.visibility = View.VISIBLE

            when (AppPref.getUserType(this.context)) {
                "student" -> {
                    if (sortByStudent == 1) {
                        this.setBackgroundResource(R.drawable.ic_pin)
                    } else {
                        this.setBackgroundResource(R.drawable.ic_unpin)
                    }
                }

                "teacher" -> {
                    if (sortByTeacher == 1) {
                        this.setBackgroundResource(R.drawable.ic_pin)
                    } else {
                        this.setBackgroundResource(R.drawable.ic_unpin)
                    }
                }

                "coordinator" -> {
                    if (sortByCoordinator == 1) {
                        this.setBackgroundResource(R.drawable.ic_pin)
                    } else {
                        this.setBackgroundResource(R.drawable.ic_unpin)
                    }
                }

                else -> {
                    this.visibility = View.GONE
                }
            }


        }

        binding.ivGroupPin.setOnClickListener {
            if (MyNetworks.isNetworkAvailable(this@ChatActivity)) {
                if (groupId.isNullOrEmpty()) {
                    UtilClass.mToast(this@ChatActivity, "Something went wrong.. $groupId")

                } else {
                    binding.loader.pB.visibility = View.VISIBLE
                    viewModel.pinUnpinGroup(this@ChatActivity, groupId)
                }
            }
        }

        if (MyNetworks.isNetworkAvailable(this)) {
            if (groupId.isBlank()) {
                Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            } else {
                binding.loader.pB.visibility = View.VISIBLE
                viewModel.retrieveChat(applicationContext, groupId)
                viewModel.markReadChat(applicationContext, groupId)
            }
        }

        val linearLayoutManager = WrapContentLinearLayoutManager(applicationContext)
        linearLayoutManager.reverseLayout = true
        binding.chatRecycleView.itemAnimator = DefaultItemAnimator()
        binding.chatRecycleView.layoutManager = linearLayoutManager
        chatAdapter = CustomChatAdapter(messagesList, applicationContext, this, this@ChatActivity)
        binding.chatRecycleView.adapter = chatAdapter

        Log.d("Message", "Group id $groupId")


        /*    binding.pullToRefresh.setOnRefreshListener {
                if(MyNetworks.isNetworkAvailable(applicationContext))
                {
                    binding.pullToRefresh.isRefreshing=false
                    binding.loader.pB.visibility=View.VISIBLE
                    if(AppPref.getUserType(applicationContext)=="mentor" || AppPref.getUserType(applicationContext)=="expert")
                        viewModel.moreRetrieveChat(applicationContext,
                            AppPref.getUserId(applicationContext), chatUserId!!,cursorPath)
                    else
                        viewModel.moreRetrieveChat(applicationContext, chatUserId!!,
                            AppPref.getUserId(applicationContext),cursorPath)
                }
            }*/


        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
//            val heightDiff: Int = binding.root.rootView.height - binding.root.height
//            // IF height diff is more then 150, consider keyboard as visible.

            val insets = ViewCompat.getRootWindowInsets(window.decorView)
            val keyboardHeight = insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom

            if (AppPref.getKeyBoardHeightInPixel(this) == 0f)
                keyboardHeight?.toFloat()?.let { AppPref.setKeyBoardHeightInPixel(this, it) }


        }

        KeyboardVisibilityEvent.setEventListener(this) {
            val layout = binding.sendMessageLayout
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams

            val dip = 270f  //dip to pix in next lines
            val r: Resources = resources
            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )

            if (AppPref.getKeyBoardHeightInPixel(this) != 0f)
                px = AppPref.getKeyBoardHeightInPixel(this)

            Log.d(
                "MyTag",
                "onCreate: screen keyboard height=${AppPref.getKeyBoardHeightInPixel(this)}"
            )

            if (!it)
                px = 20f

            params.bottomMargin = px.toInt()
            layout.layoutParams = params
        }

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /*for reply open*/
        /*val itemTouchHelper = ItemTouchHelper(itemTouchHelperSimpleClick)
        itemTouchHelper.attachToRecyclerView(binding.chatRecycleView)
*/

        /*for reply close*/



        binding.sendButton.setOnClickListener {
            // chatAdapter.notifyDataSetChanged()

            if (isLoginSuccess) {
                if (!isChannelJoin)
                    onAgoraChannelJoin()
                else {
                    if (binding.typedMessage.text.toString().isNotBlank()) {
                        //Toast.makeText(this,"We are working on chatting Module",Toast.LENGTH_SHORT).show()
                        if (MyNetworks.isNetworkAvailable(applicationContext)) {
                            // isSendBtn = true
                            binding.loader.pB.visibility = View.VISIBLE
                            viewModel.saveMessage(
                                applicationContext, groupId,
                                binding.typedMessage.text.toString(),
                                isReplied,
                                repliedMessageId,
                                isSelfReplied,
                                repliedUserType
                            )
                            binding.rlMsgReplyingLayout.visibility = View.GONE

                        }
                    } else
                        Toast.makeText(
                            this,
                            "Please enter some text before send",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            } else
                Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

        }

        if (AppPref.getUserType(this) == "sub admin")
            binding.sendMessageLayout.visibility = View.GONE
        //  binding.rlMsgReplyingLayout.visibility = View.GONE


    }

    /*    val itemTouchHelperSimpleClick =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                this@ChatActivity,
                                com.app.kiyalearning.R.color.gray1
                            )
                        )
                        .addActionIcon(com.app.kiyalearning.R.drawable.ic_reply)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false

                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            binding.rlMsgReplyingLayout.visibility = View.VISIBLE
                            chatAdapter.notifyDataSetChanged()


                            when (viewHolder.itemViewType) {
                                1 -> {
                                    val m1 = viewHolder as CustomChatAdapter.MyHolder
                                    val name = m1.binding.username.text
                                    val msg = m1.binding.myReply.text

                                    binding.tvUserNameReplying.text = name
                                    binding.tvUserMsgReplying.text = msg
                                    Log.d("Chat", "Swipe left pos $name $msg")

                                }

                                2 -> {
                                    val m1 = viewHolder as CustomChatAdapter.MyHolder1
                                    val name = m1.binding.username.text
                                    val msg = m1.binding.myReply.text
                                    binding.tvUserNameReplying.text = name
                                    binding.tvUserMsgReplying.text = msg
                                    Log.d("Chat", "Swipe left pos $name $msg")
                                }

                                3 -> {
                                    val m1 = viewHolder as CustomChatAdapter.ImageMyHolder
                                    val name = m1.binding.username.text
                                    //val msg = m1.bi.nding.myReply.text
                                    binding.tvUserNameReplying.text = name
                                    binding.tvUserMsgReplying.text = "Image"
                                    Log.d("Chat", "Swipe left pos $name")
                                }
                                   4 -> {
                                      //image layout 2
                                  }

                               5 -> {
                                   val m1 = viewHolder as CustomChatAdapter.PDFHolder
                                   val name = m1.binding.username.text
                                   val msg = m1.binding.pdfFileName.text
                                   binding.tvUserNameReplying.text = name
                                   binding.tvUserMsgReplying.text = msg
                                   Log.d("Chat", "Swipe left pos $name $msg")
                               }

                               6 -> {
                                   val m1 = viewHolder as CustomChatAdapter.OTHERPDFHolder
                                   val name = m1.binding.username.text
                                   val msg = m1.binding.pdfFileName.text
                                   binding.tvUserNameReplying.text = name
                                   binding.tvUserMsgReplying.text = msg
                                   Log.d("Chat", "Swipe left pos $name $msg")
                               }

                               else -> {
                                   val m1 = viewHolder as CustomChatAdapter.ImageOtherSideHolder
                                   val name = m1.binding.username.text
                                   //  val msg = m1.binding.messageProfileImage
                                   binding.tvUserNameReplying.text = name
                                   binding.tvUserMsgReplying.text = "Image"
                                   Log.d("Chat", "Swipe left pos $name")
                               }
                           }
                       }
                   }
               }

           }
   */
    // Button to login to the RTM system
    private fun agoraLogin() {
        if (agoraToken != null && mRtmClient != null) {
            writeToMessageHistory("agoraLogin")
            mRtmClient!!.login(agoraToken, agoraUserId, object :
                ResultCallback<Void> {

                override fun onSuccess(responseInfo: Void) {
                    writeToMessageHistory("agora login success:")
                    onAgoraChannelJoin()
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    isLoginSuccess = false
                    val text =
                        "User: ${AppPref.getUserName(applicationContext)} failed to log in to the RTM system!$errorInfo"
                    runOnUiThread {
                        Log.d("MyTag", "agoraToken: $agoraToken")
                        Log.d("MyTag", "errorInfo: $errorInfo")
                        writeToMessageHistory("errorInfo:$errorInfo")
                        //     Log.d("MyTag", "onFailure: text=$text")
                        if (AppPref.getUserType(this@ChatActivity) != "sub admin") {
                            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            })
        }


    }

    // Button to join the RTM channel
    private fun onAgoraChannelJoin() {

        try {

            // Create an RTM channel
            writeToMessageHistory("channelName:" + channelName)
            mRtmChannel = mRtmClient!!.createChannel(channelName, object : RtmChannelListener {
                override fun onMemberCountUpdated(p0: Int) {
                }

                override fun onAttributesUpdated(p0: MutableList<RtmChannelAttribute>?) {
                }

                override fun onMessageReceived(message: RtmMessage, fromMember: RtmChannelMember) {
                    val text = message.text
                    val fromUser = fromMember.userId

                    writeToMessageHistory("agora channel onMessageReceived: ")
                    viewModel.markReadChat(applicationContext, groupId)


                    if (text.equals("image_message_data_from_device_") || text.equals("pdf_file_message_data_from_device_")) {
                        Log.d("MyTag", "agoraUserId: " + agoraUserId)
                        if (!agoraUserId.equals(fromUser, true)) {
                            if (MyNetworks.isNetworkAvailable(this@ChatActivity)) {
                                if (groupId.isBlank()) {
                                    Toast.makeText(
                                        this@ChatActivity,
                                        getString(R.string.server_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressedDispatcher.onBackPressed()
                                } else {

                                    Handler(Looper.getMainLooper()).post {
                                        binding.loader.pB.visibility = View.VISIBLE
                                        isReloadChat = true
                                        viewModel.retrieveChat(applicationContext, groupId)
                                    }
                                }
                            }
                        } else {
                            Log.d("MyTag", "same User: " + agoraUserId.equals(fromUser, true))
                        }
                    } else {
                        var date = Calendar.getInstance().time.toString()
                        if (date.length > 21)
                            date = date.substring(0, 20)


                        when (fromUser) {
                            studentId -> messagesList.add(
                                0, MyMessage(
                                    343,
                                    "student",
                                    "",
                                    "",
                                    messageMediaName = "",
                                    message = text,
                                    MessageUser(studentName, ""),
                                    created_at = date,
                                    isReplied.toString(),  //this is for isReplied
                                    isSelfReplied.toString(),
                                    repliedMessageId.toString(),
                                    replyMessage
                                )
                            )

                            teacherId -> messagesList.add(
                                0, MyMessage(
                                    343,
                                    "teacher",
                                    "",
                                    "",
                                    messageMediaName = "",
                                    message = text,
                                    MessageUser(teacherName, ""),
                                    created_at = date,
                                    isReplied.toString(),  //this is for isReplied
                                    isSelfReplied.toString(),
                                    repliedMessageId.toString(),
                                    replyMessage
                                )
                            )

                            else -> messagesList.add(
                                0, MyMessage(
                                    343,
                                    "coordinator",
                                    "",
                                    "",
                                    messageMediaName = "",
                                    message = text,
                                    MessageUser(coordinatorName, ""),
                                    created_at = date,
                                    isReplied.toString(),  //this is for isReplied
                                    isSelfReplied.toString(),
                                    repliedMessageId.toString(),
                                    replyMessage
                                )
                            )
                        }

                        Handler(Looper.getMainLooper()).post {
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecycleView.smoothScrollToPosition(0)
                        }

                    }
                }

                override fun onMemberJoined(p0: RtmChannelMember?) {

                }

                override fun onMemberLeft(p0: RtmChannelMember?) {

                }
            })
        } catch (e: RuntimeException) {
            Log.d("MyTag", "join error ${e.message}: ")

        }
        // Join the RTM channel
        mRtmChannel!!.join(object : ResultCallback<Void> {
            override fun onSuccess(responseInfo: Void?) {

                isChannelJoin = true
                Log.d("MyTag", "join onSuccess: ")
            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                val text =
                    "User: " + AppPref.getUserId(applicationContext) + " failed to join the channel!" + errorInfo.toString()
                runOnUiThread {
                    run {
                        Log.d("MyTag", "join onFailure: $text")
                        val toast =
                            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
                        toast.show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })


    }


    private fun onClickSendChannelMsg(mssg: MyMessage) {
        val msgContent = binding.typedMessage.text.toString()

        // Create <Vg k="MESS" /> message instance
        val message = mRtmClient!!.createMessage()
        message.text = msgContent

        Log.d("MyTag", "agora msgContent : $msgContent")
        // Send message to channel
        mRtmChannel!!.sendMessage(message, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                writeToMessageHistory("agora channel success message sent: ")
                // messagesList.clear()

                var date = Calendar.getInstance().time.toString()
                if (date.length > 21)
                    date = date.substring(0, 20)


                // TODO:  date and mssg id

                val random = Math.random() * 100000

                messagesList.add(
                    0, MyMessage(
                        mssg.id,
                        AppPref.getUserType(this@ChatActivity),
                        "",
                        "",
                        messageMediaName = "",
                        message = msgContent,
                        MessageUser(
                            AppPref.getUserName(this@ChatActivity),
                            AppPref.getUserImage(this@ChatActivity)
                        ),
                        mssg.created_at,
                        isReplied.toString(),  //this is for isReplied
                        isSelfReplied.toString(),
                        repliedMessageId.toString(),
                        replyMessage

                    )
                )


                Log.d("MyTag", "setUpViewModel typeText: ")

                Handler(Looper.getMainLooper()).post {

                    chatAdapter.notifyItemInserted(chatAdapter.itemCount - 1)
                    chatAdapter.notifyDataSetChanged()
                    binding.chatRecycleView.smoothScrollToPosition(0)


                    //this is for replied
                    isReplied = 0
                    isReplied = 0
                    repliedMessageId = 0L
                    repliedUserType = ""
                    replyMessage = null

                    viewModel.typeText.value = ""
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                writeToMessageHistory("agora channel Failed message sent: ")
                Toast.makeText(this@ChatActivity, "Message Sent Failed", Toast.LENGTH_SHORT).show()
//                val text = """Message fails to send to channel ${mRtmChannel!!.id} Error: $errorInfo"""
//                writeToMessageHistory(text)
                onBackPressedDispatcher.onBackPressed()
            }
        })

    }

    private fun sendImageOrPdfChannelMsg(msgContent: String) {

        // Create <Vg k="MESS" /> message instance
        val message = mRtmClient!!.createMessage()
        message.text = msgContent

        reloadChat()

        // Send message to channel
        mRtmChannel!!.sendMessage(message, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                binding.typedMessage.setText("")
                mediaType = ""

            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Toast.makeText(this@ChatActivity, "Message Sent Failed", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    private fun reloadChat() {
        if (MyNetworks.isNetworkAvailable(this@ChatActivity)) {
            binding.loader.pB.visibility = View.VISIBLE
            messagesList.clear()

            if (groupId.isBlank()) {
                Toast.makeText(
                    this@ChatActivity,
                    getString(R.string.server_error),
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressedDispatcher.onBackPressed()
            } else {
                binding.loader.pB.visibility = View.VISIBLE
                isReloadChat = true
                viewModel.retrieveChat(applicationContext, groupId)
            }
        }
    }

    // Button to log out of the RTM system
    private fun onUserLogout() {
        // Log out of the RTM system
        mRtmClient?.logout(null)
    }

    // Button to leave the RTM channel
    fun onClickLeave() {
        // Leave the RTM channel
        mRtmChannel?.leave(null)
    }


    // Write message records to the TextView
    fun writeToMessageHistory(record: String) {
        Log.d("MyTag", "record= $record")
        messageHistory.append(record)
        // Log.d("MyTag", "message_history= $messageHistory")
    }

    private val imageSelectActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("MyTAG", "selected image uri ${it.data} ")
            mediaType = "image"
            if (it.resultCode == RESULT_OK && it.data != null) {
                val data = it?.data
                val realFilePathWithoutCompress =
                    UtilClass.getPathFromURI(data!!.data, this@ChatActivity)
                val realFilePath = compressImageFilePath(
                    BitmapFactory.decodeFile(realFilePathWithoutCompress),
                    this@ChatActivity
                )

                if (MyNetworks.isNetworkAvailable(this@ChatActivity.applicationContext)) {
                    val file = File(realFilePath)
                    if (UtilClass.checkFileSize(file) < 10) {
                        binding.loader.pB.visibility = View.VISIBLE
                        val requestFile: RequestBody =
                            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                        // Create an image file name
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        var imageFileName = "JPEG_$timeStamp.jpeg"
                        //for getting real name
                        imageFileName =
                            UtilClass.getFileName(this@ChatActivity.contentResolver, data.data)

                        val multipartBody = MultipartBody.Part.createFormData(
                            "message_media",
                            imageFileName,
                            requestFile
                        )
                        viewModel.saveImageMessageApi(
                            applicationContext, groupId, "", mediaType, multipartBody
                        )

                    } else {
                        UtilClass.mToast(
                            this@ChatActivity,
                            "Image should be less then or equal to 10MB"
                        )
                    }
                }
            } else {
                UtilClass.mToast(this@ChatActivity, "Nothing  selected")
            }
        }

    private fun setInitialSetup() {


        binding.attachFile.setOnClickListener {
            if (!isChannelJoin)
                onAgoraChannelJoin()
            else {
                val dialog = BottomSheetDialog(this, R.style.DialogStyle)
                val bottomSheet = ChatFileSelectorBinding.inflate(layoutInflater)
                bottomSheet.closeButton.setOnClickListener { dialog.dismiss() }
                bottomSheet.imageFile.setOnClickListener {
                    UtilClass.currentPhotoPath = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                        imageSelectActivityLauncher.launch(gallery)
                    } else {
                        val permit = Permissions.verifyStoragePermissions(this@ChatActivity)
                        if (permit != PackageManager.PERMISSION_GRANTED)
                        else {
                            val gallery =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                                )
                            imageSelectActivityLauncher.launch(gallery)
                        }
                    }
                    dialog.dismiss()


                    /*  UtilClass.currentPhotoPath = null
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                          val gallery = Intent(MediaStore.ACTION_PICK_IMAGES)
                          Log.d("MyTag", "setInitialSetup: PERMISSION_GRANTED 13")
                          startActivityForResult(gallery, pickImage)
                      } else {
                          val permit = Permissions.verifyStoragePermissions(this)
                          if (permit != PackageManager.PERMISSION_GRANTED)
                          else {
                              Log.d("MyTag", "setInitialSetup: PERMISSION_GRANTED")
                              //val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                              val gallery = Intent(
                                  Intent.ACTION_PICK,
                                  MediaStore.Images.Media.INTERNAL_CONTENT_URI
                              )
                              Log.d(
                                  "MyTag",
                                  "setInitialSetup: PERMISSION_GRANTED" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                              )
                              startActivityForResult(gallery, pickImage)

                          }
                      }*/
                    dialog.dismiss()
                }
                bottomSheet.pdfFile.setOnClickListener {
                    try {
                        filePicker.pickFile { meta ->
                            val name: String? = meta?.name
                            val sizeKb: Int? = meta?.sizeKb
                            val file: File? = meta?.file

                            if (file != null) {
                                selectedPdf(file)
                            } else {
                                UtilClass.mToast(this@ChatActivity, "Nothing selected")
                            }
                        }

                    } catch (e: Exception) {
                        Log.d("MyTag", "Pdf select error ${e.message}")
                    }

                    dialog.dismiss()
                }
                dialog.setContentView(bottomSheet.root)
                dialog.show()
            }
        }

        binding.videoCallButton.visibility = View.GONE
        binding.audioCallButton.visibility = View.GONE

        rtmInitialize()

        val chatUserImage = intent.getStringExtra("GROUP_IMAGE")
        Glide.with(binding.root)
            .load(chatUserImage)
            .fitCenter()
            .into(binding.chatUserImage)

        binding.chatUserImage.setOnClickListener {
            val viewBinding = ProfileImageViewBinding.inflate(LayoutInflater.from(this))
            viewBinding.tvDownloadImg.visibility = View.GONE
            viewBinding.tvCancelImg.visibility = View.GONE
            Glide.with(this)
                .load(chatUserImage)
                .fitCenter()
                .into(viewBinding.profileImage)

            AlertDialog.Builder(this)
                .setView(viewBinding.root)
                .show()
        }

    }

    private fun selectedPdf(files: File) {
        UtilClass.currentPhotoPath = ContentUriUtils.getFilePath(this, Uri.fromFile(files))
        mediaType = "pdf"
        var file = File("")
        if (UtilClass.currentPhotoPath != null)
            file = File(UtilClass.currentPhotoPath)
        if (UtilClass.checkFileSize(file) < 10) {
            if (MyNetworks.isNetworkAvailable(applicationContext)) {
                binding.loader.pB.visibility = View.VISIBLE
                val requestFile: RequestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                var imageFileName = "JPEG_$timeStamp.jpeg"

                imageFileName = file.name
                val multipartBody =
                    MultipartBody.Part.createFormData(
                        "message_media",
                        imageFileName,
                        requestFile
                    )
                Log.d("MyTag", "imageFileName: $imageFileName")

                viewModel.saveImageMessageApi(
                    applicationContext, groupId, "", mediaType, multipartBody
                )
            }

        } else {
            Toast.makeText(
                this@ChatActivity,
                "Pdf should be less then or equal 10MB",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun rtmInitialize() {
        try {
            AppID = "1e945242e4f6428f8aec62e0e53bbf8c"
            // Initialize the RTM client
            mRtmClient = RtmClient.createInstance(baseContext, AppID, object : RtmClientListener {
                override fun onConnectionStateChanged(state: Int, reason: Int) {
                    val text = "Connection state changed to " + state + "Reason: " + reason + "\n"
                    writeToMessageHistory(text)

//                    if(agoraUserId!!.isNotBlank())
//                        agoraLogin()

                }

                override fun onTokenExpired() {
                    onBackPressedDispatcher.onBackPressed()
                }

                override fun onTokenPrivilegeWillExpire() {
                    TODO("Not yet implemented")
                }


                override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {

                }

                override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String) {
                    val text =
                        "Message received from " + peerId + " Message: " + rtmMessage.text + "\n"
                    writeToMessageHistory(text)

                    val rcdMssg = rtmMessage.text

                }
            })

        } catch (e: Exception) {
            throw RuntimeException("RTM initialization failed!")
        }
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[ChatViewModels::class.java]

        viewModel.pinUnpinGroupResponse.observe(this@ChatActivity) {
            binding.loader.pB.visibility = View.GONE
            if (it.success) {
                Log.d("MyTag", "Pin Action ${it.userAction} group id $groupId")

                if (it.userAction == 1) {
                    binding.ivGroupPin.setBackgroundResource(R.drawable.ic_pin)
                } else {
                    binding.ivGroupPin.setBackgroundResource(R.drawable.ic_unpin)
                }
                UtilClass.mToast(this@ChatActivity, it.message)
            } else {
                UtilClass.mToast(this@ChatActivity, "Something went wrong !")
            }
        }

        viewModel.validationError.observe(this) {
            binding.pullToRefresh.isRefreshing = false
            binding.loader.pB.visibility = View.GONE
            //  binding.pullToRefresh.isRefreshing=false
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.retrieveChatResponse.observe(this) {
            binding.pullToRefresh.isRefreshing = false

            binding.loader.pB.visibility = View.GONE


            //  binding.pullToRefresh.isRefreshing=false
            if (it.success) {
                /*if (isSendBtn){
                    messagesList.clear()
                }*/
                Log.d("MyTag", "setUpViewModel getUserToken:" + AppPref.getUserToken(this))
                Log.d("ChatMessage", "setUpViewModel data:" + it.data)

                Handler(Looper.getMainLooper()).post {
                    // messagesList.clear()
                    // chatAdapter.notifyDataSetChanged()

                    messagesList.addAll(it.data)
                    cursorPath = it.nextPage
                    chatAdapter.notifyDataSetChanged()
                }

//                binding.chatNestedScroll.smoothScrollTo(0, binding.chatNestedScroll.bottom)
//                binding.chatNestedScroll.post(Runnable {
//                    binding.chatNestedScroll.fullScroll(View.FOCUS_DOWN)
//                })


                teacherId = it.teacherAgoraId
                studentId = it.studentAgoraId
                coordinatorId = it.coordinatorAgoraId


                teacherImage = it.teacherProfile
                studentImage = it.studentProfile
                coordinatorImage = it.coordinatorProfile

                teacherName = it.teacherName
                studentName = it.studentName
                coordinatorName = it.coordinatorName


                Log.d("MyTag", "response: $it")


                agoraToken = it.agoraToken
                channelName = it.channelName

//                if(!AppPref.getUserType(this).contains("student") && !AppPref.getUserType(this).contains("teacher"))
//                {
//                    Toast.makeText(this,"App is only live for student and teacher currently",Toast.LENGTH_SHORT).show()
//                    onBackPressedDispatcher.onBackPressed()
//                }

                agoraUserId = if (AppPref.getUserType(this).contains("student"))
                    it.studentAgoraId
                else if (AppPref.getUserType(this).contains("teacher"))
                    it.teacherAgoraId
                else
                    it.coordinatorAgoraId

                if (!isReloadChat)
                    agoraLogin()

            } else
                viewModel.validationError.value = it.message
        }

        viewModel.deleteChatResponse.observe(this) {
            binding.loader.pB.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false

            //   binding.pullToRefresh.isRefreshing=false
            if (it.success) {
                reloadChat()
            } else
                viewModel.validationError.value = it.message
        }



        viewModel.saveMessageResponse.observe(this) {
            binding.pullToRefresh.isRefreshing = false
            binding.loader.pB.visibility = View.GONE



            if (it.success) {
                //    messagesList.clear()
                val rcdMssg = binding.typedMessage.text.toString()
                if (mRtmClient != null) {
                    onClickSendChannelMsg(it.data)


                    //  viewModel.retrieveChatResponse..
                    binding.typedMessage.setText("")
                } else
                    onBackPressedDispatcher.onBackPressed()
                UtilClass.currentPhotoPath = null


                /*   viewModel.sendFCMMessageNotification(applicationContext, FCMMissedCall(
                       Notification("Message from "+AppPref.getUserName(binding.root.context),
                           rcdMssg,"high","default"),callOppositeSideUser,
                       RawData(AppPref.getUserName(applicationContext),
                           "","","","","message","","","")))*/

            } else
                viewModel.validationError.value = it.message
        }




        viewModel.saveImageMessageResponse.observe(this) {
            binding.pullToRefresh.isRefreshing = false

            binding.loader.pB.visibility = View.GONE
            if (it.success) {
                UtilClass.currentPhotoPath = null
                if (mRtmClient != null) {
                    //  if(mssgImgUrl!=null)
                    //   messagesHistory(mssgImgUrl!!.toString(), isMyMessage = true, isImage = true)
                    //   onClickSendPeerMsg(true)

                    sendImageOrPdfChannelMsg("image_message_data_from_device_")

                } else
                    onBackPressedDispatcher.onBackPressed()


            } else
                viewModel.validationError.value = it.message


        }

        viewModel.typeText.observe(this) {

            Log.d("MyTag", "setUpViewModel typeText: ")
            //    binding.chatRecycleView.smoothScrollToPosition(0)

            binding.typedMessage.setText("")

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        onUserLogout()
    }

    fun deleteMessage(mssgId: String) {
        if (MyNetworks.isNetworkAvailable(this@ChatActivity)) {
            binding.loader.pB.visibility = View.VISIBLE
            if (groupId.isBlank()) {
                Toast.makeText(
                    this@ChatActivity,
                    getString(R.string.server_error),
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressedDispatcher.onBackPressed()
            } else {
                binding.loader.pB.visibility = View.VISIBLE
                isReloadChat = true
                viewModel.deleteChat(applicationContext, mssgId)
            }
        }
    }

    /*  @Deprecated("Deprecated in Java")
      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)
          if (resultCode == RESULT_OK && requestCode == pickImage) {

              mediaType = "image"
              //val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data!!)
              UtilClass.currentPhotoPath = UtilClass.getPathFromURI(data!!.data, this)

              UtilClass.currentPhotoPath =
                  compressImageFilePath(BitmapFactory.decodeFile(UtilClass.currentPhotoPath), this)!!

              var file = File("")
              if (UtilClass.currentPhotoPath != null)
                  file = File(UtilClass.currentPhotoPath)

              var withPhoto = false
              Log.d("MyTag", "file.exists(): " + file.exists())
              Log.d("MyTag", "UtilClass.currentPhotoPath==${UtilClass.currentPhotoPath}")
              Log.d("MyTag", "compress file size ${UtilClass.checkFileSize(file)} ")



              if (UtilClass.currentPhotoPath != null && UtilClass.currentPhotoPath.isNotBlank() && file.exists())
                  withPhoto = true

              if (withPhoto) {
                  if (isLoginSuccess) {
                      if (MyNetworks.isNetworkAvailable(applicationContext)) {
                          // hitUploadCompressImageApi(file.path, data?.data!!)

                          if (UtilClass.checkFileSize(file) < 10) {
                              binding.loader.pB.visibility = View.VISIBLE
                              val requestFile: RequestBody =
                                  RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                              // Create an image file name
                              val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                              var imageFileName = "JPEG_$timeStamp.jpeg"

                              //for getting real name
                              imageFileName = UtilClass.getFileName(contentResolver, data.data)

                              val multipartBody = MultipartBody.Part.createFormData(
                                  "message_media",
                                  imageFileName,
                                  requestFile
                              )
                              viewModel.saveImageMessageApi(
                                  applicationContext, groupId, "", mediaType, multipartBody
                              )

                          } else {
                              Toast.makeText(
                                  this@ChatActivity,
                                  "Images should be less then or equal 10MB",
                                  Toast.LENGTH_LONG
                              ).show()
                          }

                      }
                  }
              } else {
                  *//*val body=null
                Log.d("MyTag", "withPhoto: $withPhoto")
                viewModel.callProfileUpdateImageApi(map,this,body)*//*
                Toast.makeText(this, R.string.server_error, Toast.LENGTH_SHORT).show()
            }

        } else if (resultCode == RESULT_OK && requestCode == FILE_CHOOSER) {


            *//*for pdf compressing process*//*

            //media type used for end fcm message
            mediaType = "pdf"
            // var file = UtilClass.getPathFromURI(data!!.data, this)

            val imageDataList: MutableList<ByteArray> = ArrayList()
            val imageUri: Uri = data?.data!!
            val bytes = UtilClass.getBytes(imageUri, this)
            //val c1 = compressed(bytes)
            imageDataList.add(bytes)

            Log.d(
                "MyTag",
                "Chat bytes ${bytes.size} with compress bytes ${bytes?.size} "
            )

            val pdfSizeInKb = bytes!!.size / 1024.toDouble()
            val pdfSizeInMB = pdfSizeInKb / 1024
            if (pdfSizeInMB < 10.toDouble()) {
                // pass the byte array list to be uploaded.
                if (MyNetworks.isNetworkAvailable(applicationContext)) {
                    binding.loader.pB.visibility = View.VISIBLE
                    //  val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)

                    val requestFile: RequestBody = RequestBody.create(
                        "application/pdf".toMediaTypeOrNull(),
                        imageDataList[0]
                    )
                    // Create an image file name
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    var imageFileName = "JPEG_$timeStamp.pdf"

                    imageFileName = UtilClass.getFileName(contentResolver, data.data)

                    val multipartBody =
                        MultipartBody.Part.createFormData(
                            "message_media",
                            imageFileName,
                            requestFile
                        )
                    Log.d("MyTag", "imageFileName: $imageFileName")

                    viewModel.saveImageMessageApi(
                        applicationContext, groupId, "", mediaType, multipartBody
                    )
                }

            } else {
                Toast.makeText(
                    this@ChatActivity,
                    "Pdf should be less then or equal 10MB",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {
            Toast.makeText(this, "Nothing  selected", Toast.LENGTH_SHORT).show()
        }
    }
*/

    private fun compressImageFilePath(bm: Bitmap, context: Context): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File(context.filesDir, timeStamp + ".png")
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 50, fos)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(fos != null)
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file.path
    }


    private fun compressed(byteArray: ByteArray?): ByteArray? {
        compressByteArray = byteArray!!
        Log.d("MyTag", "compressed byte array ${byteArray!!.size}")
        return try {
            val out = ByteArrayOutputStream()
            val defl = DeflaterOutputStream(out)
            defl.write(compressByteArray)
            defl.flush()
            defl.close()
            compressByteArray = out.toByteArray()
            val pdfSizeInKb = compressByteArray!!.size / 1024.toDouble()
            val pdfSizeInMB = pdfSizeInKb / 1024
            if (pdfSizeInMB < 10) {
                compressByteArray
            } else {
                if (!isRecursion) {
                    compressByteArray = compressed(compressByteArray)!!
                } else {
                    isRecursion = false
                    compressed(compressByteArray)!!
                }

            }

            compressByteArray

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            System.exit(150)
            null
        }
    }


}

class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    //... constructor
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }


}