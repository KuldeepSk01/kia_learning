package com.app.kiyalearning.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ScreenSlideFragment3Binding

class ScreenSliderFragment3 :Fragment(){

    private lateinit var screenSlideFragment3Binding: ScreenSlideFragment3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        screenSlideFragment3Binding= ScreenSlideFragment3Binding.inflate ( inflater )
         return screenSlideFragment3Binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity=activity as IntroActivity
        activity.binding.loginButton.text = requireContext().getString(R.string.login)
    }
}