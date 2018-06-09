package io.swiftfest.www.swiftfest.views.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.Speaker
import io.swiftfest.www.swiftfest.utils.ServiceLocator.Companion.gson
import io.swiftfest.www.swiftfest.utils.getHtmlFormattedSpanned
import io.swiftfest.www.swiftfest.utils.loadUriInCustomTab
import io.swiftfest.www.swiftfest.views.MainActivity
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import kotlinx.android.synthetic.main.speaker_detail_fragment.*


class SpeakerDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.speaker_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val speakerRow = arguments!!.getString(Speaker.SPEAKER_ITEM_ROW)
        val itemData = gson.fromJson(speakerRow, Speaker::class.java)
        populateView(itemData)

        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
            mainActivity.uncheckAllMenuItems()
        }
    }

    private fun loadHandle(itemData: Speaker, key: String, view: View) {
        val handle = itemData.socialProfiles.get(key)?:""
        if (!handle.isEmpty()) {
            view.setOnClickListener({
                activity?.loadUriInCustomTab(String.format("%s", handle))
            })
        } else {
            view.visibility = View.GONE
        }
    }

    private fun populateView(itemData: Speaker) {
        tv_speaker_detail_name.text = itemData.name
        val designationText = StringBuilder()
        if (!itemData.title.isNullOrBlank()) {
           designationText.append(itemData.title)
        }
        if (!itemData.company.isNullOrBlank()) {
            designationText.append("\n@ ").append(itemData.company)
        }

        tv_speaker_detail_designation.text = designationText.toString()

        itemData.bio?.let {
            tv_speaker_detail_description.text = it.getHtmlFormattedSpanned()
        }

        tv_speaker_detail_description.movementMethod = LinkMovementMethod.getInstance()

        loadHandle(itemData, "twitter", imgv_twitter)
        loadHandle(itemData, "facebook", imgv_facebook)
        loadHandle(itemData, "linkedin", imgv_linkedin)
        loadHandle(itemData, "site", imgv_site)

        Glide.with(activity)
                .load(itemData.getFullThumbnailUrl())
                .transform(CircleTransform(imgv_speaker_detail_avatar.context))
                .placeholder(R.drawable.icon_circle)
                .crossFade()
                .into(imgv_speaker_detail_avatar)

    }
}
