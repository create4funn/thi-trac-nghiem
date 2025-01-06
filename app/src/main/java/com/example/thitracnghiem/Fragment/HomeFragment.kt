package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.thitracnghiem.Activity.DoExamActivity
import com.example.thitracnghiem.Activity.ExamActivity
import com.example.thitracnghiem.Activity.MainActivity
import com.example.thitracnghiem.ApiService.ExamService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.ApiService.SubjectService
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.SubjectAdapter
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.SubjectItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), SubjectAdapter.OnSubjectClickListener {
    private lateinit var subjectService: SubjectService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubjectAdapter
    private var subjectList: List<SubjectItem> = emptyList()

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

        val username_tv: TextView = view.findViewById(R.id.username_home)


        // Nhận username từ Bundle
        arguments?.let {
            username_tv.text = it.getString("username_key").toString()
        }

        recyclerView = view.findViewById(R.id.subjectsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        subjectService = RetrofitClient.instance(requireContext()).create(SubjectService::class.java)

        getSubjectsApi()

    }

    private fun getSubjectsApi() {
        subjectService.getSubjects().enqueue(object : Callback<List<SubjectItem>> {
            override fun onResponse(call: Call<List<SubjectItem>>, response: Response<List<SubjectItem>>) {
                if (response.isSuccessful) {
                    val subjects = response.body()
//                    Log.d("abc","$subjects")

                    if (subjects != null) {
                        // Cập nhật RecyclerView với danh sách môn học
                        recyclerView.adapter = SubjectAdapter(subjects, this@HomeFragment)
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi lấy danh sách môn học", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SubjectItem>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onSubjectClick(item: SubjectItem) {
        val intent = Intent(context, ExamActivity::class.java)
        intent.putExtra("subject_id",item.subject_id)
        startActivity(intent)
    }
}