package io.swiftfest.www.swiftfest.views.speaker


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.DataProvider
import io.swiftfest.www.swiftfest.data.model.Speaker
import io.swiftfest.www.swiftfest.utils.ServiceLocator.Companion.gson
import io.swiftfest.www.swiftfest.views.detail.SpeakerDetailFragment
import kotlinx.android.synthetic.main.speaker_fragment.*


class SpeakerFragment : Fragment(), FlexibleAdapter.OnItemClickListener {

    private lateinit var speakerAdapter: FlexibleAdapter<SpeakerAdapterItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.speaker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchSpeakerData()
    }

    private fun fetchSpeakerData() {
        val items = DataProvider.instance.speakers.map { SpeakerAdapterItem(it) }
        speaker_recycler.layoutManager = LinearLayoutManager(speaker_recycler.context)
        speakerAdapter = FlexibleAdapter(items)
        speakerAdapter.addListener(this)
        speaker_recycler.adapter = speakerAdapter
        speaker_recycler.addItemDecoration(FlexibleItemDecoration(speaker_recycler.context).withDefaultDivider())
        speakerAdapter.expandItemsAtStartUp().setDisplayHeadersAtStartUp(false)
    }

    override fun onItemClick(view: View, position: Int): Boolean {

        if (speakerAdapter.getItem(position) is SpeakerAdapterItem) {
            val item = speakerAdapter.getItem(position)
            val itemData = item?.itemData

            val arguments = Bundle()

            arguments.putString(Speaker.SPEAKER_ITEM_ROW, gson.toJson(itemData, Speaker::class.java))

            val speakerDetailFragment = SpeakerDetailFragment()
            speakerDetailFragment.arguments = arguments

            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.beginTransaction()
                    ?.add(R.id.fragment_container, speakerDetailFragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }

        return true
    }

}
