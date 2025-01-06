package com.example.thitracnghiem.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.Activity.ClassroomActivity
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AllClassAdapter
import com.example.thitracnghiem.adapter.JoinedClassAdapter
import com.example.thitracnghiem.model.ClassItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassJoinedFragment : Fragment(), JoinedClassAdapter.OnItemClickListener {

    private lateinit var rcvJoinedClass : RecyclerView
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_joined, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.tv_class_joined)
        rcvJoinedClass = view.findViewById(R.id.rcvJoinedClass)
        rcvJoinedClass.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val student_id = getStudentId()
        getJoinedClasses(student_id)
    }

    private fun getJoinedClasses(student_id: Int) {
        val classService = RetrofitClient.instance(requireContext()).create(ClassService::class.java)
        classService.getJoinedClass(student_id).enqueue(object : Callback<List<ClassItem>> {
            override fun onResponse(call: Call<List<ClassItem>>, response: Response<List<ClassItem>>) {
                if (response.isSuccessful) {
                    val classList = response.body()!!

                    if (classList.isEmpty()) {
                        textView.visibility = View.GONE
                    } else {
                        rcvJoinedClass.adapter = JoinedClassAdapter(classList, this@ClassJoinedFragment)
                    }
                }
            }

            override fun onFailure(call: Call<List<ClassItem>>, t: Throwable) {
                Log.d("abc", "${t.message}")
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getStudentId() : Int{
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