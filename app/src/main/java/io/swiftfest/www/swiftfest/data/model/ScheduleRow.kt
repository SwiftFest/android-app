package io.swiftfest.www.swiftfest.data.model

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
}