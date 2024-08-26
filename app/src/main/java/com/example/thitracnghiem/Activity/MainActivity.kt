package com.example.thitracnghiem.Activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.thitracnghiem.*
import com.example.thitracnghiem.Fragment.*
import com.example.thitracnghiem.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var role = ""
        db.collection("users").document(user!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                role = documentSnapshot.getString("Role").toString()
            }
        if (!isNetworkAvailable(this)){
            replaceFragment(SavedExamFragment())
            binding.bottomNav.selectedItemId = R.id.save
            Toast.makeText(this, "Offline", Toast.LENGTH_SHORT).show()
        }else{
            replaceFragment(HomeFragment())
        }


        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.classroom -> {
                    if (role == "Sinh viên") {
                        replaceFragment(ClrStudentFragment())
                    } else if (role == "Giáo viên") {
                        replaceFragment(ClrTeacherFragment())
                    }
                }
                R.id.save -> replaceFragment(SavedExamFragment())
                R.id.account -> replaceFragment(AccountFragment())

            }
            true
        }

    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
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