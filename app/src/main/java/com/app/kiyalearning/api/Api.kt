package com.app.kiyalearning.api

import com.app.kiyalearning.api.WebConstant.Companion.ABSENT_PRESENT
import com.app.kiyalearning.api.WebConstant.Companion.ACCEPT_REJECT_CLASS
import com.app.kiyalearning.api.WebConstant.Companion.ADD_FEEDBACK
import com.app.kiyalearning.api.WebConstant.Companion.ADD_TECHNICAL_REPORT_STATUS
import com.app.kiyalearning.api.WebConstant.Companion.CHAT_LIST
import com.app.kiyalearning.api.WebConstant.Companion.CLASSES_LIST
import com.app.kiyalearning.api.WebConstant.Companion.COORDINATOR_LOG_OUT
import com.app.kiyalearning.api.WebConstant.Companion.COORDINATOR_NOTIFICATION_LIST
import com.app.kiyalearning.api.WebConstant.Companion.COORDINATOR_PROFILE_DETAILS
import com.app.kiyalearning.api.WebConstant.Companion.COUNTRY_LIST
import com.app.kiyalearning.api.WebConstant.Companion.DELETE_MESSAGE
import com.app.kiyalearning.api.WebConstant.Companion.FEES_LIST
import com.app.kiyalearning.api.WebConstant.Companion.FORGOT_PASSWORD
import com.app.kiyalearning.api.WebConstant.Companion.LOGIN
import com.app.kiyalearning.api.WebConstant.Companion.LOGIN_WITH_SIBLING
import com.app.kiyalearning.api.WebConstant.Companion.MARK_READ_MESSAGES
import com.app.kiyalearning.api.WebConstant.Companion.MARK_READ_NOTIFICATIONS
import com.app.kiyalearning.api.WebConstant.Companion.MONTH_ATTENDANCE
import com.app.kiyalearning.api.WebConstant.Companion.RESEND_OTP
import com.app.kiyalearning.api.WebConstant.Companion.RETRIEVE_MESSAGES
import com.app.kiyalearning.api.WebConstant.Companion.SAVE_MESSAGE
import com.app.kiyalearning.api.WebConstant.Companion.SAVE_STUDENT_TEST_FILE
import com.app.kiyalearning.api.WebConstant.Companion.SIGN_UP
import com.app.kiyalearning.api.WebConstant.Companion.SORT_CHAT_GROUP_BY_USER
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_ASSESSMENT
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_CLASSES_LIST
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_DASHBOARD_DATA
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_JOIN_CLASS_STATUS
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_LIBRARY_LIST
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_LOG_OUT
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_NOTIFICATION_LIST
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_PROFILE_DETAILS
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_TEST_SERIES
import com.app.kiyalearning.api.WebConstant.Companion.STUDENT_WALLET_LIST
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_ATTENDANCE
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_DASHBOARD_DATA
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_LIBRARY_LIST
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_LOG_OUT
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_NOTIFICATION_LIST
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_PROFILE_DETAILS
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_SLOTS
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_TEST_GROUP_NAMES
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_TEST_SERIES
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_TEST_SERIES_UPLOAD
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_UPLOAD_HW
import com.app.kiyalearning.api.WebConstant.Companion.TEACHER_WALLET_LIST
import com.app.kiyalearning.api.WebConstant.Companion.UPDATE_COORDINATOR_PROFILE
import com.app.kiyalearning.api.WebConstant.Companion.UPDATE_STUDENT_PROFILE
import com.app.kiyalearning.api.WebConstant.Companion.UPDATE_TEACHER_PROFILE
import com.app.kiyalearning.api.WebConstant.Companion.UPDATE_TEST_SCORE
import com.app.kiyalearning.auth.pojos.ForgetPasswordResponse
import com.app.kiyalearning.auth.pojos.HomeWorkFileResponse
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.auth.pojos.ReSendOTPResponse
import com.app.kiyalearning.auth.pojos.SignUpResponse
import com.app.kiyalearning.chat.pojo.ChatListResponse
import com.app.kiyalearning.chat.pojo.DeleteChatResponse
import com.app.kiyalearning.chat.pojo.RetrieveChatResponse
import com.app.kiyalearning.chat.pojo.SaveMessageResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.*
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AbsentPresentResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.AcceptRejectResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.DashBoardDataResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.ProfileDetailResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.UpdateProfileResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.WalletListResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.*
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.TeacherSlotsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST(LOGIN)
    fun login(@FieldMap map: HashMap<String, Any>): Call<LoginResponse>

    @FormUrlEncoded
    @POST(LOGIN_WITH_SIBLING)
    fun loginWithSibling(@FieldMap map: HashMap<String, Any>): Call<LoginResponse>


    @FormUrlEncoded
    @POST(RESEND_OTP)
    fun reSendOTP(@FieldMap map: HashMap<String, Any>): Call<ReSendOTPResponse>


    @FormUrlEncoded
    @POST(FORGOT_PASSWORD)
    fun forgetPassword(@FieldMap map: HashMap<String, Any>): Call<ForgetPasswordResponse>

    @FormUrlEncoded
    @POST(LOGIN)
    fun loginWithOtp(@FieldMap map: HashMap<String, Any>): Call<LoginResponse>

    @FormUrlEncoded
    @POST(SIGN_UP)
    fun signUpVerify(@FieldMap map: HashMap<String, Any>): Call<LoginResponse>


    @FormUrlEncoded
    @POST(SIGN_UP)
    fun signUp(@FieldMap map: HashMap<String, Any>): Call<SignUpResponse>

    @Headers("Accept: application/json")
    @POST(TEACHER_DASHBOARD_DATA)
    fun getTeacherDashBoardData(@Header("Authorization") token: String): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.DashBoardDataResponse>


    @Headers("Accept: application/json")
    @POST(STUDENT_DASHBOARD_DATA)
    fun getStudentDashBoardData(@Header("Authorization") token: String): Call<DashBoardDataResponse>




/*    @Headers("Accept: application/json")
    @GET(TEACHER_LOG_OUT)
    fun logOut(
        @Header("Authorization") token: String,
        @QueryMap map: Map<String, String>
    ): Call<LogOutResponse>

    @Headers("Accept: application/json")
    @GET(STUDENT_LOG_OUT)
    fun studentLogOut(
        @Header("Authorization") token: String,
        @QueryMap map: Map<String, String>
    ): Call<LogOutResponse>


    @Headers("Accept: application/json")
    @GET(COORDINATOR_LOG_OUT)
    fun CoordinatorLogOut(
        @Header("Authorization") token: String,
    ): Call<LogOutResponse>*/


    @Headers("Accept: application/json")
    @POST(TEACHER_LOG_OUT)
    fun logOut(@Header("Authorization") token: String): Call<LogOutResponse>

    @Headers("Accept: application/json")
    @POST(STUDENT_LOG_OUT)
    fun studentLogOut(
        @Header("Authorization") token: String): Call<LogOutResponse>

    @Headers("Accept: application/json")
    @POST(COORDINATOR_LOG_OUT)
    fun CoordinatorLogOut(
        @Header("Authorization") token: String,
    ): Call<LogOutResponse>

    @FormUrlEncoded
    @POST(CLASSES_LIST)
    fun getClassesList(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.ClassesListResponse>


    @FormUrlEncoded
    @POST(STUDENT_CLASSES_LIST)
    fun getStudentClassesList(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<StudentClassesListResponse>

    @POST(STUDENT_JOIN_CLASS_STATUS)
    fun studentJoinClassStatus(
        @Header("Authorization") token: String,
        @Query("class_id") classId: Int,
        @Query("class_join_with") deviceName: String,

    ): Call<StudentJoinClassResponse>


    @POST(ADD_TECHNICAL_REPORT_STATUS)
    fun addTechnicalStatusReport(
        @Header("Authorization") token: String,
        @Query("user_type") userType: String,
        @Query("issue") issue: String,
        @Query("class_name") className: String,
        @Query("comment") comment: String,
    ) : Call<AcceptRejectResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(ADD_FEEDBACK)
    fun addFeedback(
        @Header("Authorization") token: String,
        @FieldMap map: Map<String, String>
    ): Call<AddFeedbackResponse>


    @FormUrlEncoded
    @POST(ACCEPT_REJECT_CLASS)
    fun acceptRejectCompletedClasses(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.AcceptRejectResponse>

    @FormUrlEncoded
    @POST(ACCEPT_REJECT_CLASS)
    fun studentAcceptRejectCompletedClasses(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<AcceptRejectResponse>


    @FormUrlEncoded
    @POST(TEACHER_WALLET_LIST)
    fun getTeacherWalletList(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.WalletListResponse>


    @FormUrlEncoded
    @POST(STUDENT_WALLET_LIST)
    fun getStudentWalletList(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<WalletListResponse>


    @POST(TEACHER_ATTENDANCE)
    fun getTeacherAttendance(@Header("Authorization") token: String): Call<TeacherAttendanceResponse>


    @POST(MONTH_ATTENDANCE)
    fun getStudentAttendance(@Header("Authorization") token: String): Call<StudentAttendanceResponse>

    @Headers("Accept: application/json")
    @GET(ABSENT_PRESENT)
    fun getAbsentPresentList(@Header("Authorization") token: String): Call<AbsentPresentResponse>


    @Headers("Accept: application/json")
    @POST(STUDENT_ASSESSMENT)
    fun getAssessments(@Header("Authorization") token: String): Call<AssessmentResponse>


    @Headers("Accept: application/json")
    @POST(STUDENT_TEST_SERIES)
    fun getStudentTestSeries(@Header("Authorization") token: String): Call<TestSeriesResponse>


    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST(UPDATE_TEST_SCORE)
    fun updateTestScore(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<SaveMessageResponse>


    @Multipart
    @Headers("Accept: application/json;cone")
    @POST(SAVE_STUDENT_TEST_FILE)
    fun saveStudentTestFile(
        @Part student_file: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("test_series_id") groupId: RequestBody
    ): Call<SaveMessageResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST(TEACHER_TEST_SERIES_UPLOAD)
    fun uploadTestSeries(
        @Part teacher_file: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("test_name") testName: RequestBody,
        @Part("submission_date") date: RequestBody,
        @Part("group_id") groupId: RequestBody,
        @Part("test_type") testType: RequestBody,
        @Part("marks") totalMarks: RequestBody
    ): Call<SaveMessageResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST(TEACHER_UPLOAD_HW)
    fun addTeacherClassHW(
        @Header("Authorization") token: String,
        @Part("class_id") id: RequestBody,
        @Part homeWorkFile: MultipartBody.Part?
    ): Call<HomeWorkFileResponse>


    @Headers("Accept: application/json")
    @GET(TEACHER_TEST_SERIES)
    fun getTeacherTestSeries(@Header("Authorization") token: String): Call<TestSeriesResponse>


    @Headers("Accept: application/json")
    @GET(TEACHER_TEST_GROUP_NAMES)
    fun getTeacherGroupNames(@Header("Authorization") token: String): Call<GroupNameResponse>


    @Headers("Accept: application/json")
    @POST(TEACHER_LIBRARY_LIST)
    fun getLibraryList(@Header("Authorization") token: String): Call<LibraryDataResponse>

    @Headers("Accept: application/json")
    @POST(STUDENT_LIBRARY_LIST)
    fun getStudentLibraryList(@Header("Authorization") token: String): Call<LibraryDataResponse>

    @Headers("Accept: application/json")
    @POST(FEES_LIST)
    fun getFeesList(@Header("Authorization") token: String): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.FeesListResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(TEACHER_SLOTS)
    fun getTeacherSlots(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<TeacherSlotsResponse>


    @Headers("Accept: application/json")
    @POST(TEACHER_PROFILE_DETAILS)
    fun getTeacherProfileDetails(@Header("Authorization") token: String): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.ProfileDetailResponse>

    @Headers("Accept: application/json")
    @POST(STUDENT_PROFILE_DETAILS)
    fun getStudentProfileDetails(@Header("Authorization") token: String): Call<ProfileDetailResponse>


    @Headers("Accept: application/json")
    @POST(COORDINATOR_PROFILE_DETAILS)
    fun getCoordinatorProfileDetails(@Header("Authorization") token: String): Call<CoordinatorProfileDetailResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST(UPDATE_TEACHER_PROFILE)
    fun updateTeacherProfile(
        @Part profile: MultipartBody.Part?,
        @Part aadhaar_card: MultipartBody.Part?,
        @Part pan_card: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("first_name") fName: RequestBody,
        @Part("last_name") lName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("location") address: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<com.app.kiyalearning.teacher.dashboard.ui.home.pojos.UpdateProfileResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST(UPDATE_STUDENT_PROFILE)
    fun updateStudentProfile(
        @Part profile: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("name") fName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("location") address: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<UpdateProfileResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST(UPDATE_COORDINATOR_PROFILE)
    fun updateCoordinatorProfile(
        @Part profile: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("first_name") fName: RequestBody,
        @Part("last_name") lName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("address") address: RequestBody,
        @Part("joining_date") joiningDate: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<UpdateProfileResponse>


    @GET(COUNTRY_LIST)
    fun getCountryList(@Header("Authorization") token: String): Call<CountryListResponse>


    @GET(TEACHER_NOTIFICATION_LIST)
    fun getNotifications(@Header("Authorization") token: String): Call<NotificationsResponse>

    @GET(STUDENT_NOTIFICATION_LIST)
    fun getStudentNotifications(@Header("Authorization") token: String): Call<NotificationsResponse>


    @GET(COORDINATOR_NOTIFICATION_LIST)
    fun getCoordinatorNotifications(@Header("Authorization") token: String): Call<NotificationsResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(MARK_READ_NOTIFICATIONS)
    fun markReadNotifications(
        @Header("Authorization") token: String, @FieldMap map: HashMap<String, Any>
    ): Call<RetrieveChatResponse>

    //Chatting
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(CHAT_LIST)
    fun getChatList(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<ChatListResponse>


    @Headers("Accept: application/json")
    @GET(RETRIEVE_MESSAGES)
    fun retrieveChatHistory(
        @Header("Authorization") token: String, @Path("group_id") groupId: String,
    ): Call<RetrieveChatResponse>

    @Headers("Accept: application/json")
    @GET()
    fun retrieveChatHistoryWithUrl(
        @Header("Authorization") token: String, @Url url:String,
    ): Call<RetrieveChatResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(MARK_READ_MESSAGES)
    fun markReadChat(
        @Header("Authorization") token: String, @FieldMap map: HashMap<String, Any>
    ): Call<RetrieveChatResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(DELETE_MESSAGE)
    fun deleteChat(
        @Header("Authorization") token: String, @FieldMap map: HashMap<String, Any>
    ): Call<DeleteChatResponse>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(SORT_CHAT_GROUP_BY_USER)
    fun pinUnpinGroup(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<SaveMessageResponse>



    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST(SAVE_MESSAGE)
    fun saveMessage(
        @Header("Authorization") token: String,
        @FieldMap map: HashMap<String, Any>
    ): Call<SaveMessageResponse>



    @Multipart
    @Headers("Accept: application/json")
    @POST(SAVE_MESSAGE)
    fun saveImageMessage(
        @Part message_media: MultipartBody.Part?,
        @Header("Authorization") token: String,
        @Part("group_id") groupId: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("user_type") userType: RequestBody,
        @Part("message_media_type") messageFrom: RequestBody,
        @Part("message") message: RequestBody
    ): Call<SaveMessageResponse>


    /*

        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST(LIST_BY_STATUS)
        fun getShiftListByStatus(@Header("Authorization") token: String, @FieldMap map : Map<String, String>): Call<ShiftListResponse>



        @GET(DISPUTED_SHIFT_LIST)
        fun getDisputedShiftList(@Header("Authorization") token: String): Call<ShiftListResponse>



        @FormUrlEncoded
        @POST(ACCEPT_PAYOUT)
        fun acceptPayout(@Header("Authorization") token: String,@FieldMap map: HashMap<String, Any>): Call<AcceptPayoutResponse>


        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST(SHIFT_DISPUTES)
        fun getShiftDisputes(@Header("Authorization") token: String, @FieldMap map : Map<String, String>): Call<DisputesResponses>





        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST(START_SHIFT)
        fun startShift(@Header("Authorization") token: String,@FieldMap map: HashMap<String, Any>): Call<StartShiftResponse>


        @FormUrlEncoded
        @POST(ACTION_ON_END_REQUEST)
        fun actionOnEndShiftRequest(@Header("Authorization") token: String,@FieldMap map: HashMap<String, Any>): Call<ActionOnEndRequest>



        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST(APPLY_SHIFT)
        fun applyShift(@Header("Authorization") token: String,@FieldMap map: HashMap<String, Any>): Call<ApplyShiftResponse>


        @Headers("Accept: application/json")
        @FormUrlEncoded
        @POST(END_SHIFT)
        fun endShift(@Header("Authorization") token: String,@FieldMap map: HashMap<String, Any>): Call<EndShiftResponse>


        @Headers("Accept: application/json")
        @GET(PROFILE_DETAILS)
        fun getProfileDetails(@Header("Authorization") token: String): Call<ProfileDetailResponse>


        @Headers("Accept: application/json")
        @GET(TERMS_CONDITIONS)
        fun getTermsConditions(@Header("Authorization") token: String): Call<TermsConditionsResponse>


        @Multipart
        @Headers("Accept: application/json")
        @POST(UPDATE_PROFILE)
        fun updateProfile(  @Part photo : MultipartBody.Part?
                            ,@Header("Authorization") token: String
                            ,@Part("first_name") fName: RequestBody
                            ,@Part("last_name") lName: RequestBody
                            ,@Part("email") email: RequestBody
                            ,@Part("address") address: RequestBody
                            ,@Part("gender") gender: RequestBody
                            ,@Part("qualification") qualification: RequestBody
                            ,@Part("experience") experience: RequestBody
                            ,@Part("skill") skills: RequestBody
                            ,@Part("work_domain") workDomain: RequestBody
                            ,@Part("industry") industry: RequestBody
                            ,@Part resume:ArrayList<MultipartBody.Part> ): Call<UpdateProfileResponse>





        @Multipart
        @Headers("Accept: application/json")
        @POST(UPDATE_PROFILE)
        fun updateKYC(  @Part adhar_image  : MultipartBody.Part?
                       ,@Part pan_image : MultipartBody.Part?
                       ,@Part  medical_certificate : MultipartBody.Part?
                       ,@Part police_verification : MultipartBody.Part?
                            ,@Header("Authorization") token: String
                            ,@Part("account_holder_name") name: RequestBody
                            ,@Part("bank_name") bankName: RequestBody
                            ,@Part("branch") branchName: RequestBody
                            ,@Part("account_number") acNum: RequestBody
                            ,@Part("ifsc_code") ifscCode: RequestBody
                             ): Call<UpdateProfileResponse>



        @FormUrlEncoded
        @POST(DELETE_RESUME)
        fun deleteResume(@FieldMap map: HashMap<String, Any>): Call<HotelsHomeResponse>


        @Multipart
        @Headers("Accept: application/json")
        @POST(SAVE_DISPUTE)
        fun addDispute( @Part attachment : MultipartBody.Part?
                            ,@Header("Authorization") token: String
                            ,@Part("shift_id") shiftId: RequestBody
                            ,@Part("title") title: RequestBody
                            ,@Part("description") desc: RequestBody
                            ): Call<DisputesResponses>




        @FormUrlEncoded
        @POST(HOTEL_LIST)
        fun getHotelList(@FieldMap map: HashMap<String, Any>): Call<HotelsHomeResponse>



        @POST(NOTIFICATION_LIST)
        fun getNotifications(@Header("Authorization") token: String): Call<NotificationsResponse>


        @FormUrlEncoded
        @POST(HOTEL_LIST)
        fun takeBreak(@FieldMap map: HashMap<String, Any>): Call<TakeBreakResponse>

        @FormUrlEncoded
        @POST(HOTEL_LIST)
        fun checkOut(@FieldMap map: HashMap<String, Any>): Call<CheckOutResponse>

    */
}