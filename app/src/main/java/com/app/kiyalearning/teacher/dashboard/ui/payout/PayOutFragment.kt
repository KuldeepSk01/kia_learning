package com.app.kiyalearning.teacher.dashboard.ui.payout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.FragmentPayoutBinding
import com.app.kiyalearning.teacher.dashboard.DashBoardActivity
import com.app.kiyalearning.teacher.dashboard.ui.payout.walletSliders.DateRangeFragment
import com.app.kiyalearning.teacher.dashboard.ui.payout.walletSliders.MonthlyFragment

class PayOutFragment :Fragment() {

    private var _binding: FragmentPayoutBinding? = null
    private lateinit var viewPager: ViewPager2


    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPayoutBinding.inflate(inflater, container, false)
        val activity=activity as DashBoardActivity

        if(activity.binding!=null)
            activity.binding!!.headerTxt.text=  resources.getString(R.string.title_payout)

        viewPager= binding.viewPager

        val pagerAdapter: FragmentStateAdapter = WalletScreenSlidePagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter


        binding.monthlyTextView.setOnClickListener{
            viewPager.currentItem=0
        }
        binding.dateRangeTextView.setOnClickListener{
            viewPager.currentItem=1
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 ->
                    {
                        binding.monthlyTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                        binding.dateRangeTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                    }
                    else ->
                    {
                        binding.monthlyTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color)
                        )
                        binding.dateRangeTextView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.app_theme_color1)
                        )
                    }
                }
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



private class WalletScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> MonthlyFragment()
            else -> DateRangeFragment()
        }
        return fragment
    }
}