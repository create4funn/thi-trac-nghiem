package com.example.thitracnghiem.ApiService

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object RetrofitClient {
    private const val BASE_URL = "https://10.0.2.2:3000/api/"

    //private const val BASE_URL = "https://192.168.43.221:3000/api/"
    private fun getSecureOkHttpClient(context: Context): OkHttpClient {
        try {
            // Load self-signed certificate from raw resources
            val cf = CertificateFactory.getInstance("X.509")
            val cert = context.assets.open("cert.pem") // Lấy chứng chỉ từ asset foler
            val ca: X509Certificate = cf.generateCertificate(cert) as X509Certificate
            cert.close()

            // Create a KeyStore containing the trusted certificate
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)

            // Create a TrustManager that trusts the certificate in our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            // Create an SSLContext that uses our TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, null)

            // Create OkHttpClient with SSL
            return OkHttpClient.Builder()
                .sslSocketFactory(
                    sslContext.socketFactory,
                    tmf.trustManagers[0] as X509TrustManager
                )
                .build()
        } catch (e: Exception) {
            //Log.d("abc", "$e")
            throw RuntimeException(e)
        }
    }

    fun instance(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getSecureOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
//    //private val client = OkHttpClient.Builder().build()
//    private val client = getSecureOkHttpClient()
//    val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(client)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
}