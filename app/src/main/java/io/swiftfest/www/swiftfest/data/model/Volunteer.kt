package io.swiftfest.www.swiftfest.data.model

data class Volunteer(
        val twitter: String = "",
        val pictureUrl: String = "",
        var position: String = "",
        var firstName: String = "",
        var lastName: String = "")