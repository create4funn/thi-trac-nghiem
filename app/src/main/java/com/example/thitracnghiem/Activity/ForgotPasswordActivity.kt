package com.example.thitracnghiem.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.thitracnghiem.ApiService.*
import com.example.thitracnghiem.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var btnSendOtp: Button
    private lateinit var btnVerifyOTP: Button
    private lateinit var edtEmail: EditText
    private lateinit var edtOTP: EditText
    private lateinit var authService: AuthService
    private var user_id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btnSendOtp = findViewById(R.id.btnSendOTP)
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP)
        edtEmail = findViewById(R.id.etEmail)
        edtOTP = findViewById(R.id.etOTP)

        btnSendOtp.setOnClickListener {
            sendOTP(edtEmail.text.toString())
        }

        btnVerifyOTP.setOnClickListener {
            val otpRequest = VerifyOtpRequest(user_id, edtOTP.text.toString())
            verifyOtp(otpRequest)
        }
    }

    private fun verifyOtp(otpRequest: VerifyOtpRequest) {
        authService.verifyOtp(otpRequest).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("user_id", user_id)
                    finish()
                    startActivity(intent)
                }else{
                    Toast.makeText(this@ForgotPasswordActivity, "Mã OTP không đúng, vui lòng thử lại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun sendOTP(email: String){
        authService = RetrofitClient.instance(this).create(AuthService::class.java)
        val emailRequest = EmailRequest(email)

        authService.sendOTP(emailRequest).enqueue(object: Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if(response.isSuccessful){
                    user_id = response.body()?.user_id ?: -1
                    edtOTP.visibility = View.VISIBLE
                    btnVerifyOTP.visibility = View.VISIBLE
                    Toast.makeText(this@ForgotPasswordActivity, "Gửi mã OTP thành công, vui lòng kiểm tra SMS", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Email chưa đúng, vui lòng nhập lại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("API_ERROR", "Lỗi khi gọi API: ${t.message}")
                Toast.makeText(this@ForgotPasswordActivity, "Có lỗi xảy ra: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }
}