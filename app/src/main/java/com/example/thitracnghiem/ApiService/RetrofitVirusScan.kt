package com.example.thitracnghiem.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitVirusScan {
    private const val BASE_URL = "https://www.virustotal.com/api/v3/"
    private const val API_KEY = "023c7f0585e7ed45427137c211fdfa0626d283ab4b8fb6d3e3f3e37a081ac895" // Thay bằng API Key của bạn

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getVirusTotalService(): VirusTotalService {
        return getRetrofit().create(VirusTotalService::class.java)
    }
}
