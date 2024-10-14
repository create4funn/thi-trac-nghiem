package com.example.thitracnghiem.model

import java.sql.Timestamp

data class AnnouncementItem(
    val classroom_id: Int?,
    val text : String,
    val create_at : Timestamp?
)
