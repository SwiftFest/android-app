package io.swiftfest.www.swiftfest.data.model

data class Speaker(
        val id: Int,
        val name: String,
        val surname: String,
        val company: String?,
        val title: String?,
        val bio: String?,
        val thumbnailUrl: String,
        val rockstar: Boolean,
        val social: List<Social>?) {

    fun getFullThumbnailUrl(): String {
        return "https://swiftfest.io/img/people/${thumbnailUrl}"
    }

    val socialProfiles: Map<String, String>
        get() {
            if (social != null) {
                return social.map { it.name to it.link }.toMap()
            }
            return mapOf()
        }

    val fullName: String
        get() {
            return "$name $surname"
        }

    companion object {
        var SPEAKER_ITEM_ROW = "speaker_item_row"
    }
}