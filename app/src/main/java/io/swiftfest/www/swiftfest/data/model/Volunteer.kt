package io.swiftfest.www.swiftfest.data.model

data class Volunteer(
    val id: String,
    val name: String,
    val surname: String,
    val title: String?,
    val thumbnailUrl: String,
    val team: Boolean,
    val social: List<Social>
) {

    fun getFullThumbnailUrl(): String {
        return "http://swiftfest.io/img/people/${thumbnailUrl}"
    }

}
