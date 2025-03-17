package com.app.kiyalearning.api

class WebConstant {

    companion object{

        //Auth Related Apis
        const val BASE_URL = "https://kiyalearningapp.in/api/"

        //Auth Related Apis
        const val LOGIN="login"
        const val LOGIN_WITH_SIBLING="login_with_user_id"
        const val FORGOT_PASSWORD="send_password_to_user_mail"
        const val SIGN_UP="e-signup"
        const val RESEND_OTP="resend-otp"


        //matches
        const val CLASSES_LIST="teacher_booked_slot"
        const val STUDENT_CLASSES_LIST="student_booked_slot"
        const val STUDENT_JOIN_CLASS_STATUS="changeClassJoiningStatus"
        const val ACCEPT_REJECT_CLASS="accept_booked_slot_by_tutor"

        const val ADD_TECHNICAL_REPORT_STATUS="save_user_issue"


        const val TEACHER_WALLET_LIST="teacher_wallet"
        const val STUDENT_WALLET_LIST="student_wallet"
        const val MONTH_ATTENDANCE="get_student_attendance"
        const val TEACHER_ATTENDANCE="get_teacher_attendance"
        const val ABSENT_PRESENT="absent_present"
        const val TEACHER_LIBRARY_LIST="teacher_library"
        const val STUDENT_LIBRARY_LIST="student_library"
        const val FEES_LIST="get_teacher_fees"
        const val TEACHER_SLOTS="get_teacher_slots"
        const val TEACHER_UPLOAD_HW="addClassHomeWork"
        const val STUDENT_ASSESSMENT="getStudentAssessment"
        const val ADD_FEEDBACK="saveFeedback"
        const val STUDENT_TEST_SERIES="getStudentTestSeries"
        const val SAVE_STUDENT_TEST_FILE = "saveStudentTestDoc"
        const val TEACHER_TEST_SERIES="getTeacherTestSeies"
        const val TEACHER_TEST_SERIES_UPLOAD="saveTestSeiesByTeacher"
        const val TEACHER_TEST_GROUP_NAMES="getTeacherGroups"
        const val UPDATE_TEST_SCORE="saveStudentScoredByTeacher"



        const val TEACHER_DASHBOARD_DATA="teacher_dashboard"
        const val STUDENT_DASHBOARD_DATA="student_dashboard"

        //temp
        const val TEACHER_NOTIFICATION_LIST = "get_teacher_notification"
        const val STUDENT_NOTIFICATION_LIST = "get_student_notification"
        const val COORDINATOR_NOTIFICATION_LIST = "get_coordinator_notification"
        const val MARK_READ_NOTIFICATIONS = "marked_unread_notification"


        //chatting
        const val CHAT_LIST = "get_chat_group"
        const val RETRIEVE_MESSAGES = "get_message/{group_id}"
        const val SAVE_MESSAGE = "save_message"
        const val SORT_CHAT_GROUP_BY_USER = "sort_chat_group_by_user"
        const val MARK_READ_MESSAGES = "mark_unread_message"
        const val DELETE_MESSAGE = "deleteMessage"





        //profile
        const val TEACHER_LOG_OUT="teacher_logout"
        const val STUDENT_LOG_OUT="student_logout"
        const val COORDINATOR_LOG_OUT="coordinator_logout"
        const val TEACHER_PROFILE_DETAILS="teacher_profile"
        const val STUDENT_PROFILE_DETAILS="student_profile"
        const val COORDINATOR_PROFILE_DETAILS="coordinator_profile"
        const val UPDATE_TEACHER_PROFILE="edit_profile"
        const val UPDATE_STUDENT_PROFILE="edit_student_profile"
        const val UPDATE_COORDINATOR_PROFILE="edit_coordinator_profile"
        const val COUNTRY_LIST="all_location"


    }
}