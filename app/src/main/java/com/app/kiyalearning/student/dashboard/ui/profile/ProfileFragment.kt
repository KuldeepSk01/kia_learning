package com.app.kiyalearning.student.dashboard.ui.profile

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
import com.app.kiyalearning.databinding.StudentFragmentProfileBinding
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.student.dashboard.ui.home.viewmodels.ProfileViewModel
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.LogOutResponse
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment :Fragment() {

    private var _binding: StudentFragmentProfileBinding? = null
    private lateinit var viewModel: ProfileViewModel


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StudentFragmentProfileBinding.inflate(inflater, container, false)
        val activity=activity as DashBoardActivity

        if(activity.binding!=null)
            activity.binding!!.headerTxt.text=  resources.getString(R.string.profile)


        setUpViewModel()

        binding.editProfile.setOnClickListener{
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
                    val call: Call<LogOutResponse> = api.studentLogOut("Bearer $token")
                    call.enqueue(object : Callback<LogOutResponse> {
                        override fun onResponse(call: Call<LogOutResponse>, response: Response<LogOutResponse>) {
                            binding.loader.pB.visibility = View.GONE
                            AppPref.userLogout(requireActivity())
                            val intent =  Intent(requireActivity(), IntroActivity::class.java)
                            activity.finishAffinity()
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<LogOutResponse>, t: Throwable) {
                            AppPref.userLogout(requireActivity())
                            val intent =  Intent(requireActivity(), IntroActivity::class.java)
                            activity.finishAffinity()
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

        binding.pullToRefresh.setOnRefreshListener {
            if (MyNetworks.isNetworkAvailable(requireActivity())) {
                viewModel.getProfileDetails(requireActivity())
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.profileDetailResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                AppPref.updateProfileData(requireContext(),it.data)
                setProfileDetails()
                binding.schoolName.text=it.data.teachingPreference.schoolName
                binding.country.text=it.data.teachingPreference.country
                binding.curriculum.text=it.data.teachingPreference.curriculum
                binding.grade.text=it.data.teachingPreference.grades
                binding.subject.text=it.data.teachingPreference.subjects

            }else
                viewModel.validationError.value=it.message
        }

        viewModel.validationError.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
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


     //   val name= AppPref.getUserFirstName(requireContext())+" "+ AppPref.getUserLastName(requireContext())
        binding.fullName.text=AppPref.getUserName(requireContext())
        binding.mobileNum.text=AppPref.getUserMob(requireContext())
        binding.email.text=AppPref.getUserEmail(requireContext())
        binding.location.text=AppPref.getUserAddress(requireContext())
        binding.gender.text=AppPref.getUserGender(requireContext())
        binding.dob.text=AppPref.getUserDob(requireContext())


    }



    override fun onResume() {
        super.onResume()
        setProfileDetails()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            viewModel.getProfileDetails(requireActivity())
        }
    }


}


