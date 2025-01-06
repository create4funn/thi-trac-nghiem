package com.example.thitracnghiem.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.ApiService.RetrofitVirusScan
import com.example.thitracnghiem.ApiService.VirusTotalResponse
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ExamItem
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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

                // Quét file PDF với VirusTotal
                scanPdfWithVirusTotal(uri)

//                val fileName = getFileName(uri)
//                pdfName.text = fileName
            }
        }
    }

    private fun scanPdfWithVirusTotal(uri: Uri) {
        // Hiển thị ProgressDialog khi bắt đầu quét file
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Đang quét tệp, vui lòng chờ...")
            setCancelable(false)
            show()
        }

        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_file.pdf")
        val outputStream = file.outputStream()
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()

        val requestFile = RequestBody.create(MediaType.parse("application/pdf"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val service = RetrofitVirusScan.getVirusTotalService()
        val call = service.uploadFile("023c7f0585e7ed45427137c211fdfa0626d283ab4b8fb6d3e3f3e37a081ac895", body)

        call.enqueue(object : Callback<VirusTotalResponse> {
            override fun onResponse(
                call: Call<VirusTotalResponse>,
                response: Response<VirusTotalResponse>
            ) {
                //progressDialog.dismiss() // Ẩn dialog sau khi nhận phản hồi

                if (response.isSuccessful) {
                    val scanId = response.body()?.data?.id
                    if (scanId != null) {
                        Log.d("test", "$scanId")
                        // Gọi hàm lấy kết quả quét
                        fetchScanResult(scanId, uri, progressDialog)
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@CreateExam2Activity,
                            "Không nhận được ID quét.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@CreateExam2Activity, "Quét thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VirusTotalResponse>, t: Throwable) {
                progressDialog.dismiss() // Ẩn dialog khi gặp lỗi
                Toast.makeText(
                    this@CreateExam2Activity,
                    "Lỗi khi quét file: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchScanResult(scanId: String, uri: Uri, progressDialog: ProgressDialog) {
        val service = RetrofitVirusScan.getVirusTotalService()

        // Trì hoãn 3 giây trước khi gọi API lấy kết quả
        Handler(Looper.getMainLooper()).postDelayed({
            val call = service.getScanResult("023c7f0585e7ed45427137c211fdfa0626d283ab4b8fb6d3e3f3e37a081ac895", scanId)
            call.enqueue(object : Callback<VirusTotalResponse> {
                override fun onResponse(
                    call: Call<VirusTotalResponse>,
                    response: Response<VirusTotalResponse>
                ) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        val result = response.body()
                        val stats = result?.data?.attributes?.stats
                        if (stats != null) {
                            if (stats.malicious > 0) {
                                Toast.makeText(
                                    this@CreateExam2Activity,
                                    "File độc hại, vui lòng chọn file khác.",
                                    Toast.LENGTH_LONG
                                ).show()
                                pdfName.text = ""
                            } else {
                                val fileName = getFileName(uri)
                                pdfName.text = fileName
                                Toast.makeText(
                                    this@CreateExam2Activity,
                                    "File an toàn.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CreateExam2Activity,
                                "Không nhận được kết quả quét.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@CreateExam2Activity,
                            "Kết quả quét không thành công.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<VirusTotalResponse>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@CreateExam2Activity,
                        "Lỗi khi lấy kết quả: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }, 5000) // Chờ 3 giây
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