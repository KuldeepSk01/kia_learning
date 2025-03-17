package com.app.kiyalearning.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ScreenSlideFragment1Binding
class ScreenSliderFragment1 :Fragment(){

    private lateinit var screenSlideFragment1Binding: ScreenSlideFragment1Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        screenSlideFragment1Binding= ScreenSlideFragment1Binding.inflate ( inflater )



         return screenSlideFragment1Binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity=activity as IntroActivity
        activity.binding.loginButton.text = getString(R.string.next)
    }
}