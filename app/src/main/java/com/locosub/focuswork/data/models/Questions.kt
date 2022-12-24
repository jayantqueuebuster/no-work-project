package com.locosub.focuswork.data.models

data class Questions(
    val id: Int = 0,
    val question: String = "",
    val answer: String = "",
) {
    data class QuestionResponse(
        val questions: Questions,
        val key: String = ""
    )
}
