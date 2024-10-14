package com.example.thitracnghiem.ApiService

import com.example.thitracnghiem.model.SubjectItem
import retrofit2.Call
import retrofit2.http.GET


interface SubjectService {
    @GET("subjects")  // Đường dẫn API lấy danh sách môn học
    fun getSubjects(): Call<List<SubjectItem>>
}
