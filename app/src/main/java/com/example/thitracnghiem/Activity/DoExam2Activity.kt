package com.example.thitracnghiem.Activity

import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.ApiService.HistoryService
import com.example.thitracnghiem.ApiService.QuestionService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AnswerAdapter
import com.example.thitracnghiem.adapter.AnswerAdapter2
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.Answer
import com.example.thitracnghiem.model.HistoryItem
import com.example.thitracnghiem.model.QuestionItem
import com.github.barteksc.pdfviewer.PDFView
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

class DoExam2Activity : AppCompatActivity() {
    private var questionsList: List<QuestionItem> = emptyList()
    private lateinit var questionListUser : List<QuestionItem>
    private lateinit var radGroup: RadioGroup
    private lateinit var pdfView : PDFView
    private lateinit var recyclerView: RecyclerView
    private lateinit var timer: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnBack: ImageView
    private var userAnswers = mutableListOf<Int?>()
    private lateinit var countDownTimer: CountDownTimer
    private var totalTime: Long = 0
    private var timeLeft: Long = 0
    private var exam_id = 0
    private var url_pdf = ""
    private var numOfQues = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_exam2)

        initView()
        exam_id = intent.getIntExtra("exam_id", -1)
        url_pdf = intent.getStringExtra("pdf").toString()
        numOfQues = intent.getIntExtra("numOfQues",-1)

        // khởi tạo view
        RetrievePDFFromURL(pdfView).execute(url_pdf)

        recyclerView.layoutManager = LinearLayoutManager(this)

        //khởi tạo list
        questionListUser = List(numOfQues) {
            QuestionItem(null,null,
                answers = List(4) {
                    Answer(null, null, 0)
                }
            )
        }
        recyclerView.adapter = AnswerAdapter2(questionListUser)

        getQuestionApi(exam_id)

        val duration = intent.getIntExtra("time", 0)
        totalTime = duration * 60000L
        startTimer(totalTime)

        btnSubmit.setOnClickListener {
            submitExam()
        }
    }

    class RetrievePDFFromURL(pdfView: PDFView) : AsyncTask<String, Void, InputStream>() {

        val mypdfView: PDFView = pdfView

        override fun doInBackground(vararg params: String?): InputStream? {

            var inputStream: InputStream? = null
            try {
                val url = URL(params.get(0))

                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }

            catch (e: Exception) {
                e.printStackTrace()
                return null;
            }
            return inputStream;
        }

        override fun onPostExecute(result: InputStream?) {
            mypdfView.fromStream(result).load()
        }
    }


    private fun submitExam(){
        countDownTimer.cancel()
        val correctAnswers = calculateResults()
        val totalQuestions = questionsList.size
        var temp = correctAnswers.toFloat() * 10 / totalQuestions.toFloat()
        val score = "%.1f".format(temp)
        val resultDialog = Dialog(this)
        resultDialog.setContentView(R.layout.result_dialog)
        resultDialog.setTitle("Kết quả thi")
        resultDialog.setCancelable(false) // Ngăn dialog tự động đóng khi chạm ra ngoài
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(resultDialog.window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//        resultDialog.window!!.attributes = lp

        val tvScore: TextView = resultDialog.findViewById(R.id.tvScore)
        val tvCorrect: TextView = resultDialog.findViewById(R.id.tvTrue)
        val tvTime: TextView = resultDialog.findViewById(R.id.tvThoiGian)
        val btnAnswer: Button = resultDialog.findViewById(R.id.btnAnswer)
        val btnExit: Button = resultDialog.findViewById(R.id.btnExit)

        tvScore.text = "$score điểm"
        tvCorrect.text = "$correctAnswers/${questionsList.size}"
        val timeElapsed = totalTime - timeLeft
        val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed)
        val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                TimeUnit.MINUTES.toSeconds(elapsedMinutes)
        tvTime.text = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)

        // Lưu điểm
        saveScore(score, tvCorrect.text.toString())

        btnAnswer.setOnClickListener {
            // Logic to show detailed results
            showAnswer()
        }


        btnExit.setOnClickListener {

            resultDialog.dismiss()
            finish()
        }

        resultDialog.show()
    }

    private fun getQuestionApi(examId: Int) {
        val questionService = RetrofitClient.retrofit.create(QuestionService::class.java)
        questionService.getQuestionsByExamId(examId).enqueue(object : Callback<List<QuestionItem>> {
            override fun onResponse(
                call: Call<List<QuestionItem>>,
                response: Response<List<QuestionItem>>
            ) {
                if (response.isSuccessful) {
                    questionsList = response.body()!!
                } else {
                    Log.e("API Error", "Response was not successful")
                }
            }

            override fun onFailure(call: Call<List<QuestionItem>>, t: Throwable) {
                Log.e("API Error", "Failed to make API call", t)
            }
        })
    }


    private fun saveScore(score: String, proportion: String) {

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val user_id = sharedPref.getString("userId", null).toString()
        val user_name = sharedPref.getString("username", null).toString()


        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val historyRequest = HistoryItem(
            username = user_name,
            proportion = proportion,
            score = score.toDouble(),
            time = currentDate,
            exam_id = exam_id,
            user_id = user_id.toInt()
        )

        val historyService = RetrofitClient.retrofit.create(HistoryService::class.java)

        historyService.saveExamHistory(historyRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@DoExam2Activity, "Lưu điểm thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DoExam2Activity, "Lưu điểm không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DoExam2Activity, "Lỗi", Toast.LENGTH_SHORT).show()

                Log.e("SaveHistory", "Error: ${t.message}")
            }
        })
    }

    private fun showAnswer() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.check_answer_dialog)
        dialog.setTitle("Đáp án đúng")
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp

        // Sử dụng adapter mới để hiển thị đáp án đúng
        val correctAnswerAdapter =
            AnswerAdapter(this, questionsList, userAnswers, showCorrectAnswers = true)
        val gvLsQuestion = dialog.findViewById<GridView>(R.id.gridview)
        gvLsQuestion.adapter = correctAnswerAdapter

        val btnCancel: Button = dialog.findViewById(R.id.btn_Cancel)
        val btnFinish: Button = dialog.findViewById(R.id.btn_Finish)
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnFinish.visibility = View.GONE
        dialog.show()
    }

    private fun initView() {
        pdfView = findViewById(R.id.pdfView)
        recyclerView = findViewById(R.id.rcvAnswer)
        timer = findViewById(R.id.tvTimer2)
        btnSubmit = findViewById(R.id.btn_submit2)
        btnBack = findViewById(R.id.buttonBack)
    }

    private fun startTimer(duration: Long) {
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                val timeLeftFormatted = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                )
                timer.text = timeLeftFormatted
            }

            override fun onFinish() {
                // Handle exam end
                timer.text = "00:00"
                submitExam()
            }
        }.start()
    }

    private fun calculateResults(): Int {
        var correctAnswers = 0
        for (i in questionsList.indices) {
            for(j in questionsList[i].answers.indices){
                if(questionsList[i].answers[j].is_correct == questionListUser[i].answers[j].is_correct &&
                    questionsList[i].answers[j].is_correct == 1){
                    correctAnswers++
                }
            }
        }
        return correctAnswers
    }
}