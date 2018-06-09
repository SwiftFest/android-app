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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.DataProvider
import io.swiftfest.www.swiftfest.data.model.Social
import io.swiftfest.www.swiftfest.data.model.Volunteer
import io.swiftfest.www.swiftfest.utils.loadUriInCustomTab
import kotlinx.android.synthetic.main.volunteer_dialog_layout.*
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
            val titleText: String
            if (volunteer.title.isNullOrBlank()) {
                titleText = "Volunteer"
            } else {
                titleText = volunteer.title!!
            }

            view.findViewById<TextView>(R.id.subTitle).text = titleText
//            view.findViewById<TextView>(R.id.body).text = bodyText.toString()
            val socialLayout = view.findViewById<LinearLayout>(R.id.socialLayout)!!

            volunteer.social.map {
                addSocialItemToLayout(socialLayout, it)
            }


        }

        return true
    }

    private fun addSocialItemToLayout(socialLayout: LinearLayout, it: Social) {
        val socialImage = ImageView(activity)
        socialImage.setImageResource(getSocialResource(it.name))
        socialImage.setPadding(8, 8, 8, 8)
        socialImage.setOnClickListener { v ->
            activity?.loadUriInCustomTab(String.format("%s", it.link))
        }
        socialLayout.addView(socialImage)
    }

    private fun getSocialResource(name: String): Int = when (name.toLowerCase()) {
        "twitter" -> R.drawable.social_twitter
        "linkedin" -> R.drawable.social_linkedin
        "facebook" -> R.drawable.social_facebook
        "instagram" -> R.drawable.social_instagram
        "github" -> R.drawable.social_github
        else -> R.drawable.social_site
    }


}
