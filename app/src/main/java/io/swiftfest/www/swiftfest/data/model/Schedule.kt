package io.swiftfest.www.swiftfest.data.model

data class Schedule(
        val date: String,
        val dateReadable: String,
        val tracks: List<Track>,
        val timeslots: List<Timeslot>
)