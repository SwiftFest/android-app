package io.swiftfest.www.swiftfest.views.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.ConferenceDatabase.Speaker
import io.swiftfest.www.swiftfest.utils.ServiceLocator.Companion.gson
import io.swiftfest.www.swiftfest.utils.getHtmlFormattedSpanned
import io.swiftfest.www.swiftfest.utils.loadUriInCustomTab
import io.swiftfest.www.swiftfest.views.MainActivity
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import kotlinx.android.synthetic.main.speaker_detail_fragment.imgv_linkedin
import kotlinx.android.synthetic.main.speaker_detail_fragment.imgv_speaker_detail_avatar
import kotlinx.android.synthetic.main.speaker_detail_fragment.imgv_twitter
import kotlinx.android.synthetic.main.speaker_detail_fragment.tv_speaker_detail_description
import kotlinx.android.synthetic.main.speaker_detail_fragment.tv_speaker_detail_designation
import kotlinx.android.synthetic.main.speaker_detail_fragment.tv_speaker_detail_name


class SpeakerDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.speaker_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemData = gson.fromJson(arguments!!.getString(Speaker.SPEAKER_ITEM_ROW), Speaker::class.java)
        populateView(itemData)

        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
            mainActivity.uncheckAllMenuItems()
        }
    }

    private fun populateView(itemData: Speaker) {
        tv_speaker_detail_name.text = itemData.name
        tv_speaker_detail_designation.text = String.format("%s \n@ %s", itemData.title, itemData.company)
        itemData.bio?.let {
            tv_speaker_detail_description.text = it.getHtmlFormattedSpanned()
        }

        tv_speaker_detail_description.movementMethod = LinkMovementMethod.getInstance()

        val twitterHandle = itemData.socialProfiles?.get("twitter")
        if (!twitterHandle.isNullOrEmpty()) {
            imgv_twitter.setOnClickListener({
                activity?.loadUriInCustomTab(String.format("%s%s", resources.getString(R.string.twitter_link), twitterHandle))
            })
        } else {
            imgv_twitter.visibility = View.GONE
        }


        val linkedinHandle = itemData.socialProfiles?.get("linkedIn")
        if (!linkedinHandle.isNullOrEmpty()) {
            imgv_linkedin.setOnClickListener({
                activity?.loadUriInCustomTab(String.format("%s%s", resources.getString(R.string.linkedin_profile_link), linkedinHandle))
            })
        } else {
            imgv_linkedin.visibility = View.GONE
        }

        Glide.with(activity)
                .load(itemData.thumbnailUrl)
                .transform(CircleTransform(imgv_speaker_detail_avatar.context))
                .placeholder(R.drawable.emo_im_cool)
                .crossFade()
                .into(imgv_speaker_detail_avatar)

    }
}
