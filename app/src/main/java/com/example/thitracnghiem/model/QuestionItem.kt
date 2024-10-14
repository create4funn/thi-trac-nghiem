package com.example.thitracnghiem.model

data class QuestionItem(
    val question_id:Int?,
    val question_text: String?,
    val answers: List<Answer>
)

data class Answer(
    val answer_id: Int?,
    val answer_text: String?,
    var is_correct: Int
)