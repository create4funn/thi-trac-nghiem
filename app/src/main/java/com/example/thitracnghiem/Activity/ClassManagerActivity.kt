package com.example.thitracnghiem.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ClassItem
import com.example.thitracnghiem.model.UserItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassManagerActivity : AppCompatActivity() {
    private lateinit var tv_className: TextView
    private lateinit var tv_grade: TextView
    private lateinit var tv_subject: TextView
    private lateinit var tv_numOfMem: TextView
    private lateinit var tv_numOfTest: TextView
    private lateinit var tv_request: TextView
    private lateinit var tv_edit: TextView
    private lateinit var tv_memberList: LinearLayout
    private lateinit var tv_joinRequest: LinearLayout
    private lateinit var tv_testList: LinearLayout

    private var memList : List<UserItem> = emptyList()
    private var joinRequestList : List<UserItem> = emptyList()

    private lateinit var classItem : ClassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_manager)

        classItem = intent.getSerializableExtra("classItem") as ClassItem
        joinRequestList = (intent.getSerializableExtra("user_list") as? ArrayList<UserItem>)!!

        initView(classItem)

        if(joinRequestList.isNotEmpty()){
            tv_request.visibility = View.VISIBLE
            tv_request.text = joinRequestList.size.toString()
        }

        tv_memberList.setOnClickListener {
            getMember()
        }

        tv_joinRequest.setOnClickListener {
            val intent = Intent(this, JoinRequestActivity::class.java)
            intent.putExtra("user_list", ArrayList(joinRequestList))
            intent.putExtra("classroom_id", classItem.classroom_id!!)
            startActivity(intent)
        }

        tv_testList.setOnClickListener {
            val intent = Intent(this, ExamActivity::class.java)
            intent.putExtra("class_id", classItem.classroom_id)
            startActivity(intent)
        }

        tv_edit.setOnClickListener {
            val intent = Intent(this, CreateClassActivity::class.java)
            intent.putExtra("classItem", classItem)
            intent.putExtra("check", false)
            startActivity(intent)
        }
    }

    private fun initView(classItem : ClassItem) {
        tv_className = findViewById(R.id.tv_className)
        tv_grade = findViewById(R.id.tv_grade)
        tv_subject = findViewById(R.id.tv_subject)
        tv_numOfMem = findViewById(R.id.tv_NumOfMem)
        tv_numOfTest = findViewById(R.id.tv_numOfTest)
        tv_memberList = findViewById(R.id.member_list)
        tv_testList = findViewById(R.id.test_list)
        tv_joinRequest = findViewById(R.id.tv_join_requests)
        tv_request = findViewById(R.id.tv_request_count)
        tv_edit = findViewById(R.id.tv_edit)

        tv_className.text = classItem.class_name
        tv_grade.text = classItem.grade
        tv_subject.text = classItem.subject_name
        tv_numOfMem.text = classItem.numOfMem.toString()
        tv_numOfTest.text = classItem.numOfTest.toString()
    }

    private fun getMember(){
        val classService = RetrofitClient.instance(this).create(ClassService::class.java)
        classService.getMember(classItem.classroom_id!!).enqueue(object : Callback<List<UserItem>> {
            override fun onResponse(call: Call<List<UserItem>>, response: Response<List<UserItem>>) {
                if (response.isSuccessful) {
                    memList = response.body()!!
                    val intent = Intent(this@ClassManagerActivity, JoinRequestActivity::class.java)
                    intent.putExtra("user_list", ArrayList(memList))
                    intent.putExtra("classroom_id", classItem.classroom_id!!)
                    intent.putExtra("check", true)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<List<UserItem>>, t: Throwable) {
                Toast.makeText(this@ClassManagerActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })

    }
}