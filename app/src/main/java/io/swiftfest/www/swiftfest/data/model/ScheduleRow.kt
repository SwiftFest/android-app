package io.swiftfest.www.swiftfest.data.model

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class ScheduleRow(
        var primarySpeakerName: String = "",
        var id: String = "",
        var startTime: String = "",
        var talkTitle: String = "",
        var speakerCount: Int = 0,
        var talkDescription: String = "",
        var speakerNames: List<String> = emptyList(),
        var speakerNameToOrgName: Map<String, String?> = HashMap(0),
        var utcStartTimeString: String = "",
        var endTime: String = "",
        var room: String = "",
        var date: String = "",
        var trackSortOrder: Int = 0,
        var photoUrlMap: Map<String, String> = HashMap(0),
        var isOver: Boolean = false,
        var speakerIds: List<Int> = emptyList(),
        var primarySpeakerId: Int = 0
) {

    fun hasSpeaker(): Boolean = speakerNames.isNotEmpty()

    fun hasMultipleSpeakers(): Boolean = speakerNames.size > 1

    fun getSpeakerString(): String? = speakerNames.joinToString(", ")

    private fun parseDateTime(timeString: String): Date {
        val timeWithExtension = getReadableTime(timeString)
        val dateTimeString = "${date} ${timeWithExtension}"
//        val format = SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US)
        val format = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US)
        var startDate: Date
        try {
            startDate = format.parse(dateTimeString)
        } catch (e: ParseException) {
            Log.e("ScheduleAdapterItem", "Parse error: $e for $dateTimeString")
            val altFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US)
            startDate = altFormat.parse("${date} ${timeString}")
        }
        return startDate
    }

    fun getReadableTime(timeString: String): Any {
        val extension: String
        val startTimeInt = timeString.split(":").get(0).toInt()
        if (startTimeInt > 6 && startTimeInt != 12) {
            extension = "am"
        } else {
            extension = "pm"
        }
        return "${timeString} ${extension}"
    }

    fun getStartDate(): Date {
        return parseDateTime(startTime)
    }

    fun getEndDate(): Date {
        return parseDateTime(endTime)
    }
}