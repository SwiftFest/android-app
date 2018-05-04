package io.swiftfest.www.swiftfest.data.model

data class Session(
        val id: Int,
        val title: String,
        val description: String?,
        val outcome: String?,
        val subtype: String?,
        val complexity: String?,
        val place: String?,
        val service: Boolean?,
        val speakers: List<Int>?)