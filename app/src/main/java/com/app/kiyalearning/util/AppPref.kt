package com.app.kiyalearning.util

import android.content.Context
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.student.dashboard.ui.home.pojos.CoordinatorProfile
import com.app.kiyalearning.student.dashboard.ui.home.pojos.Profile


class AppPref {

    companion object{

        fun setUserId(c: Context, userId:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserId", userId).apply()
        }

        fun getUserId(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserId","").toString()
        }

        fun setKeyBoardHeightInPixel(c: Context, height:Float) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putFloat("KeyboardHeight", height).apply()
        }

        fun getKeyBoardHeightInPixel(c: Context):Float {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getFloat("KeyboardHeight",0f)
        }

        fun setUserEmpId(c:Context, userId:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("EmpId", userId).apply()
        }
        fun getUserToken(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserToken","").toString()
        }

        fun getTokenType(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("TokenType","").toString()
        }

        fun getUserEmpId(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("EmpId","").toString()
        }

        fun setUserFirstName(c:Context, userName:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserFirstName", userName).apply()
        }

        fun setUserName(c:Context, userName:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserName", userName).apply()
        }

        fun getUserFirstName(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserFirstName","").toString()
        }

        fun getUserName(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserName","").toString()
        }

        fun setUserLastName(c:Context, userName:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserLastName", userName).apply()
        }

        fun getUserLastName(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserLastName","").toString()
        }


        fun setUserEmail(c:Context, userEmail:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserEmail", userEmail).apply()
        }
        fun getUserEmail(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserEmail","").toString()
        }


        fun setUserType(c:Context, userType : String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserType", userType).apply()
        }
        fun getUserType(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserType","").toString()
        }
        fun setUserMob(c:Context, userMob:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserMob", userMob).apply()
        }
        fun getUserMob(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserMob","").toString()
        }
        fun setUserImage(c:Context, userImage:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserImage", userImage).apply()
        }
        fun getUserImage(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserImage","").toString()
        }

        fun getFirebaseToken(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("FirebaseToken","No_Value_Found").toString()
        }
        fun setFirebaseToken(c:Context, token:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("FirebaseToken", token).apply()
        }


        fun setUserGender(c:Context, gender:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserGender", gender).apply()
        }
        fun getUserGender(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserGender","").toString()
        }

        fun getUserAddress(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserAddress","").toString()
        }

        fun setUserAddress(c:Context, address:String) {
            c.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit().putString("UserAddress", address).apply()
        }

        fun getUserAadharCard(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserAadhar","").toString()
        }

        fun getUserPanCard(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserPan","").toString()
        }


        fun getPerHourFees(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("PerHourFees","").toString()
        }

        fun getUserDob(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserDOB","").toString()
        }


        fun isUserCheckIn(c: Context):String {
            return c.getSharedPreferences("my_file",Context.MODE_PRIVATE).getString("UserIsCheckIn","").toString()
        }


        fun updateUserData(context:Context,loginResponse: LoginResponse)
        {
            context.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit()
                .putString("UserId", loginResponse.data.id.toString())
               // .putString("EmpId", loginResponse.data[0].empid.toString())
                .putString("UserFirstName", loginResponse.data.firstName.toString())
                .putString("UserLastName", loginResponse.data.lastName.toString())
                .putString("UserGender", loginResponse.data.gender.toString())
                .putString("UserMob", loginResponse.data.mobileNo)
                .putString("UserToken", loginResponse.data.token)
              //  .putString("UserDob", loginResponse.data[0].dob)
                .putString("TokenType", "Bearer")
                .putString("UserEmail",loginResponse.data.email)
                .putString("UserAddress",loginResponse.data.location)
                .putString("UserType",loginResponse.data.userType)
                .putString("UserName",loginResponse.data.name)
//                .putString("UserCountry",loginResponse.data[0].country.toString())
//                .putString("UserState",loginResponse.data[0].state.toString())
//                .putString("UserCity",loginResponse.data[0].city.toString())
            //    .putString("UserCompany",loginResponse.data[0].assignCompany.toString())
                .putString("UserImage",loginResponse.data.profile.toString())
            //    .putString("StudentId",loginResponse.data[0].studentId.toString())
              /*  .putString("UserWorkRole",loginResponse.data[0].)
                .putString("UserDepartment",loginResponse.data.country)
                .putString("UserWorkLocation",loginResponse.data.state)
                .putString("UserIsCheckIn",loginResponse.data.city)*/
                .apply()
        }

        fun updateProfileData(context: Context, profile: Profile)
        {
            context.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit()
                .putString("UserId", profile.id.toString())
                .putString("UserFirstName", profile.firstName.toString())
                .putString("UserLastName", profile.lastName.toString())
                .putString("UserGender", profile.gender.toString())
                .putString("UserMob", profile.mobileNo)
                .putString("UserEmail", profile.email)
                .putString("UserAddress", profile.address)
                .putString("PerHourFees", profile.perHrFees)
                .putString("UserDOB", profile.dob)
                .putString("UserAadhar", profile.aadharCard)
                .putString("UserPan", profile.panCard)
                .putString("UserName",profile.name)
               // .putString("UserCompany",loginResponse.data[0].assignCompany.toString())
                .putString("UserImage",profile.profile.toString())
            //    .putString("StudentId",loginResponse.data[0].studentId.toString())
                .apply()
        }

        fun updateCoordinatorProfileData(context: Context, profile: CoordinatorProfile)
        {
            context.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit()
                .putString("UserId", profile.id.toString())
                .putString("UserFirstName", profile.firstName.toString())
                .putString("UserLastName", profile.lastName.toString())
                .putString("UserMob", profile.mobileNo)
                .putString("UserEmail", profile.email)
                .putString("UserAddress", profile.address)
                .putString("UserDOB", profile.joiningDate)
                .putString("UserName",profile.name)
                // .putString("UserCompany",loginResponse.data[0].assignCompany.toString())
                .putString("UserImage",profile.profile.toString())
                //    .putString("StudentId",loginResponse.data[0].studentId.toString())
                .apply()
        }

        fun updateTeacherProfileData(context: Context, profile: com.app.kiyalearning.teacher.dashboard.ui.home.pojos.Profile)
        {
            context.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit()
                .putString("UserId", profile.id.toString())
                .putString("UserFirstName", profile.firstName.toString())
                .putString("UserLastName", profile.lastName.toString())
                .putString("UserGender", profile.gender.toString())
                .putString("UserMob", profile.mobileNo)
                .putString("UserEmail", profile.email)
                .putString("UserAddress", profile.address)
                .putString("PerHourFees", profile.perHrFees)
                .putString("UserDOB", profile.dob)
                .putString("UserAadhar", profile.aadharCard)
                .putString("UserPan", profile.panCard)
                .putString("UserName",profile.name)
                // .putString("UserCompany",loginResponse.data[0].assignCompany.toString())
                .putString("UserImage",profile.profile.toString())
                //    .putString("StudentId",loginResponse.data[0].studentId.toString())
                .apply()
        }


        fun userLogout(context:Context)
        {
            val editor= context.getSharedPreferences("my_file",Context.MODE_PRIVATE).edit()
            editor.remove("UserId")
            editor.remove("UserName")
            editor.remove("UserFirstName")
            editor.remove("UserLastName")
            editor.remove("UserEmail")
            editor.remove("UserMob")
            editor.remove("UserImage")
            editor.remove("UserToken")
            editor.remove("TokenType")
            editor.remove("UserGender")
            editor.remove("UserDOB")
            editor.remove("UserAddress")
            editor.remove("UserAadhar")
            editor.remove("UserPan")
            editor.remove("PerHourFees")
            editor.remove("UserType")
            editor.apply()
        }


        //Location Pref
        fun getUserLat(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("userLat","").toString()
        }

        fun setUserLat(c: Context, userLat: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("userLat",userLat)
                .apply()
        }

        fun getUserLon(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("userLon","").toString()
        }

        fun setUserLon(c: Context, userLon: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("userLon",userLon)
                .apply()
        }

        fun getLocName(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("UserAddress","").toString()
        }

        fun setLocName(c: Context, locName: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("UserAddress",locName)
                .apply()

        }

        fun getCheckOutLocName(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("checkOutLocName","").toString()
        }

        fun setCheckOutLocName(c: Context, locName: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("checkOutLocName",locName)
                .apply()
        }

        fun getCheckOutTime(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("checkOutTime","").toString()
        }

        fun setCheckOutTime(c: Context, time: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("checkOutTime",time)
                .apply()
        }

        fun getCheckInTime(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("checkInTime","").toString()
        }

        fun setCheckInTime(c: Context, time: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("checkInTime",time)
                .apply()
        }

        fun getCheckInDate(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("checkInDate","").toString()
        }

        fun setCheckInDate(c: Context, time: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("checkInDate",time)
                .apply()
        }


        fun getCheckOutDate(c: Context): String {
            return c.getSharedPreferences("location",Context.MODE_PRIVATE).getString("checkOutDate","").toString()
        }

        fun setCheckOutDate(c: Context, time: String) {
            c.getSharedPreferences("location",Context.MODE_PRIVATE).edit()
                .putString("checkOutDate",time)
                .apply()
        }


    }
}