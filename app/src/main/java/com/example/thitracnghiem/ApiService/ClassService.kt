package com.example.thitracnghiem.ApiService

import com.example.thitracnghiem.model.AnnouncementItem
import com.example.thitracnghiem.model.ClassItem
import com.example.thitracnghiem.model.HistoryItem
import com.example.thitracnghiem.model.UserItem
import retrofit2.Call
import retrofit2.http.*
import java.sql.Timestamp

interface ClassService {
    @GET("classrooms/getAll/{student_id}")
    fun getAllClasses(@Path("student_id") student_id : Int): Call<List<ClassItem>>

    @GET("classrooms/student/{student_id}")
    fun getJoinedClass(@Path("student_id") student_id : Int): Call<List<ClassItem>>

    @GET("classrooms/teacher/{teacher_id}")
    fun getClassByTeacher(@Path("teacher_id") teacher_id : Int): Call<List<ClassItem>>


    @POST("classrooms/create")
    fun createClass(@Body classRequest: ClassItem): Call<Void>

    @POST("classrooms/join/{classroom_id}/{student_id}")
    fun studentJoinRequest(@Path("classroom_id") classroom_id : Int, @Path("student_id") student_id : Int): Call<Void>

    @GET("classrooms/getJoinRequest/{classroom_id}")
    fun getJoinRequest(@Path("classroom_id") classroom_id : Int): Call<List<UserItem>>

    //gv xét duyệt yêu cầu
    @POST("classrooms/respond")
    fun respondJoinRequest(@Body respondJoinRequest: JoinResponse): Call<Void>

    @GET("classrooms/getMember/{classroom_id}")
    fun getMember(@Path("classroom_id") classroom_id : Int): Call<List<UserItem>>

    @POST("classrooms/createAnnouncement")
    fun createAnnouncement(@Body announcement: AnnouncementItem): Call<Void>

    @GET("classrooms/getAnnouncements/{classroom_id}")
    fun getAnnouncements(@Path("classroom_id") classroom_id : Int): Call<List<AnnouncementItem>>

    @POST("classrooms/update")
    fun updateClass(@Body classItem: ClassItem): Call<Void>

    @GET("classrooms/search/{keyword}")
    fun searchClass(@Path("keyword") keyword : String): Call<List<ClassItem>>

    @GET("classrooms/filter")
    fun filterClass(@Query("subject_name") subjectName: String? = null, @Query("grade") grade: String? = null): Call<List<ClassItem>>
}

data class JoinResponse(val classroom_id: Int, val student_id: Int, val response: Int)


