package io.swiftfest.www.swiftfest.views


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.swiftfest.www.swiftfest.R

class CocFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.coc_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchCocData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

//    val dataListener: ValueEventListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            tv_coc.text = dataSnapshot.getValue(String::class.java)?.getHtmlFormattedSpanned()
//
//            tv_coc.movementMethod = LinkMovementMethod.getInstance()
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            Log.e(javaClass.canonicalName, "onCancelled", databaseError.toException())
//        }
//    }

    private fun fetchCocData() {
        // TODO: fetch COC (Code of Conduct) data.
    }
}