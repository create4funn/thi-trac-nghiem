package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.thitracnghiem.Activity.DoExam2Activity
import com.example.thitracnghiem.Activity.DoExamActivity
import com.example.thitracnghiem.Activity.HistoryActivity
import com.example.thitracnghiem.ApiService.ExamService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ExamAdapter
import com.example.thitracnghiem.model.ClassItem
import com.example.thitracnghiem.model.ExamItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExamTabFragment : Fragment(), ExamAdapter.OnItemClickListener {

    private lateinit var rcvExam: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var classItem: ClassItem
    private var examList: List<ExamItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exam_tab, container, false)
        rcvExam = view.findViewById(R.id.rcvExamClass)
        swipeRefreshLayout = view.findViewById(R.id.refreshExam)
        rcvExam.layoutManager = LinearLayoutManager(context)
        rcvExam.adapter = ExamAdapter(examList, this)

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            fetchExams()
            swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Lấy classItem từ arguments
        arguments?.let {
            classItem = it.getSerializable("classItem") as ClassItem
            fetchExams()
        }
    }

    private fun fetchExams() {
        val examService = RetrofitClient.retrofit.create(ExamService::class.java)
        examService.getExamsByClass(classItem.classroom_id!!).enqueue(object : Callback<List<ExamItem>> {
            override fun onResponse(call: Call<List<ExamItem>>, response: Response<List<ExamItem>>) {
                if (response.isSuccessful) {
                    examList = response.body()!!
                    rcvExam.adapter = ExamAdapter(examList, this@ExamTabFragment)
                } else {
                    Toast.makeText(context, "Lỗi khi lấy danh sách bài thi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ExamItem>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveClick(item: ExamItem) {
        //Toast.makeText(this, "Save: ${item.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDoExamClick(item: ExamItem) {
        Log.d("abc", "$item")
        if(item.pdf == null){
            val intent = Intent(context, DoExamActivity::class.java)
            intent.putExtra("exam_id", item.exam_id)
            intent.putExtra("time", item.duration)
            startActivity(intent)
        }else{
            val intent = Intent(context, DoExam2Activity::class.java)
            intent.putExtra("exam_id", item.exam_id)
            intent.putExtra("time", item.duration)
            intent.putExtra("numOfQues", item.numOfQues)
            intent.putExtra("pdf", item.pdf)
            startActivity(intent)
        }

    }

    override fun onHistory(item: ExamItem) {
        val intent = Intent(context, HistoryActivity::class.java)
        intent.putExtra("exam_id", item.exam_id)
        intent.putExtra("exam_name", item.exam_name)
        intent.putExtra("check", true)
        startActivity(intent)
    }
}



