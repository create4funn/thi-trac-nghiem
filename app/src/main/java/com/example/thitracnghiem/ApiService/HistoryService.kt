package com.example.thitracnghiem.ApiService

import com.example.thitracnghiem.model.HistoryItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface HistoryService {
    @POST("history/save")
    fun saveExamHistory(@Body historyRequest: HistoryItem): Call<Void>

    @GET("history/{userId}/{examId}")
    fun getHistoryByUserAndExam(@Path("userId") userId: Int, @Path("examId") examId: Int): Call<List<HistoryItem>>

    @GET("history/exam/{examId}")
    fun getHistoryByExamId(@Path("examId") examId: Int): Call<List<HistoryItem>>
}
