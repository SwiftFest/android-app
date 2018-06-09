package io.swiftfest.www.swiftfest.views.speaker

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.Speaker
import io.swiftfest.www.swiftfest.utils.getHtmlFormattedSpanned
import io.swiftfest.www.swiftfest.views.transform.CircleTransform
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Used for displaying speaker list items on the all speakers "Speakers" page.
 */
class SpeakerAdapterItem internal constructor(val itemData: Speaker) :
        AbstractFlexibleItem<SpeakerAdapterItem.ViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?,
                                holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {

        holder.name.text = itemData.name
        itemData.bio?.let {
            holder.bio.text = it.getHtmlFormattedSpanned()
        }

        val context = holder.name.context

        Glide.with(context)
                .load(itemData.getFullThumbnailUrl())
                .transform(CircleTransform(context))
                .placeholder(R.drawable.icon_circle)
                .crossFade()
                .into(holder.avatar)
    }

    override fun equals(other: Any?): Boolean {
        if (other is SpeakerAdapterItem) {
            val inItem = other as SpeakerAdapterItem?
            return this.itemData.name == inItem?.itemData?.name
        }
        return false
    }

    override fun hashCode(): Int {
        return itemData.name.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.speaker_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return SpeakerAdapterItem.ViewHolder(view, adapter)
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
            rootLayout = parent.findViewById(R.id.speakerRootLayout)
            avatar = parent.findViewById(R.id.speaker_image)
            avatarLayout = parent.findViewById(R.id.avatar_layout)
            bio = parent.findViewById(R.id.bio_text)
            name = parent.findViewById(R.id.name_text)
        }
    }
}
