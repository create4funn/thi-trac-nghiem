package com.example.thitracnghiem.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thitracnghiem.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val user_id = sharedPref.getString("userId", null)

        // Delay 2 giây để hiển thị Splash
        Handler(Looper.getMainLooper()).postDelayed({
            if (user_id != null) {
                // Nếu có token, chuyển đến MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
//                Toast.makeText(this, "Đã đăng nhập", Toast.LENGTH_SHORT).show()
            } else {
                // Nếu không có token, chuyển đến LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 1000)
    }
}