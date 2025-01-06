package com.example.thitracnghiem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.thitracnghiem.ApiService.*
import com.example.thitracnghiem.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var edtPassword: EditText
    private lateinit var edtPassword2: EditText
    private lateinit var btnResetPassword: Button
    private var user_id: Int = 0
    private lateinit var authService: AuthService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        edtPassword = findViewById(R.id.etInputPassword)
        edtPassword2 = findViewById(R.id.etInputPassword2)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        user_id = intent.getIntExtra("user_id", 3)
        btnResetPassword.setOnClickListener {
            val newPassword = edtPassword.text.toString()
            val confirmPassword = edtPassword2.text.toString()

            if(newPassword.length < 6){
                Toast.makeText(this, "Mật khẩu cần tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
            }else if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            } else{
                val resetPasswordRequest = ResetPasswordRequest(user_id, newPassword)
                resetPassword(resetPasswordRequest)
            }

        }
    }

    private fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        authService = RetrofitClient.instance(this).create(AuthService::class.java)
        authService.resetPassword(resetPasswordRequest).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@ResetPasswordActivity, "Reset mật khẩu thành công", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this@ResetPasswordActivity, "Không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
            }

        })
    }
}