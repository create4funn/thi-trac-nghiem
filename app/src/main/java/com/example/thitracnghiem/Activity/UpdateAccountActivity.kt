package com.example.thitracnghiem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.thitracnghiem.ApiService.AuthService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.UserItem
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateAccountActivity : AppCompatActivity() {
    private lateinit var edtUsername : TextInputEditText
    private lateinit var edtEmail : TextInputEditText
    private lateinit var edtPhone : TextInputEditText
    private lateinit var btnUpdate : Button
    private lateinit var userItem: UserItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_account)

        edtUsername = findViewById(R.id.edtUpdateUsername)
        edtEmail = findViewById(R.id.edtUpdateEmail)
        edtPhone = findViewById(R.id.edtUpdatePhone)
        btnUpdate = findViewById(R.id.btnUpdateProfile)

        userItem = intent.getSerializableExtra("userItem") as UserItem

        edtUsername.setText(userItem.username)
        edtEmail.setText(userItem.email)
        edtPhone.setText(userItem.phone)

        btnUpdate.setOnClickListener {
            if(edtUsername.text.toString().isNotEmpty() && edtEmail.text.toString().isNotEmpty() && edtPhone.text.toString().isNotEmpty()){
                val userItemRequest = UserItem(null, edtUsername.text.toString(), edtEmail.text.toString(), edtPhone.text.toString())
                updateProfile(userItemRequest)
            }else{
                Toast.makeText(this, "Vui lòng không để trống thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile(userItemRequest: UserItem) {
        val authService = RetrofitClient.retrofit.create(AuthService::class.java)
        authService.updateProfile(userItem.user_id!!, userItemRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(this@UpdateAccountActivity, "Update thành công", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}