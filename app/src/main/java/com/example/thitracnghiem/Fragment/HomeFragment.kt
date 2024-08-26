package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.thitracnghiem.Activity.ExamActivity
import com.example.thitracnghiem.Activity.MainActivity
import com.example.thitracnghiem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private val mainActivity = MainActivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemToan: CardView = view.findViewById(R.id.itemToan)
        val itemAnh: CardView = view.findViewById(R.id.itemAnh)
        val itemHoa: CardView = view.findViewById(R.id.itemHoa)
        val itemSinh: CardView = view.findViewById(R.id.itemSinh)
        val itemLy: CardView = view.findViewById(R.id.itemLy)
        val itemSu: CardView = view.findViewById(R.id.itemSu)
        val itemDia: CardView = view.findViewById(R.id.itemDia)
        val itemGdcd: CardView = view.findViewById(R.id.itemGdcd)
        val username: TextView = view.findViewById(R.id.tv_uname_home)
        val user = FirebaseAuth.getInstance().currentUser?.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user!!).get().addOnSuccessListener {
            username.text = it.getString("Username")
        }


        itemToan.setOnClickListener {
            handleSubjectItemClick("math")
        }

        itemAnh.setOnClickListener {
            handleSubjectItemClick("english")
        }
    }

    private fun handleSubjectItemClick(subject: String) {
        if (mainActivity.isNetworkAvailable(requireContext())) {
            val intent = Intent(context, ExamActivity::class.java)
            intent.putExtra("subject", subject)
            startActivity(intent)
        } else {
            Toast.makeText(context, "Không có kết nối mạng", Toast.LENGTH_SHORT).show()
        }
    }
}