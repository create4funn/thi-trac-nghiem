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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AllClassAdapter
import com.example.thitracnghiem.model.ClassItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllClassFragment : Fragment(), AllClassAdapter.OnItemClickListener {

    private val classService = RetrofitClient.retrofit.create(ClassService::class.java)
    private lateinit var rcvAllClass : RecyclerView
    private lateinit var textView: TextView
    private var student_id = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.tv_allclass)
        rcvAllClass = view.findViewById(R.id.rcvAllClass)
        rcvAllClass.layoutManager = LinearLayoutManager(context)
        student_id = getStudentId()

        getAllClasses()

    }

    private fun getAllClasses() {

        classService.getAllClasses(student_id).enqueue(object : Callback<List<ClassItem>> {
            override fun onResponse(call: Call<List<ClassItem>>, response: Response<List<ClassItem>>) {
                if (response.isSuccessful) {
                    val classList = response.body()!!

                    if (classList.isEmpty()) {
                        textView.visibility = View.GONE
                    } else {
                        textView.visibility = View.VISIBLE
                        rcvAllClass.adapter = AllClassAdapter(classList, this@AllClassFragment)
                    }

                }
            }

            override fun onFailure(call: Call<List<ClassItem>>, t: Throwable) {
                // Handle error
            }
        })
    }



    override fun onItemClick(item: ClassItem) {

        showJoinClassDialog(item.classroom_id!!)
    }

    private fun showJoinClassDialog(classroom_id : Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tham gia lớp học")
        builder.setMessage("Bạn có muốn tham gia lớp học này không?")

        // Nút Đồng ý
        builder.setPositiveButton("Đồng ý") { dialog, which ->
            // Xử lý khi người dùng chọn Đồng ý
            sendRequest(classroom_id)
            dialog.dismiss()
        }

        // Nút Hủy
        builder.setNegativeButton("Hủy") { dialog, which ->
            // Đóng dialog
            dialog.dismiss()
        }

        // Hiển thị dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sendRequest(classroom_id: Int){
        classService.studentJoinRequest(classroom_id, student_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã gửi yêu cầu tham gia lớp", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "Không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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
}