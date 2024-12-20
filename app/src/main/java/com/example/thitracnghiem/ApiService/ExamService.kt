package com.example.thitracnghiem.ApiService

import android.net.Uri
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.QuestionItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExamService {
    @GET("exams/subject/{subjectId}")
    fun getExamsBySubject(@Path("subjectId") subjectId: Int): Call<List<ExamItem>>

    @GET("exams/class1/{classId}")
    fun getExamsByClass(@Path("classId") classId: Int): Call<List<ExamItem>>

    @GET("exams/class2/{classId}")
    fun getAllExamsByClass(@Path("classId") classId: Int): Call<List<ExamItem>>

    @POST("exams/create")
    fun createExam(@Body request: CreateExamRequest): Call<ResponseBody>

    @DELETE("exams/delete/{exam_id}/{class_id}")
    fun deleteExam(@Path("exam_id") exam_id: Int, @Path("class_id") class_id: Int): Call<Void>

    @PUT("exams/visibility/{exam_id}/{status}")
    fun updateVisibility(@Path("exam_id") exam_id: Int, @Path("status") status: Int): Call<Void>

    @PUT("exams/update/{exam_id}")
    fun updateExam(@Path("exam_id") exam_id: Int, @Body request: CreateExamRequest): Call<ResponseBody>
}

data class CreateExamRequest(
    val exam_name: String,
    val class_id: Int?,
    val subject_id: Int?,
    val duration: Int,
    val numOfQues: Int,
    val pdf: String?,
    val questions: List<QuestionItem>
)