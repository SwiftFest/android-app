package io.swiftfest.www.swiftfest.views


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import io.swiftfest.www.swiftfest.R

class CocFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // TODO: determine if header can be removed from the embedded web view.
        val view = inflater.inflate(R.layout.coc_fragment, container, false)
        val webView = view.findViewById(R.id.webView) as WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.loadUrl("http://swiftfest.io/code-of-conduct/");
        return view
    }

}