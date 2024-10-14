package com.example.thitracnghiem.model

data class ClassItem(
    val classroom_id: Int?,
    val class_name: String,
    val grade: String,
    val subject_name: String,
    val class_img: String?,
    val teacher_id: Int?,
    val numOfMem: Int?,
    val numOfTest: Int?
): java.io.Serializable
