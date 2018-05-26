package io.swiftfest.www.swiftfest.data

import android.content.Context
import android.util.Log.e
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

class DataProvider private constructor() {

    lateinit var speakers: List<Speaker>
    lateinit var sessions: List<Session>
    lateinit var schedules: List<Schedule>
    lateinit var volunteers: List<Volunteer>
    lateinit var faqs: List<FaqItem>

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
        val (request, response, result) = SPEAKER_URL.httpGet().responseString()
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
            val repl = "{\"name\":\"twitter\",\"link\":\"https://twitter.com/thedevme\"}"
            data = data.replace(repl, "[${repl}]")
            speakers = Gson().fromJson<List<Speaker>>(data, speakerListType)
        } catch (err: Exception) {
            e("parseSpeakers", err.toString())
            speakers = Gson().fromJson<List<Speaker>>(loadResourceFile(context, R.raw.speakers), speakerListType)
        }

        setupSpeakerMap()
    }

    fun loadSchedules(context: Context) {
        val (request, response, result) = SCHEDULE_URL.httpGet().responseString()
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
        val (request, response, result) = SESSION_URL.httpGet().responseString()
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
        setupSessionMap()
    }

    fun loadVolunteers(context: Context) {
        val (request, response, result) = VOLUNTEER_URL.httpGet().responseString()
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

    lateinit var speakerMap: Map<Int, Speaker>

    private fun setupSpeakerMap() {
        speakerMap = speakers.map { it.id to it }.toMap()
    }

    lateinit var sessionMap: Map<Int, Session>

    private fun setupSessionMap() {
        sessionMap = sessions.map { it.id to it }.toMap()
    }

    fun toScheduleRow(timeSlot: Timeslot, sessionId: Int, sessionDate: String): ScheduleRow {
        val session = sessionMap.get(sessionId)!!
        val scheduleRow = ScheduleRow(primarySpeakerId = 0)
        scheduleRow.startTime = timeSlot.startTime
        scheduleRow.endTime = timeSlot.endTime
        // Assume that the speaker exists (error if not).
        val sessionSpeakers: List<Speaker>
        if (session.speakers != null && session.speakers.isNotEmpty()) {
            sessionSpeakers = session.speakers.map { speakerMap.get(it)!! }
            scheduleRow.speakerCount = sessionSpeakers.size
            scheduleRow.speakerIds = sessionSpeakers.map { it.id }
            scheduleRow.speakerNames = sessionSpeakers.map { it.name }
            scheduleRow.primarySpeakerName = scheduleRow.speakerNames.get(0)
            scheduleRow.primarySpeakerId = scheduleRow.speakerIds.get(0)
            scheduleRow.photoUrlMap = sessionSpeakers.map { it.name to it.getFullThumbnailUrl() }.toMap()
            scheduleRow.speakerNameToOrgName = sessionSpeakers.map { it.name to it.company }.toMap()
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
        scheduleRow.isOver = false // TODO: implement isOver based on current time relative to end time.
//        scheduleRow.trackSortOrder // TODO: assign
        return scheduleRow
    }

    companion object {
        val instance: DataProvider by lazy { Holder.INSTANCE }
    }
}
