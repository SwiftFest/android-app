package io.swiftfest.www.swiftfest.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R

class AboutFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAboutData()
    }

//    val dataListener: ValueEventListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            tv_about_description.text = dataSnapshot.getValue(String::class.java)?.getHtmlFormattedSpanned()
//
//            tv_about_description.movementMethod = LinkMovementMethod.getInstance()
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.e(javaClass.canonicalName, "onCancelled", databaseError.toException())
//        }
//    }

    private fun fetchAboutData() {
        // TODO: fetch about information.
    }
}
