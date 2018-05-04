package io.swiftfest.www.swiftfest.data.model

class FaqItem {

    data class Answer(
            var answer: String = "",
            var photoLink: String = "",
            var mapLink: String = "",
            var otherLink: String = ""
    )

    var answers: List<Answer> = emptyList()
    var question: String = ""
}