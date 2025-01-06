package com.example.thitracnghiem.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thitracnghiem.ApiService.AuthService
import com.example.thitracnghiem.ApiService.RegisterResponse
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.ApiService.UserRegister
import com.example.thitracnghiem.R
import com.example.thitracnghiem.databinding.ActivitySignUpBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val username = binding.edtHoten.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtSignUpPass.text.toString()
            val confirmPassword = binding.edtNhapLai.text.toString()
            val phoneNumber = binding.edtSdt.text.toString()
            val selectedRoleId = binding.rgRole.checkedRadioButtonId
            val role = when (selectedRoleId) {
                R.id.rbHocsinh -> 1
                R.id.rbGiaovien -> 2
                else -> 0
            }

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && role != 0) {
                if (password == confirmPassword) {
                    val apiService = RetrofitClient.instance(this).create(AuthService::class.java)
                    val registration = UserRegister(username, email, phoneNumber, password, role)
                    Log.d("abc", "test 1")
                    apiService.registerUser(registration).enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            Log.d("abc", "test 2")
                            if (response.isSuccessful) {
                                Toast.makeText(this@SignUpActivity, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Nếu response không thành công, in chi tiết lỗi
                                val errorBody = response.errorBody()?.string()
                                Log.e("SignUpActivity", "Lỗi API: $errorBody")
                                Toast.makeText(this@SignUpActivity, "Đăng ký thất bại: $errorBody", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Log.e("SignUpActivity", "Có lỗi xảy ra", t)
                            Toast.makeText(this@SignUpActivity, "Có lỗi xảy ra: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Không để trống thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvBacklogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
