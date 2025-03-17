package com.app.kiyalearning.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.R
import com.app.kiyalearning.api.Api
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.auth.pojos.ForgetPasswordResponse
import com.app.kiyalearning.databinding.ActivityForgetPasswordBinding
import com.app.kiyalearning.util.AppValidator
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UtilClass.setStatusBarProperty ( this )

        binding.backToAgain.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.sendButton.setOnClickListener {

            if(MyNetworks.isNetworkAvailable(applicationContext)){
                val email=binding.emailEdittext.text

                if(email.isBlank() || email.isEmpty())
                {
                    binding.emailEdittext.error = "Email Required"
                }else if(!AppValidator.isValidEmail(email.toString()))
                {
                    Toast.makeText(applicationContext,"PLease enter correct Email",Toast.LENGTH_SHORT).show()
                }else
                {
                    binding.loader.pB.visibility = View.VISIBLE
                    fieldsEnabled(false)
                    val map = HashMap<String, Any>()
                    map["email"] = email

                    val api: Api = RestManager.getInstance()
                    val call: Call<ForgetPasswordResponse> = api.forgetPassword(map)
                    call.enqueue(object : Callback<ForgetPasswordResponse> {
                        override fun onResponse(call: Call<ForgetPasswordResponse>, response: Response<ForgetPasswordResponse>) {
                            binding.loader.pB.visibility = View.GONE
                            fieldsEnabled(true)
                            if (response.body() != null) {
                                val loginResponse: ForgetPasswordResponse = response.body()!!
                                if(loginResponse.success)
                                    onBackPressedDispatcher.onBackPressed()
                                Toast.makeText(applicationContext, loginResponse.message,Toast.LENGTH_LONG).show()
                            } else{
                                try {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    Toast.makeText(applicationContext, jObjError.getString("message"), Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                            binding.loader.pB.visibility = View.GONE
                            fieldsEnabled(true)
                            //when id not found
                            Toast.makeText(applicationContext, R.string.server_error,Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }else
            {
                Toast.makeText(this,"Network not available",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fieldsEnabled(status:Boolean)
    {
        if(status)
            binding.loader.pB.visibility= View.GONE
        else
            binding.loader.pB.visibility= View.VISIBLE

        binding.sendButton.isEnabled=status
    }
}