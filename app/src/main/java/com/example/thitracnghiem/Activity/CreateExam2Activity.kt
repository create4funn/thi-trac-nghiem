package com.example.thitracnghiem.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ExamItem

class CreateExam2Activity : AppCompatActivity() {

    private lateinit var edtExamName: EditText
    private lateinit var edtDuration: EditText
    private lateinit var edtNumOfQues: EditText
    private lateinit var pdfName: TextView
    private lateinit var btnChooseFile: Button
    private lateinit var btnCreateQues: Button

    private var selectedPdfUri: Uri? = null
    private val PDF_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_exam2)

        // Initialize EditText fields
        edtExamName = findViewById(R.id.edtExamName2)
        edtDuration = findViewById(R.id.edtDuration2)
        edtNumOfQues = findViewById(R.id.edtNumOfQues2)
        pdfName = findViewById(R.id.tvFilePdf)
        btnChooseFile = findViewById(R.id.btnChoosePdf)
        btnCreateQues = findViewById(R.id.btnCreateQues2)

        // Set click listener for choosing PDF file
        btnChooseFile.setOnClickListener {
            openPdfPicker()
        }

        // Handle the creation of exam questions

        val class_id = intent.getIntExtra("class_id", 0)
        val exam_item = intent.getSerializableExtra("examItem") as? ExamItem ?: null

        var flag = 1
        var pdfUrl = ""
        if(exam_item != null){
            edtExamName.setText(exam_item.exam_name)
            edtDuration.setText(exam_item.duration.toString())
            pdfName.text = exam_item.pdf!!.substringAfter("%2F").substringBefore("?")
            edtNumOfQues.setText(exam_item.numOfQues.toString())
            flag = 2
            pdfUrl = exam_item.pdf.toString()
        }

        //  ----->> CONTINUES <<----------
        btnCreateQues.setOnClickListener {
            if (validateInputs()) {

                val numOfQues = edtNumOfQues.text.toString().toInt()
                val examName = edtExamName.text.toString()
                val duration = edtDuration.text.toString().toInt()

                val bottomSheet = BottomSheetFragment(exam_item?.exam_id, numOfQues, examName, duration, class_id, selectedPdfUri, pdfUrl, pdfName.text.toString(), flag)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open PDF picker
    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PDF_REQUEST_CODE)
    }

    // Handle the result of the PDF picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedPdfUri = uri
                val fileName = getFileName(uri)
                pdfName.text = fileName
            }
        }
    }

    // Get file name from URI
    private fun getFileName(uri: Uri): String {
        var result = "Unknown File"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                result = cursor.getString(nameIndex)
            }
        }
        return result
    }


    // Validate input fields
    private fun validateInputs(): Boolean {
        val examName = edtExamName.text
        val duration = edtDuration.text
        val numOfQues = edtNumOfQues.text
        val pdf = pdfName.text
        return examName.isNotEmpty() && duration.isNotEmpty() && numOfQues.isNotEmpty() && pdf.isNotEmpty()
    }
}