package io.swiftfest.www.swiftfest.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R.layout
import io.swiftfest.www.swiftfest.data.model.FaqItem
import io.swiftfest.www.swiftfest.views.faq.FaqAdapterItem
import io.swiftfest.www.swiftfest.views.faq.FaqAdapterItemHeader
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.swiftfest.www.swiftfest.data.DataProvider
import kotlinx.android.synthetic.main.faq_fragment.faq_recycler
import java.util.ArrayList
import java.util.HashMap


class FAQFragment : Fragment(), FlexibleAdapter.OnItemClickListener {

    private lateinit var headerAdapter: FlexibleAdapter<FaqAdapterItem>
    private val dataProvider = DataProvider.instance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layout.faq_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderAdapter(dataProvider.faqs)
    }

    private fun setupHeaderAdapter(faqs: List<FaqItem>) {
        val questionHeaders = HashMap<String, FaqAdapterItemHeader>()
        val items = ArrayList<FaqAdapterItem>(faqs.size)
        faqs.forEach { faq ->
            faq.answers.forEach { answer ->
                val header: FaqAdapterItemHeader = questionHeaders[faq.question] ?: FaqAdapterItemHeader(faq.question)
                questionHeaders[faq.question] = header

                val item = FaqAdapterItem(answer, header)
                items.add(item)
            }
        }

        faq_recycler.layoutManager = LinearLayoutManager(faq_recycler.context)
        headerAdapter = FlexibleAdapter(items)
        headerAdapter.addListener(this)
        faq_recycler.adapter = headerAdapter
        headerAdapter.expandItemsAtStartUp()
                .setDisplayHeadersAtStartUp(true)
    }

    override fun onItemClick(view: View, position: Int): Boolean {
        if (headerAdapter.getItem(position) is FaqAdapterItem) {
            val item = headerAdapter.getItem(position)
            val itemData = item!!.itemData

            val url = if (!TextUtils.isEmpty(itemData.otherLink)) itemData.otherLink else itemData.mapLink
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            if (faq_recycler.context.packageManager.queryIntentActivities(intent, 0).size > 0) {
                startActivity(intent)
                return false
            }
        }

        return true
    }

}
