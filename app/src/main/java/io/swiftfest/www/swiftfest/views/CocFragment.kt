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
        val view = inflater.inflate(R.layout.coc_fragment, container, false)
        val webView = view.findViewById(R.id.webView) as WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = false
//        webView.loadUrl("http://swiftfest.io/code-of-conduct/");
        webView.loadUrl("file:///android_res/raw/coc.html");
        return view
    }

}