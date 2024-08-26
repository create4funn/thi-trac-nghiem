package com.example.thitracnghiem.model

data class ExamItem(
    val id : String,
    val name: String,
    val duration: Long?,
    val numOfQues: Long?,
    val questions: List<String>
)
