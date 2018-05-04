package io.swiftfest.www.swiftfest.data.model

class ScheduleDetail(val listRow: ScheduleRow) {

    var speakerBio: String = ""
    var twitter: String = ""
    var linkedIn: String = ""
    var facebook: String = ""

    val id: String get() = listRow.id
}