package io.swiftfest.www.swiftfest.views.agenda

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.swiftfest.www.swiftfest.data.Schedule

class AgendaDayPagerAdapter internal constructor(fm: FragmentManager, private val myAgenda: Boolean)
    : FixedFragmentStatePagerAdapter(fm) {

    private val PAGE_COUNT = 2
    private val tabTitles = arrayOf("Day 1", "Day 2")

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        return AgendaDayFragment.newInstance(myAgenda,
                if (position == 0) Schedule.MONDAY else Schedule.TUESDAY
        )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun updateFragmentItem(position: Int, fragment: Fragment) {
        if (fragment is AgendaDayFragment) {
            fragment.updateList()
        }
    }

    override fun getFragmentItem(position: Int): Fragment {
        return getItem(position)
    }
}