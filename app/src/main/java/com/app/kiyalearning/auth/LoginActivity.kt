package com.app.kiyalearning.auth

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.auth.viewmodels.AuthViewModel
import com.app.kiyalearning.coordinator.dashboard.CoordinatorDashBoardActivity
import com.app.kiyalearning.databinding.ActivityLoginBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)


        setUpViewModel()

        if(AppPref.getFirebaseToken(this)=="No_Value_Found")
        {
            setToken()
        }

        binding.loginButton.setOnClickListener{
            val userId=binding.userId.text.toString()
            val password=binding.loginPassword.text.toString()
            if(userId.isBlank())
                binding.userId.error=getString(R.string.please_enter_valid_email)
//            else if(!AppValidator.isValidEmail(email.trim()))
//                binding.loginEmail.error=getString(R.string.please_enter_valid_email)
            else if(password.isBlank())
                binding.loginPassword.error=getString(R.string.please_enter_valid_email)
            else
            {
                if(MyNetworks.isNetworkAvailable(applicationContext))
                {
                    fieldsEnabled(false)
//                    val intent=Intent(this, OTPActivity::class.java)
//                    intent.putExtra("MOBILE",mobile)
//                    startActivity(intent)
                    binding.loader.pB.visibility=View.VISIBLE
                    viewModel.login(this,userId,password)
                }
            }
        }

        var myBoolean=false
        binding.passwordEye.setOnClickListener{
            if(myBoolean)
            {
                binding.loginPassword.inputType= InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordEye.setImageDrawable(AppCompatResources.getDrawable(applicationContext,R.drawable.ic_views))
            }
            else
            {
                binding.passwordEye.setImageDrawable(AppCompatResources.getDrawable(applicationContext,R.drawable.ic_password_hide_eye))
                binding.loginPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }

            myBoolean=!myBoolean
            binding.loginPassword.setSelection( binding.loginPassword.length())
        }

        binding.donTHaveAnAccountTextview.setOnClickListener{
//            val intent=Intent(this, SignUpActivity::class.java)
//            startActivity(intent)
        }

        binding.forgotPassword.setOnClickListener{
            val intent=Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.signUpButton.setOnClickListener{
             binding.donTHaveAnAccountTextview.performClick()
        }


        KeyboardVisibilityEvent.setEventListener(this) {
            val layout = binding.nestedScrollView
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams

            val dip = 260f
            val r: Resources = resources
            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            if (!it)
                px = 0f

            params.bottomMargin = px.toInt()
            layout.layoutParams = params
        }

    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        viewModel.loginResponse.observe(this) {
            fieldsEnabled(true)
            binding.loader.pB.visibility= View.GONE
            if (it.success) {
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                openActivity(it)
            }
            else
                viewModel.validationError.value=it.message

        }

        viewModel.validationError.observe(this) {
            fieldsEnabled(true)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fieldsEnabled(status:Boolean)
    {
        if(status)
            binding.loader.pB.visibility= View.GONE
        else
            binding.loader.pB.visibility= View.VISIBLE


        binding.loginButton.isEnabled=status
        binding.signUpButton.isEnabled=status
        binding.donTHaveAnAccountTextview.isEnabled=status

    }

    private fun setToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("MyTag", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            AppPref.setFirebaseToken(this,token)

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
            //Log.d("MyTag", "token=$token")
            //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    private fun openActivity(loginResponse: LoginResponse) {
        // Log.d("MyTag", "isSignUpActivity: $isSignUpActivity")
        //  Log.d("MyTag", "loginResponse: $loginResponse")
        /*    if(isSignUpActivity)
                startActivity(Intent(this, LoginActivity::class.java))
            else
            {
                AppPref.updateUserData(this,loginResponse)
                startActivity(Intent(this, DashBoardActivity::class.java))
            }*/

        AppPref.updateUserData(this,loginResponse)

        if(AppPref.getUserType(this) == "coordinator" || AppPref.getUserType(this) == "sub admin")
        {
            val intent=Intent(binding.root.context, CoordinatorDashBoardActivity::class.java)
            AppPref.setUserName(this,AppPref.getUserFirstName(this)+" "+AppPref.getUserLastName(this))
            AppPref.setUserAddress(this,loginResponse.data.coordinatorAddress)


            startActivity(intent)
            finishAffinity()
        }
        else{
           // startActivity(Intent(this, LocationPermissionActivity::class.java))
          //   finishAffinity()
            var intent=Intent(binding.root.context, com.app.kiyalearning.teacher.dashboard.DashBoardActivity::class.java)
            if(AppPref.getUserType(this) == "student")
                intent=Intent(binding.root.context, DashBoardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}