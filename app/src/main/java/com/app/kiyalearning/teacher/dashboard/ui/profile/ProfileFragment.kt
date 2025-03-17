package com.app.kiyalearning.teacher.dashboard.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.FragmentProfileBinding
import com.app.kiyalearning.databinding.ProfileImageViewBinding
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ProfileViewModel
import com.app.kiyalearning.teacher.dashboard.ui.profile.sliders.FeesFragment
import com.app.kiyalearning.teacher.dashboard.ui.profile.sliders.PersonalDetailsFragment
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.bumptech.glide.Glide

class ProfileFragment :Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var viewModel: ProfileViewModel


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val activity=activity as com.app.kiyalearning.teacher.dashboard.DashBoardActivity

        if(activity.binding!=null)
            activity.binding!!.headerTxt.text=  resources.getString(R.string.profile)

        viewPager= binding.viewPager

        val pagerAdapter: FragmentStateAdapter = ProfileScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        binding.myDetailsTextView.setOnClickListener{
            viewPager.currentItem=0
        }
        binding.preferencesTextView.setOnClickListener{
            viewPager.currentItem=1
        }

        binding.editProfile.setOnClickListener{
            startActivity(Intent(requireContext(), ViewProfileActivity::class.java))
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 ->
                    {
                        binding.myDetailsTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                        binding.preferencesTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.editProfile.visibility=View.VISIBLE
                    }
                    else ->
                    {
                        binding.myDetailsTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.preferencesTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                        binding.editProfile.visibility=View.GONE
                    }
                }
            }
        })

        setUpViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.profileDetailResponse.observe(viewLifecycleOwner) {
            if (it.success) {
                AppPref.updateTeacherProfileData(requireContext(),it.data)
                setProfileDetails()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProfileDetails() {
        Glide
            .with(this)
            .load(AppPref.getUserImage(requireContext()))
            .centerCrop()
            .placeholder(R.drawable.logo)
            .into(binding.userImage)

        binding.userImage.setOnClickListener{
            val viewBinding= ProfileImageViewBinding.inflate(LayoutInflater.from(binding.userImage.context))

            Glide.with(binding.userImage.context)
                .load(AppPref.getUserImage(requireContext()))
                .placeholder(R.drawable.logo)
                .fitCenter()
                .into(viewBinding.profileImage)

            viewBinding.tvDownloadImg.visibility = View.GONE
            viewBinding.tvCancelImg.visibility = View.GONE

            AlertDialog.Builder(binding.userImage.context)
                .setView(viewBinding.root)
                // The dialog is automatically dismissed when a dialog button is clicked.
                .show()
        }

        val name= AppPref.getUserFirstName(requireContext())+" "+ AppPref.getUserLastName(requireContext())
        binding.userName.text=name

    }



    override fun onResume() {
        super.onResume()
        setProfileDetails()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            viewModel.getProfileDetails(requireActivity())
        }
    }

    private class ProfileScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            val fragment: Fragment = when (position) {
                0 -> PersonalDetailsFragment()
                else -> FeesFragment()
            }
            return fragment
        }
    }

}


