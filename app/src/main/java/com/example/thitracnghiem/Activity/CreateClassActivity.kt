package com.example.thitracnghiem.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.thitracnghiem.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreateClassActivity : AppCompatActivity() {
    private lateinit var edtTenLop: EditText
    private lateinit var tvMonhoc: AutoCompleteTextView
    private lateinit var tvLop: AutoCompleteTextView
    private lateinit var img: ImageView
    private lateinit var cardView: CardView
    private lateinit var cardView2: CardView
    private lateinit var btnCreateClass: Button
    private lateinit var db: FirebaseFirestore
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_class)

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

        tvMonhoc.setAdapter(arrayAdapter)
        tvLop.setAdapter(arrayAdapter2)

        db = FirebaseFirestore.getInstance()

        cardView.setOnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        btnCreateClass.setOnClickListener {
            createClass()
            finish()
        }
    }

    private fun createClass() {
        val tenLop = edtTenLop.text.toString()
        val monHoc = tvMonhoc.text.toString()
        val lop = tvLop.text.toString()

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

        selectedImageUri?.let {
            fileRef.putFile(it)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val classroom = hashMapOf(
                            "tenLop" to tenLop,
                            "monHoc" to monHoc,
                            "lop" to lop,
                            "imageUrl" to uri.toString()
                        )

                        db.collection("classrooms")
                            .add(classroom)
                            .addOnSuccessListener {document ->
                                val classId = document.id
                                updateUserClasses(classId)
                                Toast.makeText(this, "Lớp học đã được tạo thành công", Toast.LENGTH_SHORT).show()

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserClasses(classId: String) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("users").document(userId)
            .update("class", FieldValue.arrayUnion(classId))
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
