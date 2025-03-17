package com.app.kiyalearning.coordinator.dashboard.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.kiyalearning.R
import com.app.kiyalearning.api.Api
import com.app.kiyalearning.api.RestManager
import com.app.kiyalearning.coordinator.dashboard.CoordinatorDashBoardActivity
import com.app.kiyalearning.coordinator.dashboard.ui.profile.viewmodels.CoordinatorProfileViewModel
import com.app.kiyalearning.databinding.CoordinatorProfileBinding
import com.app.kiyalearning.intro.IntroActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.pojos.LogOutResponse
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.MyNetworks
import com.app.kiyalearning.util.UtilClass
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ProfileFragment :Fragment() {
    private var _binding: CoordinatorProfileBinding? = null
    private lateinit var viewModel: CoordinatorProfileViewModel


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CoordinatorProfileBinding.inflate(inflater, container, false)
       // _binding = DataBindingUtil.set
        val activity=activity as CoordinatorDashBoardActivity

        if(activity.binding!=null)
            activity.binding!!.headerTxt.text=  resources.getString(R.string.profile)


        setUpViewModel()
        binding.viewmodel=viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.editProfile.setOnClickListener{
            startActivity(Intent(requireContext(), CoordinatorViewProfileActivity::class.java))
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
                    val call: Call<LogOutResponse> = api.CoordinatorLogOut("Bearer $token")
                    call.enqueue(object : Callback<LogOutResponse> {
                        override fun onResponse(call: Call<LogOutResponse>, response: Response<LogOutResponse>) {
                            binding.loader.pB.visibility = View.GONE
                            // fieldsEnabled(true)
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
                viewModel.getCoordinatorProfileDetails(requireActivity())
            }
        }


        return binding.root
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(this)[CoordinatorProfileViewModel::class.java]

        viewModel.profileDetailResponse.observe(viewLifecycleOwner) {
            binding.loader.pB.visibility=View.GONE
            binding.pullToRefresh.isRefreshing = false
            if (it.success) {
                AppPref.updateCoordinatorProfileData(requireContext(),it.data)
                setProfileDetails()
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

        AppPref.setUserName(requireContext(),AppPref.getUserFirstName(requireContext())+" "+AppPref.getUserLastName(requireContext()))

        binding.fullName.text=AppPref.getUserName(requireContext())
      //  binding.mobileNum.text=AppPref.getUserMob(requireContext())
        binding.email.text=AppPref.getUserEmail(requireContext())
        binding.location.text=AppPref.getUserAddress(requireContext())
        binding.dob.text=AppPref.getUserDob(requireContext())
    }

    override fun onResume() {
        super.onResume()
        setProfileDetails()
        if (MyNetworks.isNetworkAvailable(requireActivity())){
            binding.loader.pB.visibility=View.VISIBLE
            viewModel.getCoordinatorProfileDetails(requireActivity())
        }
    }


}


