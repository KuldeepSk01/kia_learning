package com.app.kiyalearning.student.dashboard.ui.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.kiyalearning.R
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.student.dashboard.ui.home.librarySliders.BooksFragment
import com.app.kiyalearning.student.dashboard.ui.home.librarySliders.CourseFragment
import com.app.kiyalearning.student.dashboard.ui.home.librarySliders.OthersFragment
import com.app.kiyalearning.databinding.FragmentShiftBinding


class LibraryFragment :Fragment() {

    private var _binding: FragmentShiftBinding? = null
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShiftBinding.inflate(inflater, container, false)
        val activity=activity as DashBoardActivity

        if(activity.binding!=null)
           activity.binding!!.headerTxt.text=  resources.getString(R.string.library)



        viewPager= binding.viewPager

        val pagerAdapter: FragmentStateAdapter = LibraryScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        binding.todayTextView.setOnClickListener{
            viewPager.currentItem=0
        }
        binding.sevenDaysTextView.setOnClickListener{
            viewPager.currentItem=1
        }
        binding.oneMonthTextView.setOnClickListener{
            viewPager.currentItem=2
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 ->
                    {
                        binding.todayTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                        binding.sevenDaysTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.oneMonthTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                    }
                    1 ->
                    {
                        binding.todayTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.sevenDaysTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                        binding.oneMonthTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                    }
                    else ->
                    {
                        binding.todayTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.sevenDaysTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.oneMonthTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                    }

                }
                Log.d("MyTag", "registerOnPageChangeCallback frag1 position: " + position)
            }
        })

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class LibraryScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            val fragment: Fragment = when (position) {
                0 -> CourseFragment()
                1 -> BooksFragment()
                else -> OthersFragment()
            }
            return fragment
        }
    }

}



