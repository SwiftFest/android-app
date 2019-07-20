package io.swiftfest.www.swiftfest.data.model

import android.util.Log
import io.swiftfest.www.swiftfest.R
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
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
        var primarySpeakerId: Int = 0,
        var subtype: String? = ""
) {

    companion object {
        val ISO_LENGTH = "2019-07-29T11:00:00".length

        @JvmStatic
        fun getReadableTime(timeString: String): String {
            val t = LocalDateTime.parse(timeString.substring(0, ISO_LENGTH))
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            return t.format(formatter)
        }
    }

    fun getItemColor(): Int {
        if (isOver) {
            return R.color.colorGray
        } else if (subtype == "workshop") {
            return R.color.workshopColor
        } else if (subtype == "presentation"){
            // TODO: add custom color based on presentation track.
            return R.color.colorAccent
        }
        return R.color.colorAccent
    }


    fun getReadableStartTime(): String {
        return getReadableTime(startTime)
    }

    fun getReadableEndTime(): String {
        return getReadableTime(endTime)
    }

    fun getEndDate(): LocalDateTime = LocalDateTime.parse(endTime.substring(0, ISO_LENGTH))
}