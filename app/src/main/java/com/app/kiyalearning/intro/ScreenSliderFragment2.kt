package com.app.kiyalearning.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ScreenSlideFragment2Binding

class ScreenSliderFragment2 :Fragment(){

    private lateinit var screenSlideFragment2Binding: ScreenSlideFragment2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        screenSlideFragment2Binding= ScreenSlideFragment2Binding.inflate ( inflater )
         return screenSlideFragment2Binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity=activity as IntroActivity
        activity.binding.loginButton.text = getString(R.string.next)
    }
}