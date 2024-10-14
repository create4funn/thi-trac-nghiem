package com.example.thitracnghiem.ApiService

import com.example.thitracnghiem.model.WordResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryService {
    @GET("en/{word}")
    suspend fun getMeaning(@Path("word") word: String): Response<List<WordResult>>
}