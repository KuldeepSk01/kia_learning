package com.app.kiyalearning.student.dashboard.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.FragmentMatchesBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.student.dashboard.ui.home.slider.CancelledFragment
import com.app.kiyalearning.student.dashboard.ui.home.slider.CompletedFragment
import com.app.kiyalearning.student.dashboard.ui.home.slider.NewFragment


class ClassesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private lateinit var viewPager: ViewPager2

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        val activity = activity as DashBoardActivity

        binding.apply {
            tvTodayClass.visibility = View.GONE
            tvOngoingClass.visibility = View.GONE
            tvAcceptedClass.visibility = View.GONE
            binding.tvTimeOut.visibility = View.GONE
        }

        if (activity.binding != null) activity.binding!!.headerTxt.text =
            resources.getString(R.string.title_classes)

        viewPager = binding.viewPager

        val pagerAdapter: FragmentStateAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        binding.todayTextView.setOnClickListener {
            setTvBackground(binding.todayTextView)
            viewPager.currentItem = 0
        }
        binding.oneMonthTextView.setOnClickListener {
            setTvBackground(binding.oneMonthTextView)
            viewPager.currentItem = 1
        }
        binding.sevenDaysTextView.setOnClickListener {
            setTvBackground(binding.sevenDaysTextView)
            viewPager.currentItem = 2
        }


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setTvBackground(binding.todayTextView)
                    }
                    1 -> {
                        setTvBackground(binding.oneMonthTextView)
                    }
                    else -> {
                        setTvBackground(binding.sevenDaysTextView)
                    }

                }
            }
        })
        return binding.root
    }

    /*created by kuldeep*/
    private fun setTvBackground(tv: TextView) {
        binding.apply {
            todayTextView.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }
            oneMonthTextView.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }
            sevenDaysTextView.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }

            (tv as TextView).apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.dark_gray, null))
                background = ResourcesCompat.getDrawable(
                    resources, R.drawable.button_shape_green_background, null
                )
            }
        }

    }


    private class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            val fragment: Fragment = when (position) {
                0 -> NewFragment()
                1 -> CompletedFragment()
                else -> CancelledFragment()
            }
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = activity as DashBoardActivity
        viewPager.currentItem = activity.classesSelectedTab
        activity.binding!!.dashboardNotificationLay.visibility = View.GONE
        activity.binding!!.classesFilterLay.visibility = View.VISIBLE
    }


    override fun onPause() {
        super.onPause()
        val activity = activity as DashBoardActivity
        activity.binding!!.dashboardNotificationLay.visibility = View.VISIBLE
        activity.binding!!.classesFilterLay.visibility = View.GONE
    }
}
