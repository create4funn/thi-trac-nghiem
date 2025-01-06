package com.example.thitracnghiem.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ClassItem
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CreateClassActivity : AppCompatActivity() {
    private lateinit var edtTenLop: EditText
    private lateinit var tvMonhoc: AutoCompleteTextView
    private lateinit var tvLop: AutoCompleteTextView
    private lateinit var img: ImageView
    private lateinit var cardView: CardView
    private lateinit var cardView2: CardView
    private lateinit var btnCreateClass: Button
    private var selectedImageUri: Uri? = null
    private lateinit var classService: ClassService
    private lateinit var classItem : ClassItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_class)

        classService = RetrofitClient.instance(this).create(ClassService::class.java)
        // check = true là tạo , false là sửa
        val check = intent.getBooleanExtra("check", false)
        if(!check){
            classItem = intent.getSerializableExtra("classItem") as ClassItem
        }


        edtTenLop = findViewById(R.id.createTenLop)
        tvMonhoc = findViewById(R.id.createMonHoc)
        tvLop = findViewById(R.id.createLop)
        img = findViewById(R.id.imgFromGallery)
        cardView = findViewById(R.id.cardView2)
        cardView2 = findViewById(R.id.cardViewPlus)
        btnCreateClass = findViewById(R.id.btnCreateClass)

        val monHoc = resources.getStringArray(R.array.list_mon_hoc)
        val lop = resources.getStringArray(R.array.list_lop)

        val arrayAdapter = ArrayAdapter(this, R.layout.item_dropdown, monHoc)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.item_dropdown, lop)



        if(!check){
            btnCreateClass.text = "cập nhật"
            edtTenLop.setText(classItem.class_name)
            tvMonhoc.setText(classItem.subject_name)
            tvLop.setText(classItem.grade)
            Picasso.get().load(classItem.class_img).into(img)
        }
        tvMonhoc.setAdapter(arrayAdapter)
        tvLop.setAdapter(arrayAdapter2)

        cardView.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        btnCreateClass.setOnClickListener {
            if(check){
                createClass()
            }else{
                update(classItem)
            }
        }
    }

    private fun createClass() {
        val tenLop = edtTenLop.text.toString()
        val monHoc = tvMonhoc.text.toString()
        val lop = tvLop.text.toString()
        val teacher_id = intent.getIntExtra("teacher_id", 0)
        if (tenLop.isEmpty() || monHoc.isEmpty() || monHoc == "Môn học" || lop.isEmpty() || lop == "Lớp") {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn một ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        // Tải ảnh lên Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("class_images/${UUID.randomUUID()}.jpg")


        val progressDialog = ProgressDialog(this).apply {
            setTitle("Đang tạo lớp học ...")
            setMessage("Vui lòng chờ.")
            setCancelable(false)
            show()
        }
        selectedImageUri?.let {
            fileRef.putFile(it)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->

                        val classRequest = ClassItem(
                            classroom_id = null,
                            class_name = tenLop,
                            grade = lop,
                            subject_name = monHoc,
                            class_img = uri.toString(),
                            teacher_id = teacher_id,
                            null,null
                        )
                        classService.createClass(classRequest).enqueue(object :
                            Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@CreateClassActivity, "Tạo lớp thành công", Toast.LENGTH_SHORT).show()
                                    progressDialog.cancel()
                                    finish()
                                } else {
                                    Toast.makeText(this@CreateClassActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(this@CreateClassActivity, "Lỗi", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun update(classItem : ClassItem){
        val tenLop = edtTenLop.text.toString()
        val monHoc = tvMonhoc.text.toString()
        val lop = tvLop.text.toString()

        if (tenLop.isEmpty() || monHoc.isEmpty() || lop.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val classRequest = ClassItem(classItem.classroom_id, tenLop, lop, monHoc, null, null, null, null)
        classService.updateClass(classRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateClassActivity, "Sửa thành công", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateClassActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CreateClassActivity, "Lỗi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            img.setImageURI(selectedImageUri)
            cardView2.visibility = View.GONE
        }
    }
}
