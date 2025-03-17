package com.app.kiyalearning.student.dashboard.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.databinding.ActivityTermsConditionsBinding
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.HomeViewModel
import com.app.kiyalearning.util.UtilClass
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class TermsConditionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsConditionsBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTermsConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

        val heading=intent.getStringExtra("HEADING")
        binding.headerTxt.text=heading


       // setUpViewModel()

        binding.backIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        setEventListener(this, KeyboardVisibilityEventListener {
            val layout = binding.nestedScrollView
            val params  = layout.layoutParams as ViewGroup.MarginLayoutParams

            val dip = 200f
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


    override fun onDestroy() {
        super.onDestroy()
        UtilClass.currentPhotoPath=null
    }

 /*   private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.termsConditionsResponse.observe(this) {
            binding.loader.pB.visibility=View.GONE
            if (it.status == 200L) {

                val tagHandler= MyHtmlTagHandler()
                val imageGetter = ImageGetter(
                    resources,
                    binding.termsConditions
                )
                // Using Html framework to parse html
                val styledText= HtmlCompat.fromHtml(it.content,
                    HtmlCompat.FROM_HTML_MODE_LEGACY,
                    imageGetter,tagHandler)
                binding.termsConditions.text = styledText

            }else
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.validationError.observe(this) {
            binding.loader.pB.visibility=View.GONE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onResume() {
        super.onResume()
        if(MyNetworks.isNetworkAvailable(this))
        {
            binding.loader.pB.visibility=View.VISIBLE
            viewModel.getTermsConditions(this)
        }

    }*/

}