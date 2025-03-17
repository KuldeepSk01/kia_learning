package com.app.kiyalearning.chat.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ChatListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: List<Group>
)

data class NotificationGroup(
    @SerializedName("id")
    val id: Long,
    @SerializedName("group_name")
    val name: String,
    @SerializedName("sort_by_teacher")
    val sortByTeacher: Int,
    @SerializedName("sort_by_student")
    val sortByStudent: Int,
    @SerializedName("sort_by_coordinator")
    val sortByCoordinator: Int,
    @SerializedName("sort_by_sub_admin")
    val sortBySubAdmin: Int,
    @SerializedName("group_icon")
    var groupIcon: String?=null
):Serializable

data class Group(
    val id: Long,

    @SerializedName("group_name")
    val name: String,

    @SerializedName("group_desc")
    val groupDesc: String,

    @SerializedName("group_icon")
    val groupIcon: String,

    @SerializedName("student_unread_messages")
    val studentUnread: String,

    @SerializedName("teacher_unread_messages")
    val teacherUnread: String,

    @SerializedName("coordinator_unread_messages")
    val coordinatorUnread: String,

    @SerializedName("sort_by_teacher")
    val sortByTeacher: Int,

    @SerializedName("sort_by_student")
    val sortByStudent: Int,

    @SerializedName("sort_by_coordinator")
    val sortByCoordinator: Int,

    @SerializedName("sort_by_sub_admin")
    val sortBySubAdmin: Int,

    ) : Serializable


data class User(
    val id: Long,
    val name: String,
    val photo: String,
    val email: String,
    val phone: String,
    val deviceType: String,
    val notificationToken: String
)

data class GroupChannel(
    var id: String?=null,
    var name: String?=null,
    var photo: String?=null,
):Serializable

/*data class Chat(
    val name:String,
    val time:String,
    val type:String,
    val userId:String,
    val imgUrl:String)*/


data class RTMTokenResponse(
    val status: String,
    val message: String,
    val data: AgoraToken
)

data class AgoraToken(
    val token: String,
    val uid: String,
    val channelName: String
)

data class SaveMessage(
    val customer_id: Long,
    val mentor_id: Long,
    val message_from: String,
    val message: String
)

data class SaveMessageResponse(
    @SerializedName("status")
    val status: Long,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: MyMessage,
    @SerializedName("userAction")
    val userAction: Int
)

data class RetrieveChatResponse(
    @SerializedName("status")
    val status: Long,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,

    @SerializedName("agora_token")
    val agoraToken: String,

    @SerializedName("channel_name")
    val channelName: String,

    @SerializedName("coordinator_agora_id")
    val coordinatorAgoraId: String,

    @SerializedName("teacher_agora_id")
    val teacherAgoraId: String,

    @SerializedName("student_agora_id")
    val studentAgoraId: String,


    @SerializedName("coordinator_profile")
    val coordinatorProfile: String,

    @SerializedName("teacher_profile")
    val teacherProfile: String,

    @SerializedName("student_profile")
    val studentProfile: String,


    @SerializedName("coordinator_name")
    val coordinatorName: String,

    @SerializedName("teacher_name")
    val teacherName: String,

    @SerializedName("student_name")
    val studentName: String,

    @SerializedName("data")
    val data: List<MyMessage>,

    @SerializedName("next_page")
    val nextPage: String
)

data class DeleteChatResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)

data class MyMessage(
    @SerializedName("id")
    val id: Long,

    @SerializedName("user_type")
    val userType: String,

    @SerializedName("message_media")
    val messageMedia: String,

    @SerializedName("message_media_type")
    val messageMediaType: String,

    @SerializedName("message_media_name")
    val messageMediaName: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    val user: MessageUser? = null,

    @SerializedName("created_at")
    val created_at: String,


    @SerializedName("is_replied")
    val isReplied: String?=null,
    @SerializedName("is_self_replied")
    val isSelfReplied: String?=null,

    @SerializedName("replied_message_id")
    val repliedMessageId: String?=null,

    @SerializedName("replied_message")
    val repliedMessage : MyMessage?=null
)

data class MyMessageHead(
    val messages: List<MyMessage>,
    val links: Links
)

data class MessageUser(
    val name: String,
    val profile: String
)


data class AdditionalMedia(
    val id: Long,
    val title: String,
    val tip_title: String,
    val slug: String,
    val uploaded_by: String,
    val description: String
)

data class Links(
    @SerializedName("has-pages")
    val hasPages: Boolean,
    val next: String,
    val items: Long
)

data class FCMRawData(
    val data: RawData,
    val to: String,
    val priority: String,
    val time_to_live: Long?,
    val notification: IosNotificationData?
)

data class IosNotificationData(
    val title: String,
    val body: String,
    val sound: String,
    val badge: String,
    val notificationType: String,

    @SerializedName("content_slug")
    val ContentSlug: String
)

data class RawData(
    val username: String,

    @SerializedName("user_image")
    val userImage: String,

    @SerializedName("call_type")
    val callType: String,

    @SerializedName("call_token")
    val callToken: String,

    @SerializedName("channel_name")
    val channelName: String,

    val data_type: String,

    val care_guide_device_token: String,

    val appointment_id: String,

    val is_instant_call: String
)

data class FCMMissedCall(
    val notification: Notification,
    val to: String,

    val data: RawData
)

data class Notification(
    val title: String,
    val body: String,
    val priority: String,
    val sound: String
)

