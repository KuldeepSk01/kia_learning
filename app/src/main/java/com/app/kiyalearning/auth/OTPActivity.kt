package com.app.kiyalearning.auth

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.auth.pojos.LoginResponse
import com.app.kiyalearning.auth.viewmodels.AuthViewModel
import com.app.kiyalearning.databinding.ActivityOtpBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class OTPActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
    private lateinit var viewModel: AuthViewModel
    private var mobile:String=""
    private var isSignUpActivity=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        mobile=intent.getStringExtra("MOBILE").toString()
        isSignUpActivity=intent.getBooleanExtra("IS_SIGN_UP",false)

       /* if(AppPref.getFirebaseToken(this)=="")
        {
            setToken()
        }*/


        setUpViewModel()
        binding.verifyMobile.text=mobile
        binding.otpNumber1.requestFocus()
        otpSetup()

        binding.changeMobileButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.verifyButton.setOnClickListener{
            AppPref.setUserMob(this,mobile)

            val otp=StringBuilder()
            otp.append(binding.otpNumber1.text.toString())
                .append(binding.otpNumber2.text.toString())
                .append(binding.otpNumber3.text.toString())
                .append(binding.otpNumber4.text.toString())
                .append(binding.otpNumber5.text.toString())
                .append(binding.otpNumber6.text.toString())


            if(otp.toString().isEmpty())
            {
                Toast.makeText(this,"Please enter otp",Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(MyNetworks.isNetworkAvailable(applicationContext))
                {
                    fieldsEnabled(false)
//                    if(isSignUpActivity)
//                        viewModel.verifyOtpSignUp(this,mobile,otp.toString())
//                    else
                        binding.loader.pB.visibility=View.VISIBLE
                        viewModel.verifyOtp(this,mobile,otp.toString())

                }
            }
        }

        binding.resendCodeButton.setOnClickListener{
            if(MyNetworks.isNetworkAvailable(applicationContext))
            {
                fieldsEnabled(false)
                viewModel.reSendOTP(this,mobile)
            }

        }

        setEventListener(this, KeyboardVisibilityEventListener {
            val layout = binding.nestedScrollView
            val params  = layout.layoutParams as ViewGroup.MarginLayoutParams

            val dip = 260f
            val r: Resources = resources
            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
            if(!it)
                px=0f

            params.bottomMargin=px.toInt()
            layout.layoutParams = params
        })

    }
    private fun fieldsEnabled(status:Boolean)
    {
        if(status)
            binding.loader.pB.visibility=View.GONE
        else
            binding.loader.pB.visibility=View.VISIBLE


        binding.resendCodeButton.isEnabled=status
        binding.changeMobileButton.isEnabled=status
        binding.verifyButton.isEnabled=status

    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        viewModel.reSendResponse.observe(this) {
            fieldsEnabled(true)
            binding.loader.pB.visibility= View.GONE
            if (it.success)
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
            else
                viewModel.validationError.value=it.message

        }

        viewModel.loginResponseWithOtp.observe(this) {
            fieldsEnabled(true)
            binding.loader.pB.visibility= View.GONE
            if (it.success)
            {
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

    private fun otpSetup()
    {
        binding.otpNumber1.addTextChangedListener(GenericTextWatcher(binding.otpNumber1, binding.otpNumber2))
        binding.otpNumber2.addTextChangedListener(GenericTextWatcher(binding.otpNumber2, binding.otpNumber3))
        binding.otpNumber3.addTextChangedListener(GenericTextWatcher(binding.otpNumber3, binding.otpNumber4))
        binding.otpNumber4.addTextChangedListener(GenericTextWatcher(binding.otpNumber4, binding.otpNumber5))
        binding.otpNumber5.addTextChangedListener(GenericTextWatcher(binding.otpNumber5, binding.otpNumber6))
        binding.otpNumber6.addTextChangedListener(GenericTextWatcher(binding.otpNumber6, null))

//GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        binding.otpNumber1.setOnKeyListener(GenericKeyEvent(binding.otpNumber1, null))
        binding.otpNumber2.setOnKeyListener(GenericKeyEvent(binding.otpNumber2, binding.otpNumber1))
        binding.otpNumber3.setOnKeyListener(GenericKeyEvent(binding.otpNumber3, binding.otpNumber2))
        binding.otpNumber4.setOnKeyListener(GenericKeyEvent(binding.otpNumber4,binding.otpNumber3))
        binding.otpNumber5.setOnKeyListener(GenericKeyEvent(binding.otpNumber5,binding.otpNumber4))
        binding.otpNumber6.setOnKeyListener(GenericKeyEvent(binding.otpNumber6,binding.otpNumber5))
    }

   /* private fun setToken() {
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
            Log.d("MyTag", "token=$token")
            //   Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }*/

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
     //   startActivity(Intent(this, LocationPermissionActivity::class.java))
       // finishAffinity()

        var intent=Intent(binding.root.context, com.app.kiyalearning.teacher.dashboard.DashBoardActivity::class.java)
        if(AppPref.getUserType(this) == "student")
            intent=Intent(binding.root.context, DashBoardActivity::class.java)

        startActivity(intent)
        finishAffinity()


    }
}
class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otp_number_1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }
}

class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) :
    TextWatcher {
    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.otp_number_1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp_number_2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp_number_3 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp_number_4 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.otp_number_5 -> if (text.length == 1) nextView!!.requestFocus()
            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

}

