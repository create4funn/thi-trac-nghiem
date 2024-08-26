package com.example.thitracnghiem.model

data class ClassItem(
    var id: String = "",
    val tenLop: String = "",
    val monHoc: String = "",
    val lop: String = "",
    val imageUrl: String = ""
){
    fun setId(id: String): ClassItem {
        this.id = id
        return this
    }
}