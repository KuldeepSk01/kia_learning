package com.app.kiyalearning.teacher.dashboard.ui.classes

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
import com.app.kiyalearning.teacher.dashboard.DashBoardActivity
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.AcceptedFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.CancelledFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.CompletedFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.NewFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.OngoingFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.TimeoutFragment
import com.app.kiyalearning.teacher.dashboard.ui.home.slider.TodayFragment

class ClassesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private lateinit var viewPager: ViewPager2
    private var scrollPosition: Int = 0


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        val activity = activity as DashBoardActivity

        if (activity.binding != null) activity.binding!!.headerTxt.text =
            resources.getString(R.string.title_classes)

        viewPager = binding.viewPager

       /* binding.apply {
            tvTodayClass.visibility = View.GONE
            tvOngoingClass.visibility = View.GONE
            tvAcceptedClass.visibility = View.GONE
        }*/

        val pagerAdapter: FragmentStateAdapter = ScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        binding.todayTextView.setOnClickListener {
            viewPager.currentItem = 0
        }

        binding.tvTodayClass.setOnClickListener {
            viewPager.currentItem = 1
        }

        /*new  filter created by kuldeep*/

        binding.tvAcceptedClass.setOnClickListener {
            viewPager.currentItem = 2
        }
        binding.tvOngoingClass.setOnClickListener {
            viewPager.currentItem = 3
        }

        binding.oneMonthTextView.setOnClickListener {
            viewPager.currentItem = 4

        }
        binding.sevenDaysTextView.setOnClickListener {
            viewPager.currentItem = 5

        }
        binding.tvTimeOut.setOnClickListener {
            viewPager.currentItem = 6

        }


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setTvBackground(binding.todayTextView) //new
                    }

                    1 -> {
                        setTvBackground(binding.tvTodayClass) //today

                    }

                    2 -> {
                        setTvBackground(binding.tvAcceptedClass)//accepted
                    }

                    3 -> {
                        setTvBackground(binding.tvOngoingClass)//ongoing
                    }

                    4 -> {
                        setTvBackground(binding.oneMonthTextView)//completed
                    }
                    5 -> {
                        setTvBackground(binding.sevenDaysTextView)//rejected
                    }
                    6 -> {
                        setTvBackground(binding.tvTimeOut)//completed
                    }
                    else -> {
                        setTvBackground(binding.sevenDaysTextView)//rejected
                    }

                }
            }
        })

        viewPager.currentItem = activity.classesSelectedTab

        return binding.root
    }

    /*created by kuldeep singh*/
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
            tvTodayClass.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }
            tvAcceptedClass.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }
            tvOngoingClass.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }
            sevenDaysTextView.apply {
                setTextColor(ResourcesCompat.getColor(resources, R.color.app_theme_color, null))
                background = null
            }

            tvTimeOut.apply {
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
            return 7
        }

        override fun createFragment(position: Int): Fragment {
            val fragment: Fragment = when (position) {
                0 -> NewFragment()//new
                1 -> TodayFragment()//today
                2 -> AcceptedFragment()//accepted
                3 -> OngoingFragment()//ongoing
                4 -> CompletedFragment()//completed
                5 -> CancelledFragment()//rejected
                6 -> TimeoutFragment()//timeout
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
