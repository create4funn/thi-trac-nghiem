package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.thitracnghiem.Activity.LoginActivity
import com.example.thitracnghiem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {
    private lateinit var email: TextView
    private lateinit var uname: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val logOut: TextView = view.findViewById(R.id.logOut)
        email = view.findViewById(R.id.profile_email)
        uname = view.findViewById(R.id.profile_username)

        val user = FirebaseAuth.getInstance()

        showInfo()

        logOut.setOnClickListener {
            user.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

    }

    private fun showInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user!!.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    email.text = documentSnapshot.getString("Email")
                    uname.text = documentSnapshot.getString("Username")
                }
            }
    }
}