package io.swiftfest.www.swiftfest.views.volunteer


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.DataProvider
import io.swiftfest.www.swiftfest.data.model.Volunteer
import kotlinx.android.synthetic.main.volunteer_fragment.*


class VolunteerFragment : Fragment(), FlexibleAdapter.OnItemClickListener {

    private val dataProvider = DataProvider.instance
    private lateinit var volunteerAdapter: FlexibleAdapter<VolunteerAdapterItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.volunteer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVolunteerAdapter(dataProvider.volunteers)
    }

    private fun setupVolunteerAdapter(volunteers: List<Volunteer>) {
        val items = volunteers.map { VolunteerAdapterItem(it) }
        volunteer_recycler.layoutManager = LinearLayoutManager(volunteer_recycler.context)
        volunteerAdapter = FlexibleAdapter(items)
        volunteerAdapter.addListener(this)
        volunteer_recycler.adapter = volunteerAdapter
        volunteer_recycler.addItemDecoration(FlexibleItemDecoration(volunteer_recycler.context).withDefaultDivider())
        volunteerAdapter.expandItemsAtStartUp().setDisplayHeadersAtStartUp(false)
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        val item = volunteerAdapter.getItem(position) as VolunteerAdapterItem
        val volunteer = item.itemData
        val bodyText = StringBuilder()
        bodyText.append(volunteer.title).append("\n---")

        volunteer.social.map {
            bodyText.append("\n${it.name.capitalize()}:\n${it.link}\n")
        }

        val context = activity as Context
        AlertDialog.Builder(context)
                .setTitle("${volunteer.name} ${volunteer.surname}")
                .setMessage(bodyText.toString())
                .setIcon(R.drawable.icon)
                .setPositiveButton(android.R.string.yes, { dialog, which ->
                    dialog.dismiss()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        return true // propagate.
    }


}
