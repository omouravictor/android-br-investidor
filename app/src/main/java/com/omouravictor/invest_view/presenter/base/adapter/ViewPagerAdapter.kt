package com.omouravictor.invest_view.presenter.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragments: List<Pair<Fragment, String>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position].first

}