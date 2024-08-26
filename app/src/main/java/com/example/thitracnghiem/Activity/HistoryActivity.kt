// HistoryActivity.kt
package com.example.thitracnghiem.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.HistoryAdapter
import com.example.thitracnghiem.model.HistoryItem
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rcvScored: RecyclerView
    private var historyList = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance()

        rcvScored = findViewById(R.id.rcvScored)
        // Set LinearLayoutManager with horizontal orientation
        rcvScored.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        historyAdapter = HistoryAdapter(historyList)
        rcvScored.adapter = historyAdapter

        // Fetch history data
        fetchHistory()
    }

    private fun fetchHistory() {
        val subject = intent.getStringExtra("subject")
        val idExam = intent.getStringExtra("idExam")

        if (subject != null && idExam != null) {
            db.collection("subjects").document(subject).collection("exams").document(idExam)
                .collection("histories")
                .get()
                .addOnSuccessListener { documents ->
                    historyList.clear() // Clear the list before adding new data
                    for (document in documents) {
                        val username = document.getString("username")
                        val score = document.getString("score")
                        val correctAnswers = document.getString("correctAnswers")
                        val timeTaken = document.getString("timestamp")

                        if (username != null && score != null && correctAnswers != null && timeTaken != null) {
                            val historyItem = HistoryItem(username, score, correctAnswers, timeTaken)
                            historyList.add(historyItem)
                        }
                    }
                    historyAdapter.notifyDataSetChanged() // Notify adapter about data changes
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Thiếu thông tin cần thiết để tải dữ liệu.", Toast.LENGTH_SHORT).show()
        }
    }
}
