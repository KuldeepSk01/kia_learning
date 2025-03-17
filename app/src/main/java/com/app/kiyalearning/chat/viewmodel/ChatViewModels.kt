package com.app.kiyalearning.chat.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kiyalearning.R
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.chat.pojo.*
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.NotificationsResponse
import com.app.kiyalearning.util.AppPref
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModels : ViewModel() {

    var chatListResponse = MutableLiveData<ChatListResponse>()
    var validationError = MutableLiveData<String>()
    var rtmTokenResponse = MutableLiveData<RTMTokenResponse>()
    var saveMessageResponse = MutableLiveData<SaveMessageResponse>()
    var retrieveChatResponse = MutableLiveData<RetrieveChatResponse>()
    var deleteChatResponse = MutableLiveData<DeleteChatResponse>()
    var moreChatResponse = MutableLiveData<RetrieveChatResponse>()
 //   var fcmMessageResponse = MutableLiveData<FCMCallingResponse>()
    var saveImageMessageResponse = MutableLiveData<SaveMessageResponse>()
    var saveArticleVideosMessageResponse = MutableLiveData<SaveMessageResponse>()
    var typeText = MutableLiveData<String>()

    var notificationsResponse = MutableLiveData<NotificationsResponse>()

    var pinUnpinGroupResponse = MutableLiveData<SaveMessageResponse>()

    //  lateinit var selectedImageUrl: String

    fun getNotificationsList(context: Context)
    {
        val map=HashMap<String,Any>()
        //  map["consumption_id"] = consumptionId

        val call: Call<NotificationsResponse> =  RestManager.getInstance().getCoordinatorNotifications(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context))
        call.enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                if (response.body() != null) {
                    notificationsResponse.value = response.body()
                }else
                {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

      fun getChatGroups(context: Context,search:String)
      {
          val map=HashMap<String,Any>()
          // map["status"] = id.toString()

          map["user_type"] = AppPref.getUserType(context)
          map["search_value"] = search

          val call: Call<ChatListResponse> = RestManager.getInstance().getChatList(
              AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context)
              ,map)

          call.enqueue(object : Callback<ChatListResponse> {
              override fun onResponse(call: Call<ChatListResponse>, response: Response<ChatListResponse>) {
                  if (response.body() != null) {
                      chatListResponse.value = response.body()
                  } else{
                      try {
                          val jObjError = JSONObject(response.errorBody()!!.string())
                          validationError.value =jObjError.getString("message")
                      } catch (e: Exception) {
                          validationError.value = e.message
                      }
                  }
              }
              override fun onFailure(call: Call<ChatListResponse>, t: Throwable) {
                  validationError.value = context.getString(R.string.server_error)
              }
          })
      }

      fun retrieveChat(context: Context,groupId : String,isUrl:String?=null)
      {
          if (isUrl!=null){
              val call : Call<RetrieveChatResponse> = RestManager.getInstance().retrieveChatHistoryWithUrl(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
                  isUrl)
              call.enqueue(object : Callback<RetrieveChatResponse> {
                  override fun onResponse(call: Call<RetrieveChatResponse>, response: Response<RetrieveChatResponse>) {
                      // Log.d("MyTag", "response raw="+response.raw())
                      // Log.d("MyTag", "response body="+response.body())
                      if (response.body() != null) {
                          retrieveChatResponse.value = response.body()
                      } else{
                          try {
                              val jObjError = JSONObject(response.errorBody()!!.string())
                              validationError.value =jObjError.getString("message")
                          } catch (e: Exception) {
                              validationError.value = e.message
                          }
                      }
                  }

                  override fun onFailure(call: Call<RetrieveChatResponse>, t: Throwable) {
                      validationError.value = context.getString(R.string.server_error)
                  }
              })

          }else{
              val call : Call<RetrieveChatResponse> = RestManager.getInstance().retrieveChatHistory(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
                  groupId)
              call.enqueue(object : Callback<RetrieveChatResponse> {
                  override fun onResponse(call: Call<RetrieveChatResponse>, response: Response<RetrieveChatResponse>) {
                      // Log.d("MyTag", "response raw="+response.raw())
                      // Log.d("MyTag", "response body="+response.body())
                      if (response.body() != null) {
                          retrieveChatResponse.value = response.body()
                      } else{
                          try {
                              val jObjError = JSONObject(response.errorBody()!!.string())
                              validationError.value =jObjError.getString("message")
                          } catch (e: Exception) {
                              validationError.value = e.message
                          }
                      }
                  }

                  override fun onFailure(call: Call<RetrieveChatResponse>, t: Throwable) {
                      validationError.value = context.getString(R.string.server_error)
                  }
              })

          }


       /* val call: Call<RetrieveChatResponse> = RestManager.getInstance().retrieveChatHistory(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
            groupId)*/

    }

     fun markReadChat(context: Context,groupId : String)
    {
        val map=HashMap<String,Any>()
        map["user_type"] = AppPref.getUserType(context)
        map["group_id"] = groupId

        val call: Call<RetrieveChatResponse> = RestManager.getInstance().markReadChat(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
            map)
        call.enqueue(object : Callback<RetrieveChatResponse> {
            override fun onResponse(call: Call<RetrieveChatResponse>, response: Response<RetrieveChatResponse>) {
                // Log.d("MyTag", "response raw="+response.raw())
                // Log.d("MyTag", "response body="+response.body())
                if (response.body() != null) {
                  //  retrieveChatResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        //validationError.value = e.message
                        validationError.value = context.getString(R.string.server_error)

                    }
                }
            }

            override fun onFailure(call: Call<RetrieveChatResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun deleteChat(context: Context,mssgId : String)
    {
        val map=HashMap<String,Any>()
        map["message_id"] = mssgId

        val call: Call<DeleteChatResponse> = RestManager.getInstance().deleteChat(AppPref.getTokenType(context)+" "+AppPref.getUserToken(context),
            map)
        call.enqueue(object : Callback<DeleteChatResponse> {
            override fun onResponse(call: Call<DeleteChatResponse>, response: Response<DeleteChatResponse>) {
                // Log.d("MyTag", "response raw="+response.raw())
                // Log.d("MyTag", "response body="+response.body())
                if (response.body() != null) {
                      deleteChatResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<DeleteChatResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


  /*    fun getRtmToken(context: Context,mentorId : String,customerId : String)
      {
          val call: Call<RTMTokenResponse> = RestManager.getInstance().getRtmToken(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context)
              ,mentorId,customerId,AppPref.getUserId(context).toString())

          call.enqueue(object : Callback<RTMTokenResponse> {
              override fun onResponse(call: Call<RTMTokenResponse>, response: Response<RTMTokenResponse>) {
                  if (response.body() != null) {
                      rtmTokenResponse.value = response.body()
                  } else{
                      try {
                          val jObjError = JSONObject(response.errorBody()!!.string())
                          validationError.value =jObjError.getString("message")
                      } catch (e: Exception) {
                          validationError.value = e.message
                      }
                  }
              }
              override fun onFailure(call: Call<RTMTokenResponse>, t: Throwable) {
                  validationError.value = context.getString(R.string.server_error)
              }
          })
      }

      */



    fun saveMessage(context: Context,groupId : String,message:String,isReplied:Int?,repliedMessageId:Long?,isSelfReplied:Int?,repliedUserType:String?)
    {

        val map=HashMap<String,Any>()
        // map["status"] = id.toString()
        map["user_type"] = AppPref.getUserType(context)
        map["group_id"] = groupId
        map["user_id"] = AppPref.getUserId(context)
        map["message"] = message

        map["is_replied"] = isReplied!!
        map["replied_message_id"] = repliedMessageId!!
        map["is_self_replied"] = isSelfReplied!!
        map["replied_user_type"] = repliedUserType!!

        Log.d("MyTag","User replied data ${map.toString()}")
    //    map["message_media"] = ""
     //   map["message_media_type"] = ""



        val call: Call<SaveMessageResponse> = RestManager.getInstance().saveMessage(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context)
            ,map)

        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.body() != null) {
                    saveMessageResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<SaveMessageResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }

    fun saveImageMessageApi(context: Context,groupId : String,message:String,messageMediaType:String,image : MultipartBody.Part?)
    {
        val groupId1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),groupId)
        val userId: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),AppPref.getUserId(context))
        val userType: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),AppPref.getUserType(context))

        val messageMediaType1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),messageMediaType)
        val message1: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),message)


        val call: Call<SaveMessageResponse> =  RestManager.getInstance().saveImageMessage(
            image,
            AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context)
            ,  groupId1,  userId,   userType,messageMediaType1,message1)

        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.isSuccessful) {
                    saveImageMessageResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val msg = jObjError.getString("message")
                        if (msg.equals("Unauthenticated.")) {
                            AppPref.userLogout(context)
                            val intent = Intent(context, IntroActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            validationError.value = msg
                        }
                    } catch (e: Exception) {
                        validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<SaveMessageResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
                Log.d("TAG","Error ${t.message}")
            }
        })
    }



    fun pinUnpinGroup(context: Context,groupId : String)
    {
        val map=HashMap<String,Any>()
        // map["status"] = id.toString()
        map["user_type"] = AppPref.getUserType(context)
        map["group_id"] = groupId
//        map["user_id"] = AppPref.getUserId(context)
//        map["message"] = message
        //    map["message_media"] = ""
        //   map["message_media_type"] = ""


        val call: Call<SaveMessageResponse> = RestManager.getInstance().pinUnpinGroup(AppPref.getTokenType(context)+" "+ AppPref.getUserToken(context)
            ,map)

        call.enqueue(object : Callback<SaveMessageResponse> {
            override fun onResponse(call: Call<SaveMessageResponse>, response: Response<SaveMessageResponse>) {
                if (response.body() != null) {
                    pinUnpinGroupResponse.value = response.body()
                } else{
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        validationError.value =jObjError.getString("message")
                    } catch (e: Exception) {
                        validationError.value = context.getString(R.string.server_error)

                      //  validationError.value = e.message
                    }
                }
            }
            override fun onFailure(call: Call<SaveMessageResponse>, t: Throwable) {
                validationError.value = context.getString(R.string.server_error)
            }
        })
    }


}