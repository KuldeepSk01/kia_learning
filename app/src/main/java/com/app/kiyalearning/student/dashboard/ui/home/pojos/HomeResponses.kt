package com.app.kiyalearning.student.dashboard.ui.home.pojos


import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.Attendance
import com.google.gson.annotations.SerializedName


data class DashBoardDataResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: DashBoardData
)


data class DashBoardData(

    val id: String,

    @SerializedName("classes_offered")
    val classesOffered: String,

    @SerializedName("classes_taken")
    val classesTaken: String,

    @SerializedName("cancel_classes")
    val cancelClasses: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("user_unique_id")
    val userUniqueId: String,

    val email: String,

    val location: String,

    val profile: String,

    val sibilings:List<StudentSibilingResponse>
)


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


data class StudentClassesListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: List<StudentClass>
)


data class StudentClass(
    val id: String,


    @SerializedName("teacher_name")
    val name: String,
    val location: String,
    val subject: String,

    val feedback: String,

    @SerializedName("conference_date")
    val date: String,

    @SerializedName("conference_time")
    val time: String,

    @SerializedName("conference_url")
    val classUrl: String,

    val status: String,

    @SerializedName("is_feedback")
    val isFeedBack: Long,

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
    val classHomeWork: String?=null,

    @SerializedName("is_teacher_join_class")
    val isTeacherJoinClass: Int,

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


data class WalletListResponse(
    val status: Long,
    val success: Boolean,
    val message: String,
    val expense: String,
    val paid: String,
    val due: String,
    val data: List<MyWallet>
)

data class AcceptRejectResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)

data class AddFeedbackResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)

data class StudentAttendanceResponse(
    val status: Long,
    val success: Boolean,
    val message: String,

    val data: List<Attendance>
)

data class Attendance(
    @SerializedName("is_checkin")
    val isCheckIn: String?,

    @SerializedName("checkin_time")
    val checkInTime: String? = null,

    @SerializedName("checkin_lat")
    val checkInLat: String? = null,

    @SerializedName("checkin_long")
    val checkInLong: String? = null,

    @SerializedName("checkin_address")
    val checkInAddress: String? = null,

    @SerializedName("checkout_time")
    val checkoutTime: String? = null,

    @SerializedName("checkout_lat")
    val checkoutLat: String? = null,

    @SerializedName("checkout_long")
    val checkoutLong: String? = null,

    @SerializedName("checkout_address")
    val checkoutAddress: String? = null
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

data class LibraryDataResponse(
    val status: Long,
    val present: Long,
    val absent: Long,
    val data: List<LibraryData>
)

data class LibraryData(
    val id: String,
    val title: String,
    val country: String,
    val curriculum: String,

    val pdf: String,

    @SerializedName("school_name")
    val schoolName: String,

    val garde: String,

    val subject: String,

    val book_type: String,

    val book_size: String,

    val category: String
)

data class FeesListResponse(
    val status: Long,
    val message: String,
    val data: List<Fees>
)

data class TeacherSlotsResponse(
    val status: Long,
    val message: String,
    val data: List<String>
)


data class Fees(
    val country: String,
    val curriculum: String,
    val school: String,
    val grade: String,
    val subject: String,
    val perHrFees: String
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

    val name: String? = null,

    @SerializedName("first_name")
    val firstName: Any? = null,

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

    @SerializedName("teaching_preference")
    val teachingPreference: TeachingPreference,

    )


data class CoordinatorProfileDetailResponse(
    val status: Long,
    val success: Boolean,
    val data: CoordinatorProfile,
    val message: String
)

data class CoordinatorProfile(
    val id: String,

    val name: String? = null,

    @SerializedName("first_name")
    val firstName: Any? = null,

    @SerializedName("last_name")
    val lastName: Any? = null,

    val email: String,

    @SerializedName("mobile_no")
    val mobileNo: String,

    val address: String,

    @SerializedName("joining_date")
    val joiningDate: String,

    @SerializedName("user_unique_id")
    val userUniqueId: String,

    val profile: Any? = null

)


data class AssessmentResponse(
    val status: Long,
    val success: Boolean,
    val data: List<Assessment>,
    val message: String
)

data class Assessment(
    val id: String,

    @SerializedName("test_name")
    val testName: String,

    @SerializedName("coordinator_name")
    val coordinatorName: String,

    @SerializedName("assessment_date")
    val assessmentDate: String,

    val subject: String,

    @SerializedName("marks")
    val totalMarks: String,

    @SerializedName("scored")
    val obtainedMarks: String,

    val grade: String
)


data class TestSeriesResponse(
    val status: Long,
    val success: Boolean,
    val data: List<TestSeries>,
    val message: String
)

data class StudentJoinClassResponse(
    val status: Long,
    val success: Boolean,
    val data: List<Any>,
    val message: String
)


data class TestSeries(

    val id: String,

    @SerializedName("test_name")
    val testName: String,

    val date: String,

    @SerializedName("submission_date")
    val submissionDate: String,

    @SerializedName("group_id")
    val groupId: String,

    @SerializedName("group_name")
    val groupName: String,

    @SerializedName("test_type")
    val testType: String,

    @SerializedName("teacher_file")
    val teacherFile: String,

    @SerializedName("student_file")
    val studentFile: String,

    @SerializedName("marks")
    val marks: String,

    @SerializedName("scored")
    val scored: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("created_at")
    val createdAt: String
)

data class GroupNameResponse(
    val status: Long,
    val success: Boolean,
    val data: List<Group>,
    val message: String
)

data class Group(
    val id: Long,

    @SerializedName("group_name")
    val groupName: String
)


data class TeachingPreference(
    val country: String,

    val curriculum: String,

    @SerializedName("school_name")
    val schoolName: String,

    val grades: String,

    val subjects: String
)


data class UpdateProfileResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)


