package io.swiftfest.www.swiftfest.views.agenda

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R
import kotlinx.android.synthetic.main.agenda_fragment.tablayout
import kotlinx.android.synthetic.main.agenda_fragment.viewpager
import java.util.Calendar

class AgendaFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.agenda_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDayPager(savedInstanceState)
    }


    private fun setupDayPager(savedInstanceState: Bundle?) {
        viewpager.adapter = AgendaDayPagerAdapter(childFragmentManager,
                isMyAgenda())

        tablayout.setupWithViewPager(viewpager)

        if (savedInstanceState != null) {
            viewpager.currentItem = savedInstanceState.getInt(TAB_POSITION)
        } else {
            // set current day to second if today matches
            val today = Calendar.getInstance()
            val dayTwo = Calendar.getInstance()
            dayTwo.set(2019, Calendar.JULY, 30)
            if (today == dayTwo) {
                viewpager.currentItem = 1
            }
        }
    }

    fun isMyAgenda() = arguments?.getBoolean(ARG_MY_AGENDA) ?: false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAB_POSITION, tablayout.selectedTabPosition)
    }

    companion object {

        const val TAB_POSITION = "POSITION"

        private const val ARG_MY_AGENDA = "my_agenda"

        fun newInstance() = newInstance(false)
        fun newInstanceMySchedule() = newInstance(true)

        private fun newInstance(myAgenda: Boolean): AgendaFragment {
            val fragment = AgendaFragment()
            val args = Bundle()
            args.putBoolean(ARG_MY_AGENDA, myAgenda)
            fragment.arguments = args
            return fragment
        }
    }
}
