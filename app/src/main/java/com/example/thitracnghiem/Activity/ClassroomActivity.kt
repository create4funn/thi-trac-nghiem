package com.example.thitracnghiem.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ExamAdapter
import com.example.thitracnghiem.model.ExamItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClassroomActivity : AppCompatActivity(), ExamAdapter.OnItemClickListener {
    private lateinit var btnCreateExam: Button
    private lateinit var className: TextView
    private lateinit var copyID: TextView
    private lateinit var rcvExam: RecyclerView
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)

        btnCreateExam = findViewById(R.id.btnCreateExam)
        className = findViewById(R.id.tv_className)
        rcvExam = findViewById(R.id.rcvExamClass)
        copyID = findViewById(R.id.copyID)

        db = FirebaseFirestore.getInstance()
        className.text = intent.getStringExtra("classname")
        val id = intent.getStringExtra("classId")
        loadDataExam(id!!)
        var role: String
        val user = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(user!!.uid).get().addOnSuccessListener {
            role = it.getString("Role").toString()
            if(role == "Sinh viên"){
                btnCreateExam.visibility = View.GONE
            }
        }

        copyID.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", id)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Đã sao chép id thành công", Toast.LENGTH_SHORT).show()
        }


        btnCreateExam.setOnClickListener {
            val intent = Intent(this, CreateExamActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

    }

    private fun loadDataExam(id : String) {
        val examList = mutableListOf<ExamItem>()
        rcvExam.layoutManager = LinearLayoutManager(this)
        db.collection("classrooms").document(id).collection("exams")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("examName")
                    val duration = document.getLong("duration")
                    val num = document.getLong("numOfQues")
                    val questions = document.get("questions") as? List<String>

                    val data = ExamItem(document.id ,name!!, duration, num, questions!!)
                    examList.add(data)
                    Log.d("acc", "$questions")

                }
                val adapter = ExamAdapter(examList, this)
                rcvExam.adapter = adapter

            }
    }

    override fun onSaveClick(item: ExamItem) {
        Toast.makeText(this, "Save: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDoExamClick(item: ExamItem) {
        val intent = Intent(this, DoExamActivity::class.java)
        intent.putStringArrayListExtra("questions", ArrayList(item.questions))
        intent.putExtra("time", item.duration)
        startActivity(intent)
    }

    override fun onHistory(item: ExamItem) {
        TODO("Not yet implemented")
    }


}