package com.example.thitracnghiem.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.Activity.ClassroomActivity
import com.example.thitracnghiem.Activity.CreateClassActivity
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AllClassAdapter
import com.example.thitracnghiem.adapter.JoinedClassAdapter
import com.example.thitracnghiem.model.ClassItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClrTeacherFragment : Fragment(), AllClassAdapter.OnItemClickListener {

    private lateinit var recyclerViewClasses: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clr_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateClass = view.findViewById<Button>(R.id.btnCreateClass1)
        recyclerViewClasses = view.findViewById(R.id.recyclerView)

        recyclerViewClasses.layoutManager = LinearLayoutManager(context)

        val teacher_id = getTeacherId()
        getClasses(teacher_id)

        btnCreateClass.setOnClickListener {
            val intent = Intent(context, CreateClassActivity::class.java)
            intent.putExtra("teacher_id", teacher_id)
            intent.putExtra("check", true)
            startActivity(intent)
        }

        val swipeRefreshLayout : SwipeRefreshLayout = view.findViewById(R.id.refreshClassTeacher)
        swipeRefreshLayout.setOnRefreshListener {
            getClasses(teacher_id)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getClasses(teacher_id: Int) {
        val classService = RetrofitClient.retrofit.create(ClassService::class.java)
        classService.getClassByTeacher(teacher_id).enqueue(object : Callback<List<ClassItem>> {
            override fun onResponse(call: Call<List<ClassItem>>, response: Response<List<ClassItem>>) {
                if (response.isSuccessful) {
                    val classList = response.body()!!
                    recyclerViewClasses.adapter = AllClassAdapter(classList, this@ClrTeacherFragment)
                }
            }

            override fun onFailure(call: Call<List<ClassItem>>, t: Throwable) {
                Log.d("abc", "${t.message}")
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTeacherId() : Int{
        // Logic to save results
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val user_id = sharedPref.getString("userId", null)

        return user_id!!.toInt()
    }

    override fun onItemClick(item: ClassItem) {
        val intent = Intent(context, ClassroomActivity::class.java)
        intent.putExtra("classItem", item)
        startActivity(intent)
    }
}
