package io.swiftfest.www.swiftfest.views.agenda

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.UserAgendaRepo
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.swiftfest.www.swiftfest.data.model.ScheduleRow

/**
 * Used for displaying the schedule with sticky headers with optional day filtering
 */
class ScheduleAdapterItem internal constructor(val itemData: ScheduleRow,
                                               header: ScheduleAdapterItemHeader) :
        AbstractSectionableItem<ScheduleAdapterItem.ViewHolder, ScheduleAdapterItemHeader>(header) {

    var roomSortOrder = itemData.trackSortOrder

    val title: String
        get() = itemData.talkTitle

    override fun equals(other: Any?): Boolean {
        if (other is ScheduleAdapterItem) {
            val inItem = other as ScheduleAdapterItem?
            return this.itemData.talkTitle == inItem?.itemData?.talkTitle
        }
        return false
    }

    override fun hashCode(): Int {
        return itemData.talkTitle.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.schedule_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ScheduleAdapterItem.ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                holder: ViewHolder,
                                position: Int,
                                payloads: MutableList<Any>) {

        val userAgendaRepo = UserAgendaRepo.getInstance(holder.bookmarkIndicator.context)
        if (itemData.speakerNames.isEmpty()) {
            // For "Lunch" and "Registration" Sessions
            holder.avatarLayout.visibility = View.GONE
            holder.speaker.visibility = View.GONE
            holder.time.visibility = View.GONE

            holder.bookmarkIndicator.visibility = if (userAgendaRepo.isSessionBookmarked(itemData.id))
                View.VISIBLE
            else
                View.INVISIBLE
            holder.sessionLayout.visibility = View.VISIBLE
            holder.title.text = itemData.talkTitle
            holder.room.text = itemData.room

            if (itemData.photoUrlMap.size == 0) {
                holder.rootLayout.background = null
            } else {
                addBackgroundRipple(holder)
            }

        } else {
            // For normal talks/sessions with speakers
            holder.avatarLayout.visibility = View.VISIBLE
            holder.speaker.visibility = View.VISIBLE
            holder.time.visibility = View.VISIBLE

            holder.title.text = itemData.talkTitle
            holder.time.text = String.format("%s - %s",
                    itemData.getReadableStartTime(), itemData.getReadableEndTime())
            holder.speaker.text = itemData.speakerNames.joinToString(separator = ", ")
            holder.room.text = itemData.room

            holder.speakerCount.visibility = if (itemData.speakerCount > 1) View.VISIBLE else View.GONE
            holder.speakerCount.text = String.format("+%d", itemData.speakerCount - 1)

            val context = holder.title.context

            Glide.with(context).load(itemData.photoUrlMap[itemData.primarySpeakerName])
                    .transform(CircleTransform(context))
                    .placeholder(R.drawable.icon_circle)
                    .crossFade()
                    .into(holder.avatar)

            holder.bookmarkIndicator.visibility = if (userAgendaRepo.isSessionBookmarked(itemData.id))
                View.VISIBLE
            else
                View.INVISIBLE

            addBackgroundRipple(holder)
        }

        val color = ContextCompat.getColor(
                holder.availableIndicator.context, itemData.getItemColor())

        holder.availableIndicator.setBackgroundColor(color)
        holder.time.setTextColor(color)
    }

    private fun addBackgroundRipple(holder: ViewHolder) {
        val outValue = TypedValue()
        val context = holder.title.context
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        holder.rootLayout.setBackgroundResource(outValue.resourceId)
    }


    class ViewHolder : FlexibleViewHolder {

        lateinit var rootLayout: View

        lateinit var availableIndicator: ImageView

        lateinit var bookmarkIndicator: ImageView

        lateinit var avatar: ImageView

        lateinit var avatarLayout: View

        lateinit var title: TextView

        lateinit var time: TextView

        lateinit var speaker: TextView

        lateinit var speakerCount: TextView

        lateinit var room: TextView

        lateinit var sessionLayout: View

        constructor(view: View, adapter: FlexibleAdapter<*>) : super(view, adapter) {

            findViews(view)
        }

        constructor(view: View, adapter: FlexibleAdapter<*>, stickyHeader: Boolean) : super(view, adapter, stickyHeader) {

            findViews(view)
        }

        private fun findViews(parent: View) {
            rootLayout = parent.findViewById(R.id.scheduleRootLayout)
            availableIndicator = parent.findViewById(R.id.available_indicator)
            bookmarkIndicator = parent.findViewById(R.id.bookmark_indicator)
            avatar = parent.findViewById(R.id.speaker_image)
            avatarLayout = parent.findViewById(R.id.avatar_layout)
            title = parent.findViewById(R.id.title_text)
            time = parent.findViewById(R.id.time_text)
            speaker = parent.findViewById(R.id.speaker_name_text)
            speakerCount = parent.findViewById(R.id.speaker_count)
            room = parent.findViewById(R.id.room_text)
            sessionLayout = parent.findViewById(R.id.session_layout)
        }
    }
}
