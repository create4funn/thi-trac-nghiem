package com.example.thitracnghiem.model

data class ExamItem(
    val exam_id : Int?,
    val exam_name: String,
    val duration: Int,
    val numOfQues: Int,
    val pdf: String?,
    var status: Int?
): java.io.Serializable
