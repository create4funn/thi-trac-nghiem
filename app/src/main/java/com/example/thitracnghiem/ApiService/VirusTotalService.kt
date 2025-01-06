package com.example.thitracnghiem.ApiService

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface VirusTotalService {
    @Multipart
    @POST("files")
    fun uploadFile(
        @Header("x-apikey") apiKey: String,
        @Part file: MultipartBody.Part
    ): Call<VirusTotalResponse>

    @GET("analyses/{id}")
    fun getScanResult(
        @Header("x-apikey") apiKey: String,
        @Path("id") scanId: String
    ): Call<VirusTotalResponse>
}

data class VirusTotalResponse(
    val data: Data
)

data class Data(
    val attributes: Attributes,
    val id: String,
    val type: String
)

data class Attributes(
    val date: Long,
    val results: Map<String, EngineResult>,
    val stats: Stats,
    val status: String
)

data class EngineResult(
    val category: String,
    val engine_name: String,
    val engine_update: String,
    val engine_version: String,
    val method: String,
    val result: String?
)

data class Stats(
    val confirmedTimeout: Int,
    val failure: Int,
    val harmless: Int,
    val malicious: Int,
    val suspicious: Int,
    val timeout: Int,
    val typeUnsupported: Int,
    val undetected: Int
)

//data class VirusTotalResponse(val data: Data) {
//    data class Data(
//        val id: String,
//        //val attributes: Attributes
//        val status: String,
//        val stats : LastAnalysisStats
//    )
//
//    data class Attributes(
//        val last_analysis_stats: LastAnalysisStats
//    )
//
//    data class LastAnalysisStats(
//        val malicious: Int,
//        val suspicious: Int,
//        val undetected: Int,
//        val harmless: Int
//    )
//}
