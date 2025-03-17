package com.app.kiyalearning.auth.pojos
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ReSendOTPResponse (
    val status: Long,
    val success: Boolean,
    val message: String
)



data class SignUpResponse(
    val status: Long,
    val success: Boolean,
    val message: String
)


data class LoginResponse (
    val status: Long,
    val success: Boolean,
    val message: String,
    val data: ProfileLogin
)

data class ForgetPasswordResponse (
    val status: Long,
    val success: Boolean,
    val message: String
)

data class ProfileLogin (
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("first_name")
    val firstName: Any? = null,

    @SerializedName("last_name")
    val lastName: Any? = null,

    val email: String,

    @SerializedName("mobile_no")
    val mobileNo: String,

    val gender: Any? = null,

    val profile: Any? = null,

    @SerializedName("Address")
    val address: String,

    val location: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("user_type")
    val userType: String,

    val token: String,

    val status: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("address")
    val coordinatorAddress: String

)


data class HomeWorkFileResponse (
    @SerializedName("status")
    val status: Long,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<Any>
):Serializable

