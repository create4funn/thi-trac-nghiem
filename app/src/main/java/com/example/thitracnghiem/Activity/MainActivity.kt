package com.example.thitracnghiem.Activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.*
import com.example.thitracnghiem.Fragment.*
import com.example.thitracnghiem.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var role = ""
    private var username = ""
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRole()
        if (!isNetworkAvailable(this)){
            replaceFragment(SavedExamFragment())
            binding.bottomNav.selectedItemId = R.id.save
            Toast.makeText(this, "Offline", Toast.LENGTH_SHORT).show()
        }else{
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("username_key", username) // Gửi username vào Bundle
            homeFragment.arguments = bundle
            replaceFragment(homeFragment)
        }

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    val homeFragment = HomeFragment()
                    val bundle = Bundle()
                    bundle.putString("username_key", username)
                    homeFragment.arguments = bundle
                    replaceFragment(homeFragment)
                }
                R.id.classroom -> {
                    if (role == "1") {
                        replaceFragment(ClrStudentFragment())
                    } else if (role == "2") {
                        replaceFragment(ClrTeacherFragment())
                    }
                }
                R.id.save -> replaceFragment(SavedExamFragment())
                R.id.account -> {
                    val accountFragment = AccountFragment()
                    val bundle = Bundle()
                    bundle.putString("userId", userId)
                    accountFragment.arguments = bundle
                    replaceFragment(accountFragment)
                }
                R.id.dictionary -> replaceFragment(DictionaryFragment())
            }
            true
        }

    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }

    private fun getRole(){
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        role = sharedPref.getString("role", null).toString()
        username = sharedPref.getString("username", null).toString()
        userId = sharedPref.getString("userId", null).toString()
    }

    fun logout(){

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Xóa tất cả dữ liệu trong SharedPreferences
            apply() // Áp dụng thay đổi
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}