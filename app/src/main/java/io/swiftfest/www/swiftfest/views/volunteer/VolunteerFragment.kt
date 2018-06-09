package io.swiftfest.www.swiftfest.views.volunteer


import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
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

    override fun onItemClick(itemView: View, position: Int): Boolean {
        val item = volunteerAdapter.getItem(position) as VolunteerAdapterItem
        val volunteer = item.itemData
        val bodyText = StringBuilder()
        val titleText: String

        if (volunteer.title.isNullOrBlank()) {
            titleText = "Volunteer"
        } else {
            titleText = volunteer.title!!
        }

        bodyText.append(titleText).append("\n---")

        volunteer.social.map {
            bodyText.append("\n${it.name.capitalize()}:\n${it.link}\n")
        }

        val context = activity as Context


        val dialog = MaterialDialog.Builder(context)
                .customView(R.layout.volunteer_dialog_layout, true)
                .positiveText("Ok")
                .onPositive { dialog, which ->
                    dialog.dismiss()
                }
                .show()

        val titleHeaderText = "Volunteer: ${volunteer.name} ${volunteer.surname}"

        val view = dialog.getCustomView()
        if (view != null) {
            view.findViewById<TextView>(R.id.title).text = titleHeaderText
            view.findViewById<TextView>(R.id.body).text = bodyText.toString()
        }

        return true
    }


}
