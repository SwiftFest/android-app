package io.swiftfest.www.swiftfest.data.model

data class Timeslot(
        val startTime: String,
        val endTime: String,
        val sessionIds: List<Int>
)