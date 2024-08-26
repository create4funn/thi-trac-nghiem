package com.example.thitracnghiem.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.thitracnghiem.R
import com.example.thitracnghiem.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {
            val username = binding.edtHoten.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtSignUpPass.text.toString()
            val confirmPassword = binding.edtNhapLai.text.toString()
            val sdt = binding.edtSdt.text.toString()

            val selectedRoleId = binding.rgRole.checkedRadioButtonId
            val role = when (selectedRoleId) {
                R.id.rbSinhvien -> "Sinh viên"
                R.id.rbGiaovien -> "Giáo viên"
                else -> ""
            }

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && role.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userID = firebaseAuth.currentUser!!.uid
                            val userMap = hashMapOf(
                                "Username" to username,
                                "Email" to email,
                                "Role" to role
                            )
                            db.collection("users").document(userID).set(userMap).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Email đã tồn tại hoặc không đúng định dạng", Toast.LENGTH_SHORT).show()
                        }
                    }
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
