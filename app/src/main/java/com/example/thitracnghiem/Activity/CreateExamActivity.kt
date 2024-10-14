package com.example.thitracnghiem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.thitracnghiem.R

class CreateExamActivity : AppCompatActivity() {
    private lateinit var edtExamName: EditText
    private lateinit var edtDuration: EditText
    private lateinit var edtNumOfQues: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_exam)

        // Initialize EditText fields
        edtExamName = findViewById(R.id.edtExamName)
        edtDuration = findViewById(R.id.edtDuration)
        edtNumOfQues = findViewById(R.id.edtNumOfQues)


        val btnCreateQues = findViewById<Button>(R.id.btnCreateQues)
        val class_id = intent.getIntExtra("class_id", 0)
        btnCreateQues.setOnClickListener {
            if (validateInputs()) {
                // Proceed with your logic here
                val numOfQues = edtNumOfQues.text.toString().toInt()
                val examName = edtExamName.text.toString()
                val duration = edtDuration.text.toString().toInt()
                val bottomSheet = BottomSheetFragment(null,numOfQues,examName, duration, class_id, null, null, null,0)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)

            } else {
                Toast.makeText(this, "vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val examName = edtExamName.text.toString()
        val duration = edtDuration.text.toString()
        val numOfQues = edtNumOfQues.text.toString()

        return examName.isNotEmpty() && duration.isNotEmpty() && numOfQues.isNotEmpty()
    }

}
