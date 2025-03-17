package com.app.kiyalearning.chat.adapters

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.kiyalearning.R
import com.app.kiyalearning.chat.ChatActivity
import com.app.kiyalearning.chat.pojo.MyMessage
import com.app.kiyalearning.databinding.CustomChat1Binding
import com.app.kiyalearning.databinding.CustomChatBinding
import com.app.kiyalearning.databinding.CustomChatImageBinding
import com.app.kiyalearning.databinding.CustomChatImageOtherBinding
import com.app.kiyalearning.databinding.CustomChatPdfBinding
import com.app.kiyalearning.databinding.CustomChatPdfOtherBinding
import com.app.kiyalearning.databinding.LeftReplyChatBinding
import com.app.kiyalearning.databinding.OnLongPressDialogLayoutBinding
import com.app.kiyalearning.databinding.ProfileImageViewBinding
import com.app.kiyalearning.databinding.RightReplyChatBinding
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.UtilClass
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class CustomChatAdapter(
    private val messageList: ArrayList<MyMessage>,
    private val context: Context,
    private val chatActivity: ChatActivity,
    private val listener: OnChatReplyListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnChatReplyListener {
        fun onReplyMsg(myMessage: MyMessage)
    }

    private val LAYOUT_ONE: Int = 1
    private val LAYOUT_TWO: Int = 2

    private val IMAGE_LAYOUT_ONE: Int = 3
    private val IMAGE_LAYOUT_TWO: Int = 4

    private val MY_PDF_LAYOUT: Int = 5
    private val OTHER_PDF_LAYOUT: Int = 6
    private val LEFT_CHAT_REPLY_LAYOUT: Int = 7
    private val RIGHT_CHAT_REPLY_LAYOUT: Int = 8

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item

        val myHolder: RecyclerView.ViewHolder = when (viewType) {
            LAYOUT_ONE -> {
                val binding = CustomChatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MyHolder(binding)
            }

            LAYOUT_TWO -> {
                val binding = CustomChat1Binding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

//                val binding = LeftReplyChatBinding.inflate(
//                    LayoutInflater.from(parent.context), parent, false
//                )
                MyHolder1(binding)
            }

            IMAGE_LAYOUT_ONE -> {
                val binding = CustomChatImageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ImageMyHolder(binding)
            }

            MY_PDF_LAYOUT -> {
                val binding = CustomChatPdfBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PDFHolder(binding)
            }

            OTHER_PDF_LAYOUT -> {
                val binding = CustomChatPdfOtherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                OTHERPDFHolder(binding)
            }

            IMAGE_LAYOUT_TWO -> {
                val binding = CustomChatImageOtherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ImageOtherSideHolder(binding)
            }


            //------------For Reply layout-----------------

            LEFT_CHAT_REPLY_LAYOUT -> {
                val binding = LeftReplyChatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LeftReplyChatHolder(binding)
            }

            else -> {
                val binding = RightReplyChatBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                RightReplyChatHolder(binding)
            }
        }

        return myHolder

    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        Log.d("Chating","Users ${message.user!!.name} id ${message}")

        //  Log.d("MyTag", "additionalMedia?.slug: "+message.additionalMedia?.slug)
        when (holder.itemViewType) {
            LAYOUT_ONE -> {
                val myHolder = holder as MyHolder
                myHolder.binding.myReply.text = message.message
                myHolder.binding.username.text = message.user!!.name
                myHolder.binding.myReplyTime.text = message.created_at

                myHolder.binding.myReplyLayout.setOnLongClickListener(object : OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder.binding.myReply.context,
                            myHolder.binding.myReply,
                            message!!,
                            true,
                            true
                        )
                        return true
                    }

                })

                when (message.userType) {
                    "teacher" -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.teacherImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }

                    "student" -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.studentImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }

                    else -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.coordinatorImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }
                }

                /*
                                myHolder.binding.delete.setOnClickListener {

                                    val cal = Calendar.getInstance().time
                                    val date = SimpleDateFormat("dd MMM yyyy hh:mm a").parse(message.created_at)

                                    val afterAdding15Mins = Date(date!!.time + 15 * 60 * 1000)
                                    if (cal <= afterAdding15Mins) {
                                        val dialogBuilder = AlertDialog.Builder(myHolder.binding.delete.context)
                                        val alertDialog = dialogBuilder.create()

                                        dialogBuilder.setMessage("Sure to Delete Message")
                                        dialogBuilder.setPositiveButton("Ok") { _, _ ->
                                            chatActivity.deleteMessage(message.id.toString())
                                        }

                                        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                                            alertDialog.cancel()
                                        }
                                        dialogBuilder.show()
                                    } else {
                                        Toast.makeText(
                                            myHolder.binding.username.context,
                                            "Cannot delete messages older than 15min",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                */

            }

            LAYOUT_TWO -> {
                val myHolder = holder as MyHolder1
                myHolder.binding.myReply.text = message.message
                // myHolder.binding.tempMyReply.text = message.message
                myHolder.binding.username.text =
                    String.format("%s %s", message.user!!.name, "(${message.userType})")
                myHolder.binding.myReplyTime.text = message.created_at

                when (message.userType) {
                    "teacher" -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.teacherImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }

                    "student" -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.studentImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }

                    else -> {
                        Glide.with(myHolder.binding.username.context)
                            .load(ChatActivity.coordinatorImage).centerCrop()
                            .placeholder(R.drawable.logo).into(myHolder.binding.messageProfileImage)
                    }
                }

                myHolder.binding.myReplyTopLayout.setOnLongClickListener(object :
                    OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder.binding.myReply.context,
                            myHolder.binding.myReply,
                            message!!,
                            true
                        )

                        return true
                    }

                })

            }

            IMAGE_LAYOUT_ONE -> {
                val myHolder1 = holder as ImageMyHolder

                Glide.with(holder.itemView.context).load(message.messageMedia).centerCrop()
                    .placeholder(R.drawable.ic_baseline_image_24).into(myHolder1.binding.image)

                myHolder1.binding.myReplyTime.text = message.created_at
                myHolder1.binding.username.text = message.user!!.name

                myHolder1.binding.image.setOnClickListener {
                    Log.d("MyTag", "link:" + message.messageMedia)
                    Log.d(
                        "MyTag",
                        "link substring:" + message.messageMedia.substring(message.messageMedia.length - 4)
                    )
                    val viewBinding =
                        ProfileImageViewBinding.inflate(LayoutInflater.from(myHolder1.binding.image.context))
                    Glide.with(myHolder1.binding.image.context).load(message.messageMedia)
                        .fitCenter().placeholder(R.drawable.ic_baseline_image_24)
                        .into(viewBinding.profileImage)

                    viewBinding.tvDownloadImg.setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Log.d("Permission", "Above 13")
                            saveImageToDownloadFolder(message.messageMedia)
                            UtilClass.mToast(it.context, "Downloading...")
                        } else {
                            Log.d("Permission", "below 13")
                            Dexter.withContext(it.context).withPermissions(
                                arrayListOf(
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            ).withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                                    if (p0?.areAllPermissionsGranted()!!) {
                                        saveImageToDownloadFolder(message.messageMedia)
                                        UtilClass.mToast(it.context, "Downloading...")
                                    } else {
                                        Log.d("Permission", "storage permission denied")
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                                ) {
                                    TODO("Not yet implemented")
                                }

                            }).check()
                        }
                    }


                    val dialog =
                        Dialog(myHolder1.binding.image.context, android.R.style.Theme_Material)
                    viewBinding.tvDownloadImg.setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Log.d("Permission", "Above 13")
                            saveImageToDownloadFolder(message.messageMedia)
                            UtilClass.mToast(it.context, "Downloading...")

                        } else {
                            Log.d("Permission", "below 13")
                            Dexter.withContext(it.context).withPermissions(
                                arrayListOf(
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            ).withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                                    if (p0?.areAllPermissionsGranted()!!) {
                                        saveImageToDownloadFolder(message.messageMedia)
                                        UtilClass.mToast(it.context, "Downloading...")

                                    } else {
                                        Log.d("Permission", "storage permission denied")
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                                ) {
                                    TODO("Not yet implemented")
                                }

                            }).check()
                        }
                    }
                    viewBinding.tvCancelImg.setOnClickListener {
                        dialog.dismiss()
                    }
                    // val dialog =  AlertDialog.Builder(myHolder1.binding.image.context,android.R.style.Theme_Material)
                    dialog.setContentView(viewBinding.root)

                    dialog.create()
                    dialog.show()


                    /* AlertDialog.Builder(myHolder1.binding.image.context)
                         .setView(viewBinding.root)
                         // The dialog is automatically dismissed when a dialog button is clicked.
                         .show()*/

                }

                /*
                                myHolder1.binding.delete.setOnClickListener {

                                    val cal = Calendar.getInstance().time
                                    val date = SimpleDateFormat("dd MMM yyyy hh:mm a").parse(message.created_at)

                                    val afterAdding15Mins = Date(date!!.time + 15 * 60 * 1000)
                                    if (cal <= afterAdding15Mins) {
                                        val dialogBuilder = AlertDialog.Builder(myHolder1.binding.delete.context)
                                        val alertDialog = dialogBuilder.create()

                                        dialogBuilder.setMessage("Sure to Delete Message")
                                        dialogBuilder.setPositiveButton("Ok") { _, _ ->
                                            chatActivity.deleteMessage(message.id.toString())
                                        }

                                        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                                            alertDialog.cancel()
                                        }
                                        dialogBuilder.show()
                                    } else {
                                        Toast.makeText(
                                            myHolder1.binding.username.context,
                                            "Cannot delete messages older than 15min",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                */

                Glide.with(myHolder1.binding.username.context).load(message.user.profile)
                    .centerCrop().placeholder(R.drawable.logo)
                    .into(myHolder1.binding.messageProfileImage)

                myHolder1.binding.image.setOnLongClickListener(object : OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder1.binding.imageCard.context,
                            null,
                            message!!,
                            false,
                            true
                        )
                        return true
                    }
                })

            }

            MY_PDF_LAYOUT -> {
                val myHolder1 = holder as PDFHolder
                Glide.with(holder.itemView.context).load(message.messageMedia).centerCrop()
                    .placeholder(R.drawable.ic_doc_file).into(myHolder1.binding.image)
                myHolder1.binding.myReplyTime.text = message.created_at
                myHolder1.binding.username.text = message.user!!.name
                //  val fileName=message.messageMedia.substring(48)
                myHolder1.binding.pdfFileName.text = message.messageMediaName
                Log.d("MyTag", "Message $message")

                myHolder1.binding.relativeLayout.setOnClickListener {
                    Log.d("MyTag", "Url ${message.messageMedia}")/*  myHolder1.binding.image.context.startActivity(
                          Intent(Intent.ACTION_VIEW, Uri.parse(message.messageMedia))
                      )*/

                    try {
                        val i = Intent(Intent.ACTION_VIEW)
                        // i.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=${message.messageMedia}"),"*/*")
                        i.setData(Uri.parse(message.messageMedia))
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        myHolder1.binding.image.context.startActivity(
                            Intent.createChooser(i, "select application")
                        )
                    } catch (e: Exception) {
                        Log.d("MyTag", "action view error ${e.message}")

                    }


                    /* if (message.messageMedia.contains(".ppt") || message.messageMedia.contains(".pptx") || message.messageMedia.contains(".pdf")) {
                         val dialog = Dialog(it.context,android.R.style.Theme_Material)

                         val viewBinding =
                             DialogWebviewLayoutBinding.inflate(LayoutInflater.from(it.context))

                         viewBinding.progressbar.visibility = View.VISIBLE
                         viewBinding.webView.apply {
                             settings.javaScriptEnabled = true
                             settings.builtInZoomControls = true
                             webViewClient = WebViewClient()
                             webChromeClient = WebChromeClient()
                             loadUrl("http://docs.google.com/viewer?url=${message.messageMedia}")
                         }

                         viewBinding.tvDownloadImg.setOnClickListener {
                             pdfDownloadFolder(message.messageMedia)
                         }
                         viewBinding.tvCancelImg.setOnClickListener {
                             dialog.dismiss()
                         }

                         dialog.setContentView(viewBinding.root)
                         dialog.create()
                         dialog.show()


                         *//*    val dialog = AlertDialog.Builder(it.context)
                        val viewBinding =
                            DialogWebviewLayoutBinding.inflate(LayoutInflater.from(it.context))
                        viewBinding.progressbar.visibility = View.VISIBLE

                        viewBinding.webView.apply {
                            settings.javaScriptEnabled = true
                            settings.builtInZoomControls = true
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            loadUrl("http://docs.google.com/viewer?url=${message.messageMedia}")
                        }
                        dialog.setNeutralButton("Cancel",
                            DialogInterface.OnClickListener { arg0, arg1 ->
                               // saveImageToDownloadFolder(message.messageMedia)
                                arg0.dismiss()
                            })
                        dialog.setNegativeButton("Download",
                            DialogInterface.OnClickListener { arg0, arg1 ->
                                arg0.dismiss()
                                pdfDownloadFolder(message.messageMedia)

                            })
                        dialog.setView(viewBinding.root)
                        dialog.show()*//*
                    } else {
                        myHolder1.binding.image.context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(message.messageMedia))
                        )

                    }*/

                }


                myHolder1.binding.relativeLayout.setOnLongClickListener(object :
                    OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder1.binding.relativeLayout.context,
                            null,
                            message!!,
                            false,
                            true
                        )
                        return true
                    }

                })


                /*
                                myHolder1.binding.delete.setOnClickListener {

                                    val cal = Calendar.getInstance().time
                                    val date = SimpleDateFormat("dd MMM yyyy hh:mm a").parse(message.created_at)

                                    val afterAdding15Mins = Date(date!!.time + 15 * 60 * 1000)
                                    if (cal <= afterAdding15Mins) {
                                        val dialogBuilder = AlertDialog.Builder(myHolder1.binding.delete.context)
                                        val alertDialog = dialogBuilder.create()

                                        dialogBuilder.setMessage("Sure to Delete Message")
                                        dialogBuilder.setPositiveButton("Ok") { _, _ ->
                                            chatActivity.deleteMessage(message.id.toString())
                                        }

                                        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                                            alertDialog.cancel()
                                        }
                                        dialogBuilder.show()
                                    } else {
                                        Toast.makeText(
                                            myHolder1.binding.username.context,
                                            "Cannot delete messages older than 15min",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }
                */

                myHolder1.binding.ivDownloadingBtn.setOnClickListener {
                    pdfDownloadFolder(message.messageMedia)
                    UtilClass.mToast(it.context, "Downloading...")
                }

                Glide.with(myHolder1.binding.username.context).load(message.user.profile)
                    .centerCrop().placeholder(R.drawable.logo)
                    .into(myHolder1.binding.messageProfileImage)

            }

            OTHER_PDF_LAYOUT -> {
                val myHolder1 = holder as OTHERPDFHolder

                Glide.with(holder.itemView.context).load(message.messageMedia).centerCrop()
                    .placeholder(R.drawable.ic_doc_file).into(myHolder1.binding.image)

                myHolder1.binding.myReplyTime.text = message.created_at
                myHolder1.binding.username.text =
                    String.format("%s %s", message.user!!.name, "(${message.userType})")
                // val fileName=message.messageMedia.substring(48)
                myHolder1.binding.pdfFileName.text = message.messageMediaName

                myHolder1.binding.ivDownloadingBtn.setOnClickListener {
                    pdfDownloadFolder(message.messageMedia)
                    UtilClass.mToast(it.context, "Downloading...")
                }

                myHolder1.binding.relativeLayout.setOnClickListener {
                    Log.d("MyTag", "Url ${message.messageMedia}")
                    try {
                        val i = Intent(Intent.ACTION_VIEW)
                        //i.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=${message.messageMedia}"),"*/*")
                        i.setData(Uri.parse(message.messageMedia))
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        myHolder1.binding.image.context.startActivity(
                            Intent.createChooser(i, "select application")
                        )
                    } catch (e: Exception) {
                        Log.d("MyTag", "action view error ${e.message}")

                    }


                    /* if (message.messageMedia.contains(".ppt") || message.messageMedia.contains(".pptx") || message.messageMedia.contains(".pdf")) {
                        // val dialog = AlertDialog.Builder(it.context)
                         val dialog = Dialog(it.context,android.R.style.Theme_Material)
                         val viewBinding =
                             DialogWebviewLayoutBinding.inflate(LayoutInflater.from(it.context))

                         viewBinding.progressbar.visibility = View.VISIBLE
                         viewBinding.webView.apply {
                             settings.javaScriptEnabled = true
                             settings.builtInZoomControls = true
                             webViewClient = WebViewClient()
                             webChromeClient = WebChromeClient()
                             loadUrl("http://docs.google.com/viewer?url=${message.messageMedia}")
                         }

                         viewBinding.tvDownloadImg.setOnClickListener {
                             pdfDownloadFolder(message.messageMedia)
                         }
                         viewBinding.tvCancelImg.setOnClickListener {
                             dialog.dismiss()
                         }

                         dialog.setContentView(viewBinding.root)
                         dialog.create()
                         dialog.show()
                     } else {
                         myHolder1.binding.image.context.startActivity(
                             Intent(Intent.ACTION_VIEW, Uri.parse(message.messageMedia))
                         )

                     }*/
                }

                Glide.with(myHolder1.binding.username.context).load(message.user.profile)
                    .centerCrop().placeholder(R.drawable.logo)
                    .into(myHolder1.binding.messageProfileImage)



                myHolder1.binding.relativeLayout.setOnLongClickListener(object :
                    OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder1.binding.relativeLayout.context,
                            null,
                            message!!,
                            false
                        )
                        return true
                    }

                })


            }

            IMAGE_LAYOUT_TWO -> {
                val myHolder1 = holder as ImageOtherSideHolder

                Glide.with(holder.itemView.context).load(message.messageMedia).centerCrop()
                    .placeholder(R.drawable.ic_baseline_image_24).into(myHolder1.binding.image)
                myHolder1.binding.myReplyTime.text = message.created_at
                myHolder1.binding.username.text =
                    String.format("%s %s", message.user!!.name, "(${message.userType})")

                myHolder1.binding.image.setOnClickListener {
                    Log.d("MyTag", "link:" + message.messageMedia)
                    Log.d(
                        "MyTag",
                        "link substring:" + message.messageMedia.substring(message.messageMedia.length - 5)
                    )
                    val viewBinding =
                        ProfileImageViewBinding.inflate(LayoutInflater.from(myHolder1.binding.image.context))

                    Glide.with(myHolder1.binding.image.context).load(message.messageMedia)
                        .fitCenter().placeholder(R.drawable.ic_baseline_image_24)
                        .into(viewBinding.profileImage)


                    val dialog =
                        Dialog(myHolder1.binding.image.context, android.R.style.Theme_Material)
                    viewBinding.tvDownloadImg.setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Log.d("Permission", "Above 13")
                            saveImageToDownloadFolder(message.messageMedia)
                            UtilClass.mToast(it.context, "Downloading...")

                        } else {
                            Log.d("Permission", "below 13")
                            Dexter.withContext(it.context).withPermissions(
                                arrayListOf(
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            ).withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                                    if (p0?.areAllPermissionsGranted()!!) {
                                        saveImageToDownloadFolder(message.messageMedia)
                                        UtilClass.mToast(it.context, "Downloading...")

                                    } else {
                                        Log.d("Permission", "storage permission denied")
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                                ) {
                                    TODO("Not yet implemented")
                                }

                            }).check()
                        }
                    }
                    viewBinding.tvCancelImg.setOnClickListener {
                        dialog.dismiss()
                    }
                    // val dialog =  AlertDialog.Builder(myHolder1.binding.image.context,android.R.style.Theme_Material)
                    dialog.setContentView(viewBinding.root)

                    dialog.create()
                    dialog.show()

                }

                Glide.with(myHolder1.binding.username.context).load(message.user.profile)
                    .centerCrop().placeholder(R.drawable.logo)
                    .into(myHolder1.binding.messageProfileImage)


                myHolder1.binding.image.setOnLongClickListener(object : OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        showBottomSheetDialogCopy(
                            myHolder1.binding.imageCard.context,
                            null,
                            message!!,
                            false
                        )
                        return true
                    }
                })


            }


            LEFT_CHAT_REPLY_LAYOUT -> {
                val leftReplyChatHolder = holder as LeftReplyChatHolder
                leftReplyChatHolder.binding.apply {

                   /* clLeftChat.setOnClickListener {


                    }*/


                    if (message.repliedMessage?.messageMediaName.isNullOrEmpty()) {
                        myRepliedLayout.visibility = View.VISIBLE
                        rlPdfReplyLayout.visibility = View.GONE
                        rlReplyImage.visibility = View.GONE
                    } else {
                        myRepliedLayout.visibility = View.GONE
                        if (message.repliedMessage?.messageMediaType?.contains(".image")!!) {
                            rlReplyImage.visibility = View.VISIBLE
                            rlPdfReplyLayout.visibility = View.GONE

                        } else {
                            rlPdfReplyLayout.visibility = View.GONE
                            rlReplyImage.visibility = View.VISIBLE

                        }

                    }

                    myRepliedName.text = message.repliedMessage?.user?.name
                    myRepliedMsg.text = message.repliedMessage?.message
                    myReply.text = message.message
                    myReplyTime.text = message.created_at


                    Glide.with(holder.itemView.context).load(message.repliedMessage?.messageMedia)
                        .centerCrop()
                        .placeholder(R.drawable.ic_doc_file).into(imagePdf)
                    leftReplyChatHolder.binding.pdfFileName.text =
                        message.repliedMessage?.messageMediaName

                    pdfFileName.text =
                        if (message.repliedMessage?.userType.equals(AppPref.getUserType(myReply.context))) {
                            "You "
                        } else {
                            message.repliedMessage?.user?.name
                        }

                    Glide.with(holder.itemView.context).load(message.repliedMessage?.messageMedia)
                        .centerCrop()
                        .placeholder(R.drawable.ic_doc_file).into(ivReplyImage)

                    //  val fileName=message.messageMedia.substring(48)


                    usernameProfile.text =
                        String.format("%s %s", message.user!!.name, "(${message.userType})")
                    Glide.with(userProfile.context).load(message.user?.profile)
                        .centerCrop().placeholder(R.drawable.logo)
                        .into(userProfile)




                    myReplyTopLayout.setOnLongClickListener(object : OnLongClickListener {
                        override fun onLongClick(p0: View?): Boolean {
                            showBottomSheetDialogCopy(
                                myReplyTopLayout.context,
                                myReply,
                                message!!,
                                true
                            )
                            return true
                        }

                    })
                }


            }

            else -> {
                //right reply Layout 2
                val rightReplyChatHolder = holder as RightReplyChatHolder
                rightReplyChatHolder.binding.apply {
//                    clRightChat.setOnClickListener {
//
//
//                    }



                    myRepliedName.text =
                        if (message.repliedMessage?.userType.equals(AppPref.getUserType(myReply.context))) {
                            "You "
                        } else {
                            message.repliedMessage?.user?.name
                        }




                    Log.d("Message", "reply msg $myReply")
                    if (message.repliedMessage?.messageMediaName.isNullOrEmpty()) {
                        myRepliedLayout.visibility = View.VISIBLE
                        rlPdfReplyLayout.visibility = View.GONE
                        ivReplyImage.visibility = View.GONE
                    } else {
                        myRepliedLayout.visibility = View.GONE
                        if (message.repliedMessage?.messageMediaType == "image") {
                            rlReplyImage.visibility = View.VISIBLE
                            rlPdfReplyLayout.visibility = View.GONE

                        } else {
                            rlPdfReplyLayout.visibility = View.VISIBLE
                            rlReplyImage.visibility = View.GONE
                        }

                    }

                    myRepliedMsg.text = message.repliedMessage?.message
                    myReply.text = message.message
                    myReplyTime.text = message.created_at

                    usernameProfile.text = message.user?.name
                    Glide.with(userProfileImage.context).load(message.user?.profile)
                        .centerCrop().placeholder(R.drawable.logo)
                        .into(userProfileImage)

                    myImageName.text =
                        if (message.repliedMessage?.userType.equals(AppPref.getUserType(myReply.context))) {
                            "You "
                        } else {
                            message.repliedMessage?.user?.name
                        }





                    Glide.with(holder.itemView.context).load(message.repliedMessage?.messageMedia)
                        .centerCrop()
                        .placeholder(R.drawable.ic_doc_file).into(imagePdf)

                    rightReplyChatHolder.binding.pdfFileName.text =
                        message.repliedMessage?.messageMediaName

                    myPdfName.text =
                        if (message.repliedMessage?.userType.equals(AppPref.getUserType(myReply.context))) {
                            "You "
                        } else {
                            message.repliedMessage?.user?.name
                        }


                    Glide.with(holder.itemView.context).load(message.repliedMessage?.messageMedia)
                        .centerCrop()
                        .placeholder(R.drawable.ic_doc_file).into(ivReplyImage)

                    myImageName.text =
                        if (message.repliedMessage?.userType.equals(AppPref.getUserType(myReply.context))) {
                            "You "
                        } else {
                            message.repliedMessage?.user?.name
                        }



                    myReplyLayout.setOnLongClickListener(object : OnLongClickListener {
                        override fun onLongClick(p0: View?): Boolean {
                            showBottomSheetDialogCopy(
                                myReplyLayout.context,
                                myReply,
                                message!!,
                                true,
                                true
                            )
                            return true
                        }

                    })


                }
            }
        }
    }

    private fun showBottomSheetDialogCopy(
        context: Context,
        tv: TextView? = null,
        message: MyMessage,
        isCopyText: Boolean = false,
        isDeleted: Boolean = false
    ) {
        val dialog = BottomSheetDialog(context, R.style.DialogStyle)
        val bottomSheet = DataBindingUtil.inflate<OnLongPressDialogLayoutBinding>(
            LayoutInflater.from(context), R.layout.on_long_press_dialog_layout, null, false
        )

        if (isDeleted) {
            bottomSheet.tvDeleteText.visibility = View.VISIBLE
        } else {
            bottomSheet.tvDeleteText.visibility = View.GONE
        }


        if (isCopyText) {
            bottomSheet.tvCopyText.visibility = View.VISIBLE
        } else {
            bottomSheet.tvCopyText.visibility = View.GONE
        }


        bottomSheet.tvDeleteText.setOnClickListener {
            val cal = Calendar.getInstance().time
            val date = SimpleDateFormat("dd MMM yyyy hh:mm a").parse(message.created_at)

            val afterAdding15Mins = Date(date!!.time + 15 * 60 * 1000)
            if (cal <= afterAdding15Mins) {
                val dialogBuilder = AlertDialog.Builder(it.context)
                val alertDialog = dialogBuilder.create()

                dialogBuilder.setMessage("Sure to Delete Message")
                dialogBuilder.setPositiveButton("Ok") { _, _ ->
                    chatActivity.deleteMessage(message.id.toString())
                }

                dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                    alertDialog.cancel()
                }
                dialogBuilder.show()
            } else {
                Toast.makeText(
                    it.context,
                    "Cannot delete messages older than 15min",
                    Toast.LENGTH_SHORT
                ).show()
            }

            dialog.dismiss()

        }

        bottomSheet.llReplyText.setOnClickListener {
            listener.onReplyMsg(message)
            dialog.dismiss()
        }
        bottomSheet.tvCopyText.setOnClickListener {
            UtilClass.copyText(context, tv?.text.toString())
            UtilClass.mToast(context, context.getString(R.string.text_copied))
            dialog.dismiss()
        }
        dialog.setContentView(bottomSheet.root)
        dialog.show()
    }


    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        // Log.d("MyTag", "messageList=: "+message.messageMediaType)

        val retValue: Int = if (AppPref.getUserType(context) == message.userType) {
            if (message.isReplied == "1") {
                RIGHT_CHAT_REPLY_LAYOUT
            } else {
                if (message.messageMediaType.contains("pdf")) MY_PDF_LAYOUT
                else if (message.messageMediaType.contains("image")) IMAGE_LAYOUT_ONE
                else LAYOUT_ONE
            }

        } else {
            if (message.isReplied == "1") {
                LEFT_CHAT_REPLY_LAYOUT
            } else {
                if (message.messageMediaType.contains("pdf")) OTHER_PDF_LAYOUT
                else if (message.messageMediaType.contains("image")) IMAGE_LAYOUT_TWO
                else LAYOUT_TWO
            }
        }
        return retValue
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return messageList.size
    }

    // Holds the views for adding it to image and text
    class MyHolder(customChatBinding: CustomChatBinding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }

    class MyHolder1(customChatBinding: CustomChat1Binding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }

    /*  class MyHolder1(customChatBinding: LeftReplyChatBinding) :
          RecyclerView.ViewHolder(customChatBinding.root) {
          val binding = customChatBinding
      }
  */
    class ImageMyHolder(customChatBinding: CustomChatImageBinding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }

    class ImageOtherSideHolder(customChatBinding: CustomChatImageOtherBinding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }

    class PDFHolder(customChatBinding: CustomChatPdfBinding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }

    class OTHERPDFHolder(customChatBinding: CustomChatPdfOtherBinding) :
        RecyclerView.ViewHolder(customChatBinding.root) {
        val binding = customChatBinding
    }


    class LeftReplyChatHolder(leftReplyChatBinding: LeftReplyChatBinding) :
        RecyclerView.ViewHolder(leftReplyChatBinding.root) {
        val binding = leftReplyChatBinding
    }

    class RightReplyChatHolder(rightReplyChatBinding: RightReplyChatBinding) :
        RecyclerView.ViewHolder(rightReplyChatBinding.root) {
        val binding = rightReplyChatBinding
    }

    fun openTapOnMessageDialog(context: Context) {

    }

    private fun saveImageToDownloadFolder(imageFile: String) {
        try {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val uri1 = Uri.parse(imageFile)
            var request = DownloadManager.Request(uri1)
            val storagePath = Environment.DIRECTORY_DOWNLOADS

            request.setDestinationInExternalPublicDir(
                storagePath, "kiya_learning.png"
            )
            request.setTitle(context.getString(R.string.title_file_download))
            request.setDescription(context.getString(R.string.downloading))
            downloadManager.enqueue(request)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pdfDownloadFolder(pdfPath: String) {
        try {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val file = File(pdfPath)
            // val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val uri1 = Uri.parse(pdfPath)
            var request = DownloadManager.Request(uri1)
            val storagePath = Environment.DIRECTORY_DOWNLOADS

            request.setDestinationInExternalPublicDir(
                storagePath, "${file.name}"
            )
            request.setTitle(context.getString(R.string.title_file_download))
            request.setDescription(context.getString(R.string.downloading))
            downloadManager.enqueue(request)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
