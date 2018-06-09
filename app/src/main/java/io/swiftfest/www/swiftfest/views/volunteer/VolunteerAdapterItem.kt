package io.swiftfest.www.swiftfest.views.volunteer

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.Social
import io.swiftfest.www.swiftfest.data.model.Volunteer
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import io.swiftfest.www.swiftfest.views.volunteer.VolunteerAdapterItem.ViewHolder

/**
 * Used for displaying volunteer list items on the all volunteers "volunteers" page.
 */
class VolunteerAdapterItem internal constructor(val itemData: Volunteer) :
        AbstractFlexibleItem<ViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        val bodyText = StringBuilder()
        itemData.social.map {
            val simpleLink = extractLink(it)
            if (simpleLink.isNotBlank()) {
                val text = "${it.name.capitalize()}: ${simpleLink}"
                bodyText.append(text).append("\n")
            }
        }

        holder.name.text = String.format("%s %s", itemData.name, itemData.surname)
        holder.bio.text = bodyText

        val context = holder.name.context

        Glide.with(context)
                .load(itemData.getFullThumbnailUrl())
                .transform(CircleTransform(context))
                .placeholder(R.drawable.icon_circle)
                .crossFade()
                .into(holder.avatar)
    }

    private fun extractLink(it: Social): String {
        if (it.name.equals("site")) {
            return it.link
        }

        val linkTokens = it.link.split("/")
        val link = linkTokens[linkTokens.size - 1]
        if (link.isBlank()) {
            if (linkTokens.size > 1) {
                return linkTokens[linkTokens.size - 2]
            }
        }
        return link
    }

    override fun equals(other: Any?): Boolean {
        if (other is VolunteerAdapterItem) {
            val inItem = other as VolunteerAdapterItem?
            return this.itemData.name == inItem?.itemData?.name
        }
        return false
    }

    override fun hashCode(): Int {
        return itemData.name.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.volunteer_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder : FlexibleViewHolder {

        private lateinit var rootLayout: View
        lateinit var avatar: ImageView
        private lateinit var avatarLayout: View
        lateinit var name: TextView
        lateinit var bio: TextView

        constructor(view: View, adapter: FlexibleAdapter<*>) : super(view, adapter) {
            findViews(view)
        }

        constructor(view: View, adapter: FlexibleAdapter<*>, stickyHeader: Boolean) : super(view, adapter, stickyHeader) {
            findViews(view)
        }

        private fun findViews(parent: View) {
            rootLayout = parent.findViewById(R.id.volunteerRootLayout)
            avatar = parent.findViewById(R.id.volunteer_image)
            avatarLayout = parent.findViewById(R.id.avatar_layout)
            bio = parent.findViewById(R.id.bio_text)
            name = parent.findViewById(R.id.name_text)
        }
    }
}
