package io.swiftfest.www.swiftfest.data

import android.content.Context
import android.os.Handler
import android.util.Log.e
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.swiftfest.www.swiftfest.MyApplication.Companion.SCHEDULE_URL
import io.swiftfest.www.swiftfest.MyApplication.Companion.SESSION_URL
import io.swiftfest.www.swiftfest.MyApplication.Companion.SPEAKER_URL
import io.swiftfest.www.swiftfest.MyApplication.Companion.VOLUNTEER_URL
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.*
import io.swiftfest.www.swiftfest.utils.isNullorEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import kotlinx.coroutines.*

class DataProvider private constructor() {

    lateinit var speakers: List<Speaker>
    lateinit var sessions: List<Session>
    lateinit var schedules: List<Schedule>
    lateinit var volunteers: List<Volunteer>
    lateinit var faqs: List<FaqItem>

    lateinit var speakerMap: Map<Int, Speaker>
    lateinit var sessionMap: Map<Int, Session>

    private val HTTP_TIMEOUT: Int = 5000

    private object Holder {
        val INSTANCE = DataProvider()
    }

    private fun loadResourceFile(context: Context, resId: Int): String {
        val in_s = context.resources.openRawResource(resId)
        val b = ByteArray(in_s.available())
        in_s.read(b)
        return String(b)
    }

    fun loadSpeakers(context: Context) {
        val (request, response, result) = SPEAKER_URL.httpGet().timeout(HTTP_TIMEOUT).responseString()
        val speakerListType = object : TypeToken<List<Speaker>>() {}.type
        var data = ""
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                e("getSpeakers", ex.toString())
            }
            is Result.Success -> {
                data = result.get()
            }
        }

        try {
            speakers = Gson().fromJson<List<Speaker>>(data, speakerListType)
        } catch (err: Exception) {
            e("parseSpeakers", err.toString())
            speakers = Gson().fromJson<List<Speaker>>(loadResourceFile(context, R.raw.speakers), speakerListType)
        }

        setupSpeakerMap()
    }

    fun loadSchedules(context: Context) {
        val (request, response, result) = SCHEDULE_URL.httpGet().timeout(HTTP_TIMEOUT).responseString()
        val scheduleListType = object : TypeToken<List<Schedule>>() {}.type
        var data = ""
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                e("getSchedule", ex.toString())
            }
            is Result.Success -> {
                data = result.get()
            }
        }
        try {
            schedules = Gson().fromJson<List<Schedule>>(data, scheduleListType)
        } catch (err: Exception) {
            e("parseSchedules", err.toString())
            schedules = Gson().fromJson<List<Schedule>>(loadResourceFile(context, R.raw.schedule), scheduleListType)
        }
    }

    fun loadSessions(context: Context) {
        val (request, response, result) = SESSION_URL.httpGet().timeout(HTTP_TIMEOUT).responseString()
        val sessionListType = object : TypeToken<List<Session>>() {}.type
        var data = ""
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                e("getSessions", ex.toString())
            }
            is Result.Success -> {
                data = result.get()
            }
        }
        try {
            sessions = Gson().fromJson<List<Session>>(data, sessionListType)
        } catch (err: Exception) {
            e("parseSessions", err.toString())
            sessions = Gson().fromJson<List<Session>>(loadResourceFile(context, R.raw.sessions), sessionListType)
        }

        sessions = sessions.filter { !it.title.isNullorEmpty() && it.title.isNotBlank() }
        setupSessionMap()
    }

    fun loadVolunteers(context: Context) {
        val (request, response, result) = VOLUNTEER_URL.httpGet().timeout(HTTP_TIMEOUT).responseString()
        val volunteersListType = object : TypeToken<List<Volunteer>>() {}.type
        var data = ""
        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                e("getVolunteers", ex.toString())
            }
            is Result.Success -> {
                data = result.get()
            }
        }
        try {
            volunteers = Gson().fromJson<List<Volunteer>>(data, volunteersListType)
        } catch (err: Exception) {
            e("parseVolunteers", err.toString())
            volunteers = Gson().fromJson<List<Volunteer>>(loadResourceFile(context, R.raw.volunteers), volunteersListType)
        }
    }

    fun loadFaqs(context: Context) {
        val faqListType = object : TypeToken<List<FaqItem>>() {}.type
        faqs = Gson().fromJson<List<FaqItem>>(loadResourceFile(context, R.raw.faq), faqListType)
    }

    private fun setupSpeakerMap() {
        speakerMap = speakers.map { it.id to it }.toMap()
    }

    private fun setupSessionMap() {
        sessionMap = sessions.map { it.id to it }.toMap()
    }

    fun toScheduleRow(timeSlot: Timeslot, sessionId: Int, sessionDate: String): ScheduleRow {
        
        val session = sessionMap[sessionId]
        val scheduleRow = ScheduleRow(primarySpeakerId = 0)

        session?.let {
            scheduleRow.subtype = session.subtype
            scheduleRow.startTime = timeSlot.startTime
            scheduleRow.endTime = timeSlot.endTime
            // Assume that the speaker exists (error if not).
            val sessionSpeakers: List<Speaker>
            if (session.speakers != null && session.speakers.isNotEmpty()) {

                sessionSpeakers = session.speakers.map { speakerMap[it] ?: error("No speaker for this slot") }
                scheduleRow.speakerCount = sessionSpeakers.size
                scheduleRow.speakerIds = sessionSpeakers.map { it.id }
                scheduleRow.speakerNames = sessionSpeakers.map { it.fullName }
                scheduleRow.primarySpeakerName = scheduleRow.speakerNames[0]
                scheduleRow.primarySpeakerId = scheduleRow.speakerIds[0]
                scheduleRow.photoUrlMap = sessionSpeakers.map { it.fullName to it.getFullThumbnailUrl() }.toMap()
                scheduleRow.speakerNameToOrgName = sessionSpeakers.map { it.fullName to it.company }.toMap()
            }
            scheduleRow.date = sessionDate
            if (session.place.isNullOrBlank()) {
                scheduleRow.room = "Location TBD"
            } else {
                scheduleRow.room = session.place!!
            }
            scheduleRow.id = sessionId.toString()
            scheduleRow.talkDescription = session.description ?: "No description."
            scheduleRow.talkTitle = session.title
            val now = LocalDateTime.now()
            scheduleRow.isOver = now.isAfter(scheduleRow.getEndDate())
        }
//        scheduleRow.trackSortOrder // TODO: assign
        return scheduleRow
    }

    suspend fun fetchAppData(context: Context) {
        val handler = Handler(context.mainLooper)
        handler.postDelayed({
            Toast.makeText(context, "Having trouble connecting", Toast.LENGTH_LONG).show()
        }, 10000)
        val sessionTask = GlobalScope.async { instance.loadSessions(context) }
        val scheduleTask = GlobalScope.async { instance.loadSchedules(context) }
        val speakerTask = GlobalScope.async { instance.loadSpeakers(context) }
        val volunteerTask = GlobalScope.async { instance.loadVolunteers(context) }
        val faqTask = GlobalScope.async { instance.loadFaqs(context) }
        sessionTask.await()
        scheduleTask.await()
        speakerTask.await()
        volunteerTask.await()
        faqTask.await()

        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        val instance: DataProvider by lazy { Holder.INSTANCE }
    }
}

fun <K, V> Map<K, V>.getOrRefetchData(context: Context, key: K): V? {
    if (this.containsKey(key)) {
        return this.get(key)
    }

    // Refetch data from server.
    val pDialog = MaterialDialog.Builder(context)
            .title("Loading...")
            .content(R.string.fetching_data)
            .progressIndeterminateStyle(true)
            .progress(true, 0)
            .show()

    GlobalScope.launch(Dispatchers.Default) {
        val start = System.currentTimeMillis()
        DataProvider.instance.fetchAppData(context)
        // Make sure dialog shows for at least 1.5s.
        val needed = 1500 - (System.currentTimeMillis() - start)
        if (needed > 0) {
            Thread.sleep(needed)
        }
        launch(Dispatchers.Main) {
            pDialog.dismiss()
        }
    }

    return null
}
