package io.swiftfest.www.swiftfest.views.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.R.string
import io.swiftfest.www.swiftfest.data.DataProvider
import io.swiftfest.www.swiftfest.data.model.Speaker
import io.swiftfest.www.swiftfest.utils.Constants
import io.swiftfest.www.swiftfest.data.model.ScheduleDetail
import io.swiftfest.www.swiftfest.data.model.ScheduleRow
import io.swiftfest.www.swiftfest.data.UserAgendaRepo
import io.swiftfest.www.swiftfest.utils.NotificationUtils
import io.swiftfest.www.swiftfest.utils.ServiceLocator.Companion.gson
import io.swiftfest.www.swiftfest.utils.getHtmlFormattedSpanned
import io.swiftfest.www.swiftfest.views.MainActivity
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import kotlinx.android.synthetic.main.agenda_detail_fragment.agendaDetailView
import kotlinx.android.synthetic.main.agenda_detail_fragment.fab_agenda_detail_bookmark
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_description
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_room
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_speaker_name
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_speaker_title
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_time
import kotlinx.android.synthetic.main.agenda_detail_fragment.tv_agenda_detail_title
import kotlinx.android.synthetic.main.agenda_detail_fragment.v_agenda_detail_speaker_divider


class AgendaDetailFragment : Fragment() {

    private val dataProvider = DataProvider.instance

    private lateinit var scheduleRowItem: ScheduleRow
    private lateinit var scheduleDetail: ScheduleDetail

    private val userAgendaRepo: UserAgendaRepo
        get() = UserAgendaRepo.getInstance(fab_agenda_detail_bookmark.context)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.agenda_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheduleRowString = arguments!!.getString(Constants.SCHEDULE_ITEM_ROW)
        scheduleRowItem = gson.fromJson(scheduleRowString, ScheduleRow::class.java)
        fetchAgendaDetailData()
        populateView()

        if (activity is MainActivity) {
            val mainActivity = activity as MainActivity
            mainActivity.uncheckAllMenuItems()
        }
    }

    private fun populateView() {
        tv_agenda_detail_title.text = scheduleRowItem.talkTitle
        tv_agenda_detail_room.text = resources.getString(R.string.str_agenda_detail_room, scheduleRowItem.room)
        tv_agenda_detail_time.text = resources.getString(R.string.str_agenda_detail_time, scheduleRowItem.startTime, scheduleRowItem.endTime)

        fab_agenda_detail_bookmark.setOnClickListener({

            val nextBookmarkStatus = !userAgendaRepo.isSessionBookmarked(scheduleDetail.id)
            userAgendaRepo.bookmarkSession(scheduleDetail.id, nextBookmarkStatus)
            val context = tv_agenda_detail_title.context
            if (nextBookmarkStatus) {
                NotificationUtils(context).scheduleMySessionNotifications()
            } else {
                NotificationUtils(context).cancelNotificationAlarm(scheduleRowItem.id)
            }

            Snackbar.make(agendaDetailView,
                    if (nextBookmarkStatus)
                        getString(R.string.saved_agenda_item)
                    else getString(R.string.removed_agenda_item),
                    Snackbar.LENGTH_SHORT).show()

            showBookmarkStatus(scheduleDetail)
        })

        populateSpeakersInformation(scheduleRowItem)
    }

    private fun fetchAgendaDetailData() {
        scheduleDetail = ScheduleDetail(scheduleRowItem)
        val primarySpeaker = dataProvider.speakerMap.get(scheduleRowItem.primarySpeakerId)
        if (primarySpeaker != null) {
            scheduleDetail.speakerBio = primarySpeaker.bio ?: ""
            scheduleDetail.facebook = primarySpeaker.socialProfiles.get("facebook") ?: ""
            scheduleDetail.linkedIn = primarySpeaker.socialProfiles.get("linkedin") ?: ""
            scheduleDetail.twitter = primarySpeaker.socialProfiles.get("twitter") ?: ""
        }

        showAgendaDetail(scheduleDetail)
    }

    private fun populateSpeakersInformation(itemData: ScheduleRow) = when {
        itemData.speakerNames.isEmpty() -> {
            tv_agenda_detail_speaker_name.visibility = View.GONE
            v_agenda_detail_speaker_divider.visibility = View.GONE
        }
        else -> {
            var speakerNames = ""
            val imgViewSize = resources.getDimension(R.dimen.imgv_speaker_size).toInt()
            var marginValue = resources.getDimension(R.dimen.def_margin).toInt()
            val offsetImgView = resources.getDimension(R.dimen.imgv_speaker_offset).toInt()
            val defaultLeftMargin = resources.getDimension(R.dimen.def_margin).toInt()

            itemData.speakerNames.mapIndexed { index, speakerName ->
                val orgName = itemData.speakerNameToOrgName[speakerName]
                // append company name to speaker name
                speakerNames += "${speakerName} - ${orgName ?: ""}"

                if (itemData.speakerNames.size > 1) {
                    tv_agenda_detail_speaker_title.text = getString(string.header_speakers)

                    // if the current speaker name is not the last then add a line break
                    if (speakerName != itemData.speakerNames.last()) {
                        speakerNames += "\n"
                    }
                } else {
                    tv_agenda_detail_speaker_title.text = getString(string.header_speaker)
                }


                // Add an imageview to the relative layout
                val tempImg = ImageView(activity)
                val lp = RelativeLayout.LayoutParams(imgViewSize, imgViewSize)
                if (speakerName == itemData.speakerNames.first()) {
                    lp.setMargins(marginValue, 0, 0, defaultLeftMargin)
                } else {
                    marginValue += offsetImgView
                    lp.setMargins(marginValue, 0, 0, defaultLeftMargin)
                }

                // add the imageview above the textview for room data
                lp.addRule(RelativeLayout.ABOVE, tv_agenda_detail_room.id)
                tempImg.layoutParams = lp

                // add speakerName as a child to the relative layout
                agendaDetailView.addView(tempImg)

                Glide.with(this)
                        .load(itemData.photoUrlMap[speakerName])
                        .transform(CircleTransform(tempImg.context))
                        .placeholder(R.drawable.emo_im_cool)
                        .crossFade()
                        .into(tempImg)

                tempImg.setOnClickListener { _ ->
                    val eventSpeaker = dataProvider.speakerMap.get(itemData.speakerIds[index])
                    val arguments = Bundle()

                    arguments.putString(Speaker.SPEAKER_ITEM_ROW, gson.toJson(eventSpeaker, Speaker::class.java))

                    val speakerDetailFragment = SpeakerDetailFragment()
                    speakerDetailFragment.arguments = arguments

                    val fragmentManager = activity?.supportFragmentManager
                    fragmentManager?.beginTransaction()
                            ?.add(R.id.fragment_container, speakerDetailFragment)
                            ?.addToBackStack(null)
                            ?.commit()
                }
            }
            tv_agenda_detail_speaker_name.text = speakerNames
        }
    }

    fun showAgendaDetail(scheduleDetail: ScheduleDetail) {
        populateSpeakersInformation(scheduleDetail.listRow)
        showBookmarkStatus(scheduleDetail)

        tv_agenda_detail_title.text = scheduleDetail.listRow.talkTitle
        tv_agenda_detail_description.text = scheduleDetail.listRow.talkDescription.getHtmlFormattedSpanned()

        tv_agenda_detail_description.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showBookmarkStatus(scheduleDetail: ScheduleDetail) {
        val userAgendaRepo = userAgendaRepo
        val context = fab_agenda_detail_bookmark.context
        fab_agenda_detail_bookmark.backgroundTintList = if (userAgendaRepo.isSessionBookmarked(scheduleDetail.id))
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
        else
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorLightGray))
    }

    companion object {
        fun addDetailFragmentToStack(supportFragmentManager: FragmentManager, itemData: ScheduleRow) {
            val arguments = Bundle()
            arguments.putString(Constants.SCHEDULE_ITEM_ROW, gson.toJson(itemData, ScheduleRow::class.java))

            val agendaDetailFragment = AgendaDetailFragment()
            agendaDetailFragment.arguments = arguments

            supportFragmentManager.beginTransaction()
                    ?.add(R.id.fragment_container, agendaDetailFragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }
}
