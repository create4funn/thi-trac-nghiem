package com.example.thitracnghiem.model

data class QuestionItem(
    val id:String,
    val text: String,
    val listAnswer: List<String>,
    val correct: String
)
