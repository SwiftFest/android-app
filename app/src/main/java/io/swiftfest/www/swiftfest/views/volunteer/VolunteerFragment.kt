package io.swiftfest.www.swiftfest.views.volunteer


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.Volunteer
import io.swiftfest.www.swiftfest.utils.loadUriInCustomTab
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import kotlinx.android.synthetic.main.volunteer_fragment.volunteer_recycler


class VolunteerFragment : Fragment(), FlexibleAdapter.OnItemClickListener {

    private lateinit var volunteerAdapter: FlexibleAdapter<VolunteerAdapterItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.volunteer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchVolunteerData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

//    val dataListener: ValueEventListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            val rows = ArrayList<VolunteerEvent>()
//            for (volunteerSnapshot in dataSnapshot.children) {
//                val volunteer = volunteerSnapshot.getValue(VolunteerEvent::class.java)
//                if (volunteer != null) {
//                    rows.add(volunteer)
//                }
//            }
//
//            setupVolunteerAdapter(rows)
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.e(javaClass.canonicalName, "detailQuery:onCancelled", databaseError.toException())
//        }
//    }

    private fun fetchVolunteerData() {
        // TODO: fetch volunteer data.
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        val item = volunteerAdapter.getItem(position)
        if (item is VolunteerAdapterItem && !item.itemData.twitter.isEmpty()) {
            val context = activity as Context
            context.loadUriInCustomTab(String.format("%s%s", resources.getString(R.string.twitter_link), item.itemData.twitter))
            return false
        }

        return true // propagate.
    }


    private fun setupVolunteerAdapter(rows: ArrayList<Volunteer>) {
        val items = rows.map { VolunteerAdapterItem(it) }
        volunteer_recycler.layoutManager = LinearLayoutManager(volunteer_recycler.context)
        volunteerAdapter = FlexibleAdapter(items)
        volunteerAdapter.addListener(this)
        volunteer_recycler.adapter = volunteerAdapter
        volunteer_recycler.addItemDecoration(FlexibleItemDecoration(volunteer_recycler.context).withDefaultDivider())
        volunteerAdapter.expandItemsAtStartUp().setDisplayHeadersAtStartUp(false)
    }
}
