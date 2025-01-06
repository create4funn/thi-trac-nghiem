package com.example.thitracnghiem.Fragment


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.thitracnghiem.Activity.MainActivity
import com.example.thitracnghiem.Activity.UpdateAccountActivity
import com.example.thitracnghiem.ApiService.AuthService
import com.example.thitracnghiem.ApiService.ChangePassword
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AllClassAdapter
import com.example.thitracnghiem.model.ClassItem
import com.example.thitracnghiem.model.UserItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {
    private lateinit var email: TextView
    private lateinit var uname: TextView
    private var user_id = 0
    private lateinit var userItem : UserItem
    private lateinit var userService: AuthService // Sử dụng lateinit thay vì khởi tạo sớm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val logOut: TextView = view.findViewById(R.id.logOut)
        userService = RetrofitClient.instance(requireContext()).create(AuthService::class.java)
        // Nhận user_id từ Bundle
        arguments?.let {
           user_id = it.getString("userId").toString().toInt()
        }

        email = view.findViewById(R.id.profile_email)
        uname = view.findViewById(R.id.profile_username)

        val btnUpdate = view.findViewById<TextView>(R.id.updateInfo)
        val changePassword = view.findViewById<TextView>(R.id.changePassword)

        getUserInfo()

        logOut.setOnClickListener {
            val activity = activity as MainActivity
                activity.logout()
        }

        btnUpdate.setOnClickListener {
            val intent = Intent(context, UpdateAccountActivity::class.java)
            intent.putExtra("userItem", userItem)
            startActivity(intent)
        }

        changePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thay đổi mật khẩu")

        // Inflate custom layout cho dialog
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)

        val edtOldPassword = view.findViewById<EditText>(R.id.edtOldPassword)
        val edtNewPassword = view.findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirmPassword = view.findViewById<EditText>(R.id.edtConfirmPassword)

        builder.setView(view)

        // Xử lý sự kiện khi người dùng nhấn nút "Thay đổi"
        builder.setPositiveButton("Thay đổi") { dialog, _ ->
            val oldPassword = edtOldPassword.text.toString()
            val newPassword = edtNewPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            if(newPassword.length < 6){
                Toast.makeText(context, "mật khẩu cần tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
            }else if (newPassword != confirmPassword) {
                Toast.makeText(context, "mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
            } else{
                val changePassword = ChangePassword(oldPassword, newPassword)
                changePassword(changePassword)
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun changePassword(changePassword: ChangePassword) {
        userService.changePassword(user_id, changePassword).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Toast.makeText(context, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserInfo() {

        userService.getUser(user_id).enqueue(object : Callback<UserItem> {
            override fun onResponse(call: Call<UserItem>, response: Response<UserItem>) {
                if(response.isSuccessful){
                    userItem = response.body()!!
                    email.text = userItem.email
                    uname.text = userItem.username
                }
            }
            override fun onFailure(call: Call<UserItem>, t: Throwable) {
            }
        })
    }

}