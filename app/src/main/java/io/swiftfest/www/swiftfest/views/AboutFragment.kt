package io.swiftfest.www.swiftfest.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.utils.getHtmlFormattedSpanned
import kotlinx.android.synthetic.main.about_fragment.*

class AboutFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAboutData()
    }

    private fun fetchAboutData() {
        tv_about_description.text = getString(R.string.about_swiftfest).getHtmlFormattedSpanned()
    }
}
