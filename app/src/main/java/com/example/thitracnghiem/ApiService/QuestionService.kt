package com.example.thitracnghiem.ApiService

import com.example.thitracnghiem.model.QuestionItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface QuestionService {
    @GET("questions/{exam_id}")
    fun getQuestionsByExamId(@Path("exam_id") examId: Int): Call<List<QuestionItem>>
}