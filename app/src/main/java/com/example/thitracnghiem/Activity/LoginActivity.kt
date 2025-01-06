package com.example.thitracnghiem.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.ApiService.AuthService
import com.example.thitracnghiem.ApiService.LoginResponse
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.ApiService.UserLogin
import com.example.thitracnghiem.databinding.ActivityLoginBinding
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = RetrofitClient.instance(this).create(AuthService::class.java)
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                val credentials = UserLogin(email, password)

                apiService.loginUser(credentials).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val token = loginResponse?.token
                            var userId = ""
                            if(token != null){
                                val jwt = JWT(token)
                                userId = jwt.getClaim("userId").asString().toString()
                                val username = jwt.getClaim("username").asString()
                                val role = jwt.getClaim("role").asString()

                                val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("userId", userId)
                                    putString("username", username)
                                    putString("role", role)
                                    apply()
                                }
                            }

                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    return@addOnCompleteListener
                                }
                                val fcmToken = task.result
                                Log.d("abcd", "$fcmToken")
                                // Gửi token này về backend của bạn để lưu trữ
                                sendFcmToken(userId, fcmToken)
                            }

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Vui lòng không để trống thông tin", Toast.LENGTH_SHORT).show()
            }
        }


        binding.tvRegisterHere.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendFcmToken(userId: String, fcmToken: String?) {

        apiService.updateFcmToken(userId.toInt(), fcmToken!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }
}
