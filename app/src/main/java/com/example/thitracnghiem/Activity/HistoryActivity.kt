// HistoryActivity.kt
package com.example.thitracnghiem.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.ApiService.HistoryService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.HistoryAdapter
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.HistoryItem
import com.google.firebase.firestore.FirebaseFirestore
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rcvScored: RecyclerView
    private lateinit var btnExcel : Button
    private var historyList : List<HistoryItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rcvScored = findViewById(R.id.rcvScored)
        btnExcel = findViewById(R.id.btnExcel)

        rcvScored.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        historyAdapter = HistoryAdapter(historyList)
        rcvScored.adapter = historyAdapter

        val user_id = getUser()
        val exam_id = intent.getIntExtra("exam_id", -1)
        val exam_name = intent.getStringExtra("exam_name")

        val check = intent.getBooleanExtra("check", false)
        if(check){
            fetchHistoryClass(exam_id)
            btnExcel.visibility = View.VISIBLE

        }else{
            fetchHistory(user_id, exam_id)
        }
        btnExcel.setOnClickListener {
            if (historyList.isNotEmpty()) {
                exportToExcel(historyList, exam_name.toString())
            } else {
                Toast.makeText(this, "Danh sách lịch sử trống", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getUser() : Int{
        // Logic to save results
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)
        return userId!!.toInt()
    }

    private fun fetchHistoryClass(examId: Int) {
        val historyService = RetrofitClient.retrofit.create(HistoryService::class.java)

        historyService.getHistoryByExamId(examId).enqueue(object : Callback<List<HistoryItem>> {
            override fun onResponse(call: Call<List<HistoryItem>>, response: Response<List<HistoryItem>>) {
                if (response.isSuccessful) {
                    // Lấy danh sách lịch sử thi từ response body
                    historyList = response.body()!!
                    rcvScored.adapter = HistoryAdapter(historyList)

                    historyAdapter.notifyDataSetChanged() // Cập nhật adapter để hiển thị dữ liệu mới
                } else {
                    Toast.makeText(this@HistoryActivity, "Lỗi khi lấy danh sách lịch sử thi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<HistoryItem>>, t: Throwable) {
                Toast.makeText(this@HistoryActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchHistory(userId: Int, examId: Int) {
        val historyService = RetrofitClient.retrofit.create(HistoryService::class.java)

        historyService.getHistoryByUserAndExam(userId, examId).enqueue(object : Callback<List<HistoryItem>> {
            override fun onResponse(call: Call<List<HistoryItem>>, response: Response<List<HistoryItem>>) {
                if (response.isSuccessful) {
                    // Lấy danh sách lịch sử thi từ response body
                    historyList = response.body()!!
                    rcvScored.adapter = HistoryAdapter(historyList)

                    historyAdapter.notifyDataSetChanged() // Cập nhật adapter để hiển thị dữ liệu mới
                } else {
                    Toast.makeText(this@HistoryActivity, "Lỗi khi lấy danh sách lịch sử thi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<HistoryItem>>, t: Throwable) {
                Toast.makeText(this@HistoryActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exportToExcel(historyList: List<HistoryItem>, exam_name: String) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("$exam_name")

        // Tạo header
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Họ Tên")
        headerRow.createCell(1).setCellValue("Số câu đúng")
        headerRow.createCell(2).setCellValue("Điểm số")
        headerRow.createCell(3).setCellValue("Thời gian")

        // Ghi dữ liệu vào sheet
        for ((index, item) in historyList.withIndex()) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(item.username)
            row.createCell(1).setCellValue(item.proportion)
            row.createCell(2).setCellValue(item.score)
            row.createCell(3).setCellValue(item.time)
        }

        // Lưu file
        val fileName = "Kết quả bài thi $exam_name.xlsx"
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filePath = File(downloadDir, fileName)

        FileOutputStream(filePath).use { outputStream ->
            workbook.write(outputStream)
        }
        workbook.close()
        //Log.d("abc", "${filePath.absolutePath}")
        Toast.makeText(this, "File Excel đã được lưu tại: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
    }

}
