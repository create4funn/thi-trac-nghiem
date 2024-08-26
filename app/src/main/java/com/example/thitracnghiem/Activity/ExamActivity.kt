package com.example.thitracnghiem.Activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ExamAdapter
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.QuestionItem
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ExamActivity : AppCompatActivity(), ExamAdapter.OnItemClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var subject: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        recyclerView = findViewById(R.id.recycleView_exam)
        recyclerView.layoutManager = LinearLayoutManager(this)

        subject = intent.getStringExtra("subject").toString()
        val examList = mutableListOf<ExamItem>()
        db = FirebaseFirestore.getInstance()
        db.collection("subjects").document(subject!!).collection("exams")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.get("examName")
                    val duration = document.getLong("duration")
                    val num = document.getLong("numOfQues")
                    val questions = document.get("questions") as? List<String>

                    val data = ExamItem(document.id, name.toString(), duration, num, questions!!)
                    examList.add(data)
                    Log.d("acc", "$questions")

                }
                val adapter = ExamAdapter(examList, this)
                recyclerView.adapter = adapter

            }


    }

    override fun onSaveClick(item: ExamItem) {


        fetchData(item) { questionsList ->
            val dbHelper = ExamDatabaseHelper(this)
            dbHelper.saveExamToSQLite(item, questionsList)
            Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
        }


    }

    private fun fetchData(exam: ExamItem, callback: (List<QuestionItem>) -> Unit) {
        val questionsList = mutableListOf<QuestionItem>()
        val tasks = exam.questions.map { questionId ->
            db.collection("questions").document(questionId).get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val questionId = document.id
                    val text = document.getString("text") ?: ""
                    val listAnswer = document.get("options") as? List<String> ?: listOf()
                    val correct = document.getString("answer") ?: ""

                    val questionItem = QuestionItem(questionId, text, listAnswer, correct)
                    questionsList.add(questionItem)
                }
                callback(questionsList)
            }
            .addOnFailureListener { e ->
                Log.w("DoExamActivity", "Error getting documents.", e)
                callback(emptyList())
            }
        Log.d("abc", "test2")

    }


    override fun onDoExamClick(item: ExamItem) {

        val intent = Intent(this, DoExamActivity::class.java)
        intent.putStringArrayListExtra("questions", ArrayList(item.questions))
        intent.putExtra("time", item.duration)
        intent.putExtra("subject", subject)
        intent.putExtra("idExam", item.id)

        startActivity(intent)
    }

    override fun onHistory(item: ExamItem) {
        val intent = Intent(this, HistoryActivity::class.java)

        intent.putExtra("subject", subject)
        intent.putExtra("idExam", item.id)

        startActivity(intent)
    }


}