package com.example.thitracnghiem.Activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.ExamService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ClassroomPagerAdapter
import com.example.thitracnghiem.adapter.ExamAdapter
import com.example.thitracnghiem.model.ClassItem
import com.example.thitracnghiem.model.ExamItem
import com.example.thitracnghiem.model.UserItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class ClassroomActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnCreateExam: Button
    private lateinit var className: TextView
    private lateinit var quanLyLop: TextView
    private lateinit var numRequest: TextView
    private lateinit var linearLayout: LinearLayout
    private lateinit var textview: TextView
    private var userList: List<UserItem> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)

        className = findViewById(R.id.tv_className)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Lấy classItem từ intent
        val classItem = intent.getSerializableExtra("classItem") as? ClassItem
            ?: throw IllegalArgumentException("ClassItem không được truyền vào")

        className.text = classItem.class_name  // Cập nhật tên lớp nếu cần

        // Thiết lập ViewPager với Adapter
        val pagerAdapter = ClassroomPagerAdapter(this, classItem)
        viewPager.adapter = pagerAdapter

        // Thiết lập TabLayout với ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Bài Thi" else "Thông Báo"
        }.attach()


        val role = getRole()


        if(role == 2){
            btnCreateExam = findViewById(R.id.btnCreateExam)
            linearLayout = findViewById(R.id.LinearLayoutAccept)
            quanLyLop = findViewById(R.id.tv_quanLyLop)
            numRequest = findViewById(R.id.numRequest)

            btnCreateExam.visibility = View.VISIBLE
            quanLyLop.visibility = View.VISIBLE



            btnCreateExam.setOnClickListener {
                showBottomSheet(classItem?.classroom_id!!)
            }
            // Kiểm tra xem có ai xin vào lớp không
            getStudentJoinRequest(classItem?.classroom_id!!)

            textview = findViewById(R.id.tv4setclick)
            textview.setOnClickListener{
                val intent = Intent(this, JoinRequestActivity::class.java)
                intent.putExtra("user_list", ArrayList(userList))
                intent.putExtra("classroom_id", classItem.classroom_id!!)
                startActivity(intent)
            }

            quanLyLop.setOnClickListener {
                val intent = Intent(this, ClassManagerActivity::class.java)
                intent.putExtra("classItem", classItem)
                intent.putExtra("user_list", ArrayList(userList))
                startActivity(intent)
            }
        }


    }

    private fun getStudentJoinRequest(class_id: Int){
        val classService = RetrofitClient.instance(this).create(ClassService::class.java)
        classService.getJoinRequest(class_id).enqueue(object : Callback<List<UserItem>> {
            override fun onResponse(call: Call<List<UserItem>>, response: Response<List<UserItem>>) {
                if (response.isSuccessful) {
                    userList = response.body()!!
                    linearLayout.visibility = View.VISIBLE
                    numRequest.text = userList.size.toString()
                } else {
                    linearLayout.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<List<UserItem>>, t: Throwable) {
                Log.e("RetrofitError", "Error: ${t.message}")
                Toast.makeText(this@ClassroomActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })

    }



    private fun getRole() : Int{
        // Logic to save results
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", null)
        return role!!.toInt()
    }

    private fun showBottomSheet(class_id: Int) {
        // Inflate the bottom sheet layout
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)

        // Create the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        // Find the TextViews for selection
        val optionOne: TextView = bottomSheetView.findViewById(R.id.optionOne)
        val optionTwo: TextView = bottomSheetView.findViewById(R.id.optionTwo)

        // Set click listeners for each option
        optionOne.setOnClickListener {
            val intent = Intent(this, CreateExamActivity::class.java)
            intent.putExtra("class_id", class_id)
            bottomSheetDialog.dismiss()
            startActivity(intent)
        }

        optionTwo.setOnClickListener {
            val intent = Intent(this, CreateExam2Activity::class.java)
            intent.putExtra("class_id", class_id)
            bottomSheetDialog.dismiss()
            startActivity(intent)
        }

        // Show the bottom sheet
        bottomSheetDialog.show()
    }


}