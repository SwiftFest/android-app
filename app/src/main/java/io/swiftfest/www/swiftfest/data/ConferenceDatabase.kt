package io.swiftfest.www.swiftfest.data

import io.swiftfest.www.swiftfest.R

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime


open class ConferenceDatabase {

    /*
     * 3 Primary data classes
     * Schedule, Session, Speaker
     */

    data class Schedule(
            val date: String,
            val dateReadable: String,
            val tracks: List<Track>,
            val timeslots: List<Timeslot>
    )

    data class Session(
            val id: Int,
            val title: String,
            val description: String,
            val outcome: String,
            val subtype: String,
            val complexity: String,
            val speakers: List<Int>)

    data class Speaker(
            val id: Int,
            val name: String,
            val surname: String,
            val company: String,
            val title: String,
            val bio: String?,
            val thumbnailUrl: String,
            val rockstar: Boolean) {
//            val social: List<Social>) {

        val socialProfiles: Map<String, String>
            get() {
                // TODO: fix parsing and readd.
//                return social.map { it.name to it.link }.toMap()
                return mapOf()
            }

        companion object {
            var SPEAKER_ITEM_ROW = "speaker_item_row"
        }
    }

    data class EventSpeaker(
            val thumbnailUrl: String = "",
            val socialProfiles: HashMap<String, String>? = HashMap(0),
            var bio: String = "",
            var title: String = "",
            var company: String = "",
            var name: String = "") {

        companion object {
            var SPEAKER_ITEM_ROW = "speaker_item_row"
        }
    }

    data class Track(
            val title: String,
            val color: String
    )

    data class Timeslot(
            val startTime: String,
            val endTime: String,
            val sessionIds: List<Int>
    )

    data class Social(
            val name: String,
            val link: String
    )

    data class VolunteerEvent(
            val twitter: String = "",
            val pictureUrl: String = "",
            var position: String = "",
            var firstName: String = "",
            var lastName: String = "")

    class FaqEvent {

        data class Answer(
                var answer: String = "",
                var photoLink: String = "",
                var mapLink: String = "",
                var otherLink: String = ""
        )

        var answers: List<Answer> = emptyList()
        var question: String = ""
    }

    companion object {
        fun getLocalStartTime(startTime: CharSequence): LocalDateTime {
            return ZonedDateTime.parse(startTime).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
}

