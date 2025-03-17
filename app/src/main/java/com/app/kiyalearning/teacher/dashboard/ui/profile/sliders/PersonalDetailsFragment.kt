package com.app.kiyalearning.teacher.dashboard.ui.profile.sliders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.api.Api
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.databinding.FragmentPersonalDetailsBinding
import com.app.kiyalearning.databinding.ProfileImageViewBinding
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.LogOutResponse
import com.app.kiyalearning.teacher.dashboard.ui.home.viewmodels.ProfileViewModel
import com.app.kiyalearning.teacher.dashboard.ui.profile.ViewProfileActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalDetailsFragment :Fragment(){

    private lateinit var binding: FragmentPersonalDetailsBinding
    private lateinit var viewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentPersonalDetailsBinding.inflate ( inflater )

        setUpViewModel()

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getProfileDetails(requireActivity())
            }
        }

        binding.uploadPanCard.setOnClickListener{
            startActivity(Intent(requireContext(), ViewProfileActivity::class.java))
        }

        binding.uploadAadharCard.setOnClickListener{
            startActivity(Intent(requireContext(), ViewProfileActivity::class.java))
        }



        binding.logoutCard.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(requireActivity())
            val alertDialog=dialogBuilder.create()

            dialogBuilder.setMessage("Sure to Log Out")
            dialogBuilder.setPositiveButton("Ok") { _, _ ->
                if(MyNetworks.isNetworkAvailable(requireActivity()))
                {
                    // fieldsEnabled(false)
                    binding.loader.pB.visibility = View.VISIBLE
                    val map=HashMap<String,String>()
                    //map["type"] = "business"
                    val token= AppPref.getUserToken(requireActivity().applicationContext)

                    val api: Api = RestManager.getInstance()
                    val call: Call<LogOutResponse> = api.logOut("Bearer $token")
                    call.enqueue(object : Callback<LogOutResponse> {
                        override fun onResponse(call: Call<LogOutResponse>, response: Response<LogOutResponse>) {
                            binding.loader.pB.visibility = View.GONE
                            // fieldsEnabled(true)
                            AppPref.userLogout(requireActivity())
                            val intent =  Intent(requireActivity(), IntroActivity::class.java)
                            activity?.finishAffinity()
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<LogOutResponse>, t: Throwable) {
                            AppPref.userLogout(requireActivity())
                            val intent =  Intent(requireActivity(), IntroActivity::class.java)
                            activity?.finishAffinity()
                            startActivity(intent)
                        }
                    })

                }
            }

            dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                alertDialog.cancel()
            }
            dialogBuilder.show()
        }

        return binding.root
    }


    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.profileDetailResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                AppPref.updateTeacherProfileData(requireContext(),it.data)
                setProfileDetails()
            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility= View.GONE
            binding.pullToRefresh.isRefreshing = false
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProfileDetails() {
        val name=AppPref.getUserFirstName(requireContext())+" "+AppPref.getUserLastName(requireContext())
        binding.fullName.text=name
        binding.mobileNum.text=AppPref.getUserMob(requireContext())
        binding.email.text=AppPref.getUserEmail(requireContext())
        binding.location.text=AppPref.getUserAddress(requireContext())
        binding.gender.text=AppPref.getUserGender(requireContext())
        binding.dob.text=AppPref.getUserDob(requireContext())


        if(AppPref.getUserAadharCard(requireContext())=="" )
        {
            binding.aadharCardLayout.visibility=View.GONE
        }else
        {
            binding.uploadAadharCard.visibility=View.GONE
            Glide.with(requireContext())
                .load(AppPref.getUserAadharCard(requireContext()))
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(binding.aadharCard)


            binding.aadharCard.setOnClickListener{
                val viewBinding= ProfileImageViewBinding.inflate(LayoutInflater.from(binding.aadharCard.context))

                Glide.with(binding.aadharCard.context)
                    .load(AppPref.getUserAadharCard(requireContext()))
                    .placeholder(R.drawable.logo)
                    .fitCenter()
                    .into(viewBinding.profileImage)


                AlertDialog.Builder(binding.aadharCard.context)
                    .setView(viewBinding.root)
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .show()
            }

        }

        if(AppPref.getUserPanCard(requireContext())=="")
        {
            binding.panCardLayout.visibility=View.GONE
        }else
        {
            binding.uploadPanCard.visibility=View.GONE
            Glide.with(requireContext())
                .load(AppPref.getUserPanCard(requireContext()))
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(binding.panCard)


            binding.panCard.setOnClickListener{
                val viewBinding= ProfileImageViewBinding.inflate(LayoutInflater.from(binding.aadharCard.context))

                Glide.with(binding.aadharCard.context)
                    .load(AppPref.getUserPanCard(requireContext()))
                    .placeholder(R.drawable.logo)
                    .fitCenter()
                    .into(viewBinding.profileImage)


                AlertDialog.Builder(binding.aadharCard.context)
                    .setView(viewBinding.root)
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .show()
            }

        }




    }

    override fun onResume() {
        super.onResume()
           if (MyNetworks.isNetworkAvailable(requireActivity())){
               viewModel.getProfileDetails(requireActivity())
               binding.loader.pB.visibility=View.VISIBLE
           }
    }



}