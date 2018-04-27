package io.swiftfest.www.swiftfest.views.agenda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.Schedule.ScheduleRow
import io.swiftfest.www.swiftfest.data.UserAgendaRepo
import io.swiftfest.www.swiftfest.utils.isNullorEmpty
import io.swiftfest.www.swiftfest.views.detail.AgendaDetailFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.helpers.EmptyViewHelper
import java.util.*


/**
 * Fragment for an agenda day
 */
class AgendaDayFragment : Fragment(), FlexibleAdapter.OnItemClickListener {
    private val timeHeaders = HashMap<String, ScheduleAdapterItemHeader>()

    private var dayFilter: String = ""
    private var onlyMyAgenda: Boolean = false

    private lateinit var userAgendaRepo: UserAgendaRepo
    private var headerAdapter: FlexibleAdapter<ScheduleAdapterItem>? = null

    private lateinit var agendaRecyler: RecyclerView
    private lateinit var emptyStateView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayFilter = arguments?.getString(ARG_DAY) ?: ""
        userAgendaRepo = UserAgendaRepo.getInstance(context!!)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                val fragmentManager = activity?.supportFragmentManager
                if (fragmentManager?.backStackEntryCount!! > 0) {
                    fragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.agenda_day_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NOTE: Kotlin Extensions' agenda_vew is null in setupHeaderAdapter sporadically, so do this old school
        agendaRecyler = view.findViewById(R.id.agenda_recycler)
        emptyStateView = view.findViewById(R.id.empty_view)

        agendaRecyler.layoutManager = LinearLayoutManager(activity?.applicationContext)

        onlyMyAgenda = arguments?.getBoolean(ARG_MY_AGENDA) ?: false

        fetchScheduleData()

        activity?.supportFragmentManager?.addOnBackStackChangedListener(backStackChangeListener)
    }

    private val backStackChangeListener: () -> Unit = {
        if (onlyMyAgenda) {
            fetchScheduleData()
        } else {
            headerAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.supportFragmentManager?.removeOnBackStackChangedListener(backStackChangeListener)
    }

    fun updateList() {
        agendaRecyler.adapter.notifyDataSetChanged()
    }

//    val dataListener: ValueEventListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            val rows = ArrayList<ScheduleRow>()
//            for (roomSnapshot in dataSnapshot.children) {
//                val key = roomSnapshot.key
//                val data = roomSnapshot.getValue(ScheduleEvent::class.java)
//                Log.d(TAG, "Event: $data")
//                if (data != null) {
//                    val scheduleRow = data.toScheduleRow(key)
//                    if (scheduleRow.date == dayFilter && (!onlyMyAgenda
//                                    || onlyMyAgenda && userAgendaRepo.isSessionBookmarked(scheduleRow.id))) {
//                        rows.add(scheduleRow)
//                    }
//                }
//            }
//
//            setupHeaderAdapter(rows)
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.w(TAG, "scheduleQuery:onCancelled", databaseError.toException())
//        }
//    }

    private fun fetchScheduleData() {
        // TODO: fetch schedule.
    }

    private fun setupHeaderAdapter(rows: List<ScheduleRow>) {
        val items = ArrayList<ScheduleAdapterItem>(rows.size)
        for (row in rows) {
            val timeDisplay = if (row.startTime.isEmpty()) "Unscheduled" else row.startTime
            var header: ScheduleAdapterItemHeader? = timeHeaders[timeDisplay]
            if (header == null) {
                header = ScheduleAdapterItemHeader(timeDisplay)
                timeHeaders[timeDisplay] = header
            }

            val item = ScheduleAdapterItem(row, header)
            items.add(item)
        }

        val sortedItems = items.sortedWith(
                compareBy<ScheduleAdapterItem> { it.itemData.utcStartTimeString }
                        .thenBy { it.roomSortOrder })

        headerAdapter = FlexibleAdapter(sortedItems)
        headerAdapter!!.addListener(this)
        agendaRecyler.adapter = headerAdapter
        agendaRecyler.addItemDecoration(FlexibleItemDecoration(agendaRecyler.context).withDefaultDivider())
        headerAdapter!!.expandItemsAtStartUp().setDisplayHeadersAtStartUp(true)

        EmptyViewHelper(headerAdapter, emptyStateView, null,null)
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        val adapterItem = try { headerAdapter?.getItem(position) } catch (e: Exception) { null }
        if (adapterItem is ScheduleAdapterItem) {
            val itemData = adapterItem.itemData
            if (itemData.primarySpeakerName.isNullorEmpty()) {
                val url = itemData.photoUrlMap.get(itemData.primarySpeakerName)

                if (!url.isNullorEmpty()) {
                    // event where info URL is in the photoUrls string
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    val packageManager = activity?.packageManager
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i)
                    }
                    return false
                }
            }

            activity?.let {
                AgendaDetailFragment.addDetailFragmentToStack(it.supportFragmentManager, itemData);
            }
        }

        return true
    }

    companion object {

        private val TAG = AgendaDayFragment::class.java.name
        private const val ARG_DAY = "day"
        private const val ARG_MY_AGENDA = "my_agenda"

        fun newInstance(myAgenda: Boolean, day: String): AgendaDayFragment {
            val fragment = AgendaDayFragment()
            val args = Bundle()
            args.putBoolean(ARG_MY_AGENDA, myAgenda)
            args.putString(ARG_DAY, day)
            fragment.arguments = args
            return fragment
        }
    }
}

