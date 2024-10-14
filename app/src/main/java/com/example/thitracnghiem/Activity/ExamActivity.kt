package com.example.thitracnghiem.Activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.ApiService.ExamService
import com.example.thitracnghiem.ApiService.QuestionService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ExamAdapter
import com.example.thitracnghiem.adapter.ExamAdapter2
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.QuestionItem
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExamActivity : AppCompatActivity(), ExamAdapter.OnItemClickListener,
    ExamAdapter2.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private var examList : List<ExamItem> = emptyList()
    private var questionsList: List<QuestionItem> = emptyList()
    private val examService = RetrofitClient.retrofit.create(ExamService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        recyclerView = findViewById(R.id.recycleView_exam)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val subject_id = intent.getIntExtra("subject_id", 0)
        val class_id = intent.getIntExtra("class_id", 0)

        if(subject_id > 0){
            getExamBySubject(subject_id)
        }else{
            getExamByClass(class_id)
        }

    }

    private fun getExamBySubject(subject_id : Int){

        examService.getExamsBySubject(subject_id).enqueue(object : Callback<List<ExamItem>> {
            override fun onResponse(call: Call<List<ExamItem>>, response: Response<List<ExamItem>>) {
                if (response.isSuccessful) {
                    examList = response.body()!!
                    recyclerView.adapter = ExamAdapter(examList, this@ExamActivity)
                } else {
                    Toast.makeText(this@ExamActivity, "Lỗi khi lấy danh sách bài thi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ExamItem>>, t: Throwable) {
                Toast.makeText(this@ExamActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getExamByClass(class_id : Int) {

        examService.getAllExamsByClass(class_id).enqueue(object : Callback<List<ExamItem>> {
            override fun onResponse(call: Call<List<ExamItem>>, response: Response<List<ExamItem>>) {
                if (response.isSuccessful) {
                    examList = response.body()!!
                    recyclerView.adapter = ExamAdapter2(examList, this@ExamActivity)
                } else {
                    Toast.makeText(this@ExamActivity, "Lỗi khi lấy danh sách bài thi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ExamItem>>, t: Throwable) {
                Toast.makeText(this@ExamActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveClick(item: ExamItem) {
        saveTest(item.exam_id!!, item)
    }

    private fun saveTest(examId: Int, item: ExamItem ) {
        val questionService = RetrofitClient.retrofit.create(QuestionService::class.java)

        questionService.getQuestionsByExamId(examId).enqueue(object : Callback<List<QuestionItem>> {
            override fun onResponse(call: Call<List<QuestionItem>>, response: Response<List<QuestionItem>>) {
                if (response.isSuccessful) {
                    questionsList = response.body()!!
                    val dbHelper = ExamDatabaseHelper(this@ExamActivity)
                    dbHelper.saveExamToSQLite(item, questionsList)
                    Toast.makeText(this@ExamActivity, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ExamActivity, "Lưu không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<QuestionItem>>, t: Throwable) {
                Log.e("API Error", "Failed to make API call", t)
            }
        })
    }


    override fun onDoExamClick(item: ExamItem) {
        val intent = Intent(this, DoExamActivity::class.java)
        intent.putExtra("exam_id", item.exam_id)
        intent.putExtra("time", item.duration)
        startActivity(intent)

    }

    override fun onHistory(item: ExamItem) {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra("exam_id", item.exam_id)
        startActivity(intent)

    }


    override fun onUpdateClick(item: ExamItem) {
        if(item.pdf != null){
            val intent = Intent(this, CreateExam2Activity::class.java)
            intent.putExtra("examItem", item)
            startActivity(intent)
        }else{
            val intent = Intent(this, CreateExamActivity::class.java)
            intent.putExtra("examItem", item)
            startActivity(intent)
        }



//        // Gọi API cập nhật bài kiểm tra
//        examService.updateExam(item.exam_id, item).enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    // Cập nhật thành công, có thể hiển thị thông báo hoặc cập nhật UI
//                    Toast.makeText(this@ExamActivity, "Cập nhật bài kiểm tra thành công", Toast.LENGTH_SHORT).show()
//
//                    // Cập nhật lại danh sách nếu cần thiết
//                    recyclerView.adapter?.notifyItemChanged(list.indexOf(item))
//                } else {
//                    Toast.makeText(this@ExamActivity, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                // Xử lý khi có lỗi kết nối hoặc lỗi khác
//                Toast.makeText(this@ExamActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    override fun onDeleteClick(item: ExamItem) {
        examService.deleteExam(item.exam_id!!).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    // Cập nhật danh sách và xóa item
                    val updatedList = examList.toMutableList()
                    updatedList.remove(item)

                    // Cập nhật adapter với danh sách mới
                    recyclerView.adapter?.let { adapter ->
                        if (adapter is ExamAdapter2) {
                            adapter.updateList(updatedList) // Cần thêm phương thức updateList trong adapter
                        }
                    }

                    Toast.makeText(this@ExamActivity, "Xóa thành công", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }


    override fun onStatusClick(item: ExamItem) {
        val status = if (item.status == 0) 1 else 0

        examService.updateVisibility(item.exam_id!!, status).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful){
                    // Cập nhật trạng thái của item
                    item.status = status

                    // Tìm vị trí của item trong examList
                    val position = examList.indexOf(item)

                    // Nếu vị trí hợp lệ, thông báo cho adapter rằng item đã thay đổi
                    if (position != -1) {
                        recyclerView.adapter?.notifyItemChanged(position)
                    }

                    Toast.makeText(this@ExamActivity, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ExamActivity, "Không thành công", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }


}