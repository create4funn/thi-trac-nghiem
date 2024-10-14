package com.example.thitracnghiem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.JoinResponse
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.JoinRequestAdapter
import com.example.thitracnghiem.model.UserItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class JoinRequestActivity : AppCompatActivity(), JoinRequestAdapter.OnItemClickListener {

    private lateinit var rcvJoinRequest : RecyclerView
    private lateinit var textView: TextView
    private lateinit var userList: ArrayList<UserItem>
    private lateinit var adapter: JoinRequestAdapter
    private var classroom_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_request)

        textView = findViewById(R.id.textView4)
        rcvJoinRequest = findViewById(R.id.recyclerviewJoinRequest)
        rcvJoinRequest.layoutManager = LinearLayoutManager(this)

        userList = (intent.getSerializableExtra("user_list") as? ArrayList<UserItem>)!!
        classroom_id = intent.getIntExtra("classroom_id", 0)
        val check = intent.getBooleanExtra("check", false)

        if(check) textView.text = "Danh sách thành viên"
        adapter = JoinRequestAdapter(userList, this, check)
        rcvJoinRequest.adapter = adapter
    }

    override fun onAcceptClick(item: UserItem) {
        respond(item.user_id!!, 1)
    }

    override fun onRejectClick(item: UserItem) {
        respond(item.user_id!!, 0)
    }

    override fun onKickClick(item: UserItem) {
        respond(item.user_id!!, 3)
    }


    private fun respond(student_id: Int, response: Int) {
        val joinResponse = JoinResponse(classroom_id, student_id, response)

        val apiService = RetrofitClient.retrofit.create(ClassService::class.java)

        apiService.respondJoinRequest(joinResponse).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Xóa item khỏi userList khi API trả về thành công
                    val position = userList.indexOfFirst { it.user_id == student_id }
                    if (position != -1) {
                        userList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

            }

        })
    }
}