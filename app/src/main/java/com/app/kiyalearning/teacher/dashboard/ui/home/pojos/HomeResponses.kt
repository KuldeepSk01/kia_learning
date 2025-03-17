package com.app.kiyalearning.teacher.dashboard.ui.home.pojos


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class NotificationsResponse(
    val data: List<MyNotification>,
    val message: String,
    val status: Long,
    val success: Boolean,
    @SerializedName("unread_notification")
    val unreadNotification: Boolean
) : Serializable

data class CountryListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: List<MyCountry>
)

data class MyCountry(
    val id: String,
    val location: String
)

data class DashBoardDataResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: DashBoardData
)

data class LogOutResponse(
    @SerializedName("status")
    val status: Long,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String

)

data class DashBoardData(

    val id: String,

    @SerializedName("classes_offered")
    val classesOffered: String,

    @SerializedName("classes_taken")
    val classesTaken: String,

    @SerializedName("cancel_classes")
    val cancelClasses: String,

    val earning: String,

    val paid: String,

    val due: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("user_unique_id")
    val userUniqueId: String,

    val email: String,

    val location: String,

    val profile: String
)


data class MyNotification(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("created_at")
    val created_at: String
) : Serializable

data class CardData(
    val title: String,
    val content: String,
    val color: Int
)

data class MyClass(
    val id: String,
    val name: String,
    val location: String,
    val subject: String,


    @SerializedName("conference_date")
    val date: String,

    @SerializedName("conference_time")
    val time: String,

    @SerializedName("conference_url")
    val classUrl: String,

    val status: String,
    val profile: String,

    @SerializedName("rate")
    val fees: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("is_break")
    val hasBreak: Long,

    @SerializedName("reschedule_conference_date")
    val rescheduleConferenceDate: String,

    @SerializedName("reschedule_conference_time")
    val rescheduleConferenceTime: String,

    @SerializedName("is_reschedule")
    val isReschedule: Long,

    @SerializedName("class_timestamp")
    val classTimestamp: Long,

    @SerializedName("rejected_by")
    val rejectedBy: String,
    @SerializedName("rejected_reason")
    val rejectedReason: String,

    @SerializedName("class_home_work")
    val classHomeWork: String? = null,

    @SerializedName("is_student_join_class")
    val isStudentJoinClass: Int,

    @SerializedName("teacher_class_join_with")
    val teacherClassJoinWith: String,

    @SerializedName("student_class_join_with")
    val studentClassJoinWith: String,

    @SerializedName("join_time_of_teacher")
    val joinTimeOfTeacher: String,

    @SerializedName("join_time_of_student")
    val joinTimeOfStudent: String
)

data class MyWallet(
    val id: String,
    val name: String,
    val location: String,
    val subject: String,


    @SerializedName("conference_date")
    val date: String,

    @SerializedName("conference_time")
    val time: String,

    @SerializedName("conference_url")
    val classUrl: String,

    val status: String,
    val profile: String,

    @SerializedName("rate")
    val fees: String,

    @SerializedName("created_at")
    val createdAt: String
)

data class ClassesListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: List<MyClass>
)

data class WalletListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val expense: String,
    val paid: String,
    val due: String,
    val data: List<MyWallet>,

    @SerializedName("month_names")
    val monthNames: List<MonthsName>
)


data class MonthsName(
    @SerializedName("is_selected")
    var isSelected: Boolean,

    @SerializedName("month_name")
    val name: String
)

data class AcceptRejectResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)

data class TeacherAttendanceResponse(
    val status: Long,
    val success: Boolean,
    val message: String,

    val data: List<Attendance>
)

data class Attendance(

    val name: String,

    @SerializedName("check_in")
    val checkIn: String,

    @SerializedName("check_out")
    val checkOut: String,

    @SerializedName("check_in_date")
    val checkInDate: String,

    @SerializedName("check_out_date")
    val checkOutDate: String,

    @SerializedName("attendance_date")
    val attendanceDate: String


)

data class AbsentPresent(
    val name: String,
    val checkIn: String,
    val checkInDate: String,
    val checkInAddress: String,
    val checkOut: String,
    val checkOutDate: String,
    val checkOutAddress: String
)

data class AbsentPresentResponse(
    val status: Long,
    val present: Long,
    val absent: Long,
    val absentPresent: List<AbsentPresent>
)


data class FeesListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: List<Fees>
)

data class TeacherSlotsResponse(
    val status: Long,
    val message: String,
    val data: List<String>
)


data class Fees(
    val id: String,
    val country: String,
    val curriculum: String,

    @SerializedName("school_name")
    val school: String,
    val garde: String,
    val subject: String,
    val rate: String
)

data class ProfileDetailResponse(
    val status: Long,
    val success: Boolean,
    val data: Profile,
    val message: String
)

data class Profile(
    val id: Long,

    @SerializedName("Empid")
    val empid: Any? = null,

    @SerializedName("first_name")
    val firstName: Any? = null,

    val name: String,

    @SerializedName("last_name")
    val lastName: Any? = null,

    val email: String,

    @SerializedName("mobile_no")
    val mobileNo: String,

    val gender: Any? = null,

    @SerializedName("per_hour_fee")
    val perHrFees: String? = null,

    @SerializedName("location")
    val address: String,

    val dob: String,


    val profile: Any? = null,

    @SerializedName("aadhaar_card")
    val aadharCard: String,

    @SerializedName("pan_card")
    val panCard: String,
)

data class UpdateProfileResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)


