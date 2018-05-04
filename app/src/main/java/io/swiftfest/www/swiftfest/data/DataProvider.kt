package io.swiftfest.www.swiftfest.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.swiftfest.www.swiftfest.R
import io.swiftfest.www.swiftfest.data.model.*

class DataProvider private constructor() {

    lateinit var speakers: List<Speaker>
    lateinit var sessions: List<Session>
    lateinit var schedules: List<Schedule>

    private object Holder {
        val INSTANCE = DataProvider()
    }

    private fun loadResourceFile(context: Context, resId: Int): String {
        val in_s = context.resources.openRawResource(resId)
        val b = ByteArray(in_s.available())
        in_s.read(b)
        return String(b)
    }

    /*
     * TODO: potentially replace with http get calls (and cache the result) rather than using local files.
     */
    fun loadData(context: Context) {
        try {
            val speakerListType = object : TypeToken<List<Speaker>>() {}.type
            speakers = Gson().fromJson<List<Speaker>>(
                    loadResourceFile(context, R.raw.speakers), speakerListType)
            val sessionListType = object : TypeToken<List<Session>>() {}.type
            sessions = Gson().fromJson<List<Session>>(
                    loadResourceFile(context, R.raw.sessions), sessionListType)
            val scheduleListType = object : TypeToken<List<Schedule>>() {}.type
            schedules = Gson().fromJson<List<Schedule>>(
                    loadResourceFile(context, R.raw.schedule), scheduleListType)

            setupSessionMap()
            setupSpeakerMap()
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private lateinit var speakerMap: Map<Int, Speaker>

    private fun setupSpeakerMap() {
        speakerMap = speakers.map { it.id to it }.toMap()
    }

    lateinit var sessionMap: Map<Int, Session>

    private fun setupSessionMap() {
        sessionMap = sessions.map { it.id to it }.toMap()
    }

    fun toScheduleRow(timeSlot: Timeslot, sessionId: Int, sessionDate: String): ScheduleRow {
        val session = sessionMap.get(sessionId)!!
        val scheduleRow = ScheduleRow()
        scheduleRow.startTime = timeSlot.startTime
        scheduleRow.endTime = timeSlot.endTime
        // Assume that the speaker exists (error if not).
        val sessionSpeakers: List<Speaker>
        if (session.speakers != null && session.speakers.isNotEmpty()) {
            sessionSpeakers = session.speakers.map { speakerMap.get(it)!! }
            scheduleRow.speakerCount = sessionSpeakers.size
            scheduleRow.speakerNames = sessionSpeakers.map { it.name }
            scheduleRow.primarySpeakerName = scheduleRow.speakerNames.get(0)
            scheduleRow.photoUrlMap = sessionSpeakers.map { it.name to it.thumbnailUrl }.toMap()
            scheduleRow.speakerNameToOrgName = sessionSpeakers.map { it.name to it.company}.toMap()
        }
        scheduleRow.date = sessionDate
        if (session.place.isNullOrBlank()) {
            scheduleRow.room = "Location TBD"
        } else {
            scheduleRow.room = session.place!!
        }
        scheduleRow.id = sessionId.toString()
        scheduleRow.talkDescription = session.description?:"No description."
        scheduleRow.talkTitle = session.title
        scheduleRow.isOver = false // TODO: implement isOver based on current time relative to end time.
//        scheduleRow.trackSortOrder // TODO: assign
        return scheduleRow
    }

    companion object {
        val instance: DataProvider by lazy { Holder.INSTANCE }
    }
}
