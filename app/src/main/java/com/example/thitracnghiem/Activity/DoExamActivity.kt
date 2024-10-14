package com.example.thitracnghiem.Activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.thitracnghiem.ApiService.HistoryService
import com.example.thitracnghiem.ApiService.QuestionService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AnswerAdapter
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.HistoryItem
import com.example.thitracnghiem.model.QuestionItem
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoExamActivity : AppCompatActivity() {

    private var questionsList: List<QuestionItem> = emptyList()

    private lateinit var tvNum: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var radGroup: RadioGroup
    private lateinit var radA: RadioButton
    private lateinit var radB: RadioButton
    private lateinit var radC: RadioButton
    private lateinit var radD: RadioButton
    private lateinit var btnNext: TextView
    private lateinit var btnBack: TextView
    private lateinit var tvCurrentQues: TextView
    private lateinit var timer: TextView
    private lateinit var btnSubmit: TextView
    private var currentQuestionIndex = 0
    private var userAnswers = mutableListOf<Int?>()
    private var sumQuestion: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    private var totalTime: Long = 0
    private var timeLeft: Long = 0
    private var exam_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_exam)

        initView()
        exam_id = intent.getIntExtra("exam_id", -1)

        val check = intent.getIntExtra("check",0)
        if (check == 0) {
            getQuestionApi(exam_id)
        } else {
            val dbHelper = ExamDatabaseHelper(this)
            questionsList = dbHelper.getQuestionsForExam(exam_id)

            Log.d("abc", "$questionsList")
            sumQuestion = questionsList.size

//            questionsList.addAll(offlineQuestions)
            for (i in questionsList.indices) {
                userAnswers.add(null)
            }
            if (questionsList.isNotEmpty()) {
                displayQuestion(questionsList[0])
            }
        }

        val duration = intent.getIntExtra("time", 0)
        totalTime = duration * 60000L
        startTimer(totalTime)

        btnNext.setOnClickListener {
            displayNextQuestion()
        }
        btnBack.setOnClickListener {
            displayPreviousQuestion()
        }

        radA.setOnClickListener { saveUserAnswer() }
        radB.setOnClickListener { saveUserAnswer() }
        radC.setOnClickListener { saveUserAnswer() }
        radD.setOnClickListener { saveUserAnswer() }
//
        btnSubmit.setOnClickListener {

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.check_answer_dialog)
            dialog.setTitle("Danh sách câu trả lời")
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = lp

            //
            val answerAdapter = AnswerAdapter(this, questionsList, userAnswers)
            val gvLsQuestion = dialog.findViewById<GridView>(R.id.gridview)
            gvLsQuestion.adapter = answerAdapter

            val btnCancel: Button = dialog.findViewById(R.id.btn_Cancel)
            val btnFinish: Button = dialog.findViewById(R.id.btn_Finish)
            btnCancel.setOnClickListener { dialog.dismiss() }

            btnFinish.setOnClickListener {
                dialog.dismiss()
                submitExam()
            }

            dialog.show()
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
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(resultDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        resultDialog.window!!.attributes = lp

        val tvScore: TextView = resultDialog.findViewById(R.id.tvScore)
        val tvCorrect: TextView = resultDialog.findViewById(R.id.tvTrue)
        val tvTime: TextView = resultDialog.findViewById(R.id.tvThoiGian)
        val btnAnswer: Button = resultDialog.findViewById(R.id.btnAnswer)
        val btnSaveScore: Button = resultDialog.findViewById(R.id.btnSaveScore)
        val btnExit: Button = resultDialog.findViewById(R.id.btnExit)

        tvScore.text = "$score điểm"
        tvCorrect.text = "$correctAnswers/${questionsList.size}"
        val timeElapsed = totalTime - timeLeft
        val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed)
        val elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                TimeUnit.MINUTES.toSeconds(elapsedMinutes)
        tvTime.text = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)

        resultDialog.setCancelable(false) // Ngăn dialog tự động đóng khi chạm ra ngoài

        btnAnswer.setOnClickListener {
            // Logic to show detailed results
            showAnswer()
        }
        btnSaveScore.setOnClickListener {
            // Logic to save results
            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val jwtToken = sharedPref.getString("token", null)
            var user_id = ""
            var user_name = ""
            if (jwtToken != null) {
                // Giải mã JWT và lấy role
                val jwt = JWT(jwtToken)
                user_id = jwt.getClaim("userId").asString().toString()
                user_name = jwt.getClaim("username").asString().toString()
                Log.d("abc","$user_id   $user_name")
            }
            saveScore(user_name, score, tvCorrect.text.toString(), user_id.toInt(), exam_id)
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

                    if (questionsList.isNotEmpty()) {
                        sumQuestion = questionsList.size
                        userAnswers = MutableList(questionsList.size) { null }
                        displayQuestion(questionsList[0])
                    }
                } else {
                    Log.e("API Error", "Response was not successful")
                }
            }

            override fun onFailure(call: Call<List<QuestionItem>>, t: Throwable) {
                Log.e("API Error", "Failed to make API call", t)
            }
        })
    }


    private fun saveScore(username: String, score: String, proportion: String, user_id: Int, exam_id: Int) {

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val historyRequest = HistoryItem(
            username = username,
            proportion = proportion,
            score = score.toDouble(),
            time = currentDate,
            exam_id = exam_id,
            user_id = user_id
        )

        val historyService = RetrofitClient.retrofit.create(HistoryService::class.java)

        historyService.saveExamHistory(historyRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DoExamActivity, "Lưu điểm thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DoExamActivity, "Lưu điểm không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DoExamActivity, "Lỗi", Toast.LENGTH_SHORT).show()

                Log.e("SaveHistory", "Error: ${t.message}")
            }
        })
    }

    private fun showAnswer() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.check_answer_dialog)
        dialog.setTitle("Đáp án đúng")
        dialog.setCancelable(false)
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
        tvNum = findViewById(R.id.tvNum)
        tvQuestion = findViewById(R.id.tvQuestion)
        ivIcon = findViewById(R.id.ivIcon)
        radGroup = findViewById(R.id.radGroup)
        radA = findViewById(R.id.radA)
        radB = findViewById(R.id.radB)
        radC = findViewById(R.id.radC)
        radD = findViewById(R.id.radD)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        tvCurrentQues = findViewById(R.id.txtCurentPosition)
        timer = findViewById(R.id.tvTimer)
        btnSubmit = findViewById(R.id.btnSubmit)
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

    private fun displayQuestion(questionItem: QuestionItem) {
        tvNum.text = "Câu ${currentQuestionIndex + 1}"
        tvCurrentQues.text = "${currentQuestionIndex + 1}/$sumQuestion"
        tvQuestion.text = questionItem.question_text
        radA.text = "A. ${questionItem.answers[0].answer_text}"
        radB.text = "B. ${questionItem.answers[1].answer_text}"
        radC.text = "C. ${questionItem.answers[2].answer_text}"
        radD.text = "D. ${questionItem.answers[3].answer_text}"
        radGroup.clearCheck()

        // khôi phục đáp án đã chọn
        userAnswers[currentQuestionIndex]?.let {
            when (it) {
                0 -> radGroup.check(R.id.radA)
                1 -> radGroup.check(R.id.radB)
                2 -> radGroup.check(R.id.radC)
                3 -> radGroup.check(R.id.radD)
            }
        }
    }

    private fun saveUserAnswer() {
        val selectedOptionIndex = when (radGroup.checkedRadioButtonId) {
            R.id.radA -> 0
            R.id.radB -> 1
            R.id.radC -> 2
            R.id.radD -> 3
            else -> null
        }
        userAnswers[currentQuestionIndex] = selectedOptionIndex
    }

    private fun displayNextQuestion() {
        if (currentQuestionIndex < questionsList.size - 1) {
            currentQuestionIndex++
            displayQuestion(questionsList[currentQuestionIndex])
        }
    }

    private fun displayPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            displayQuestion(questionsList[currentQuestionIndex])
        }
    }

    private fun calculateResults(): Int {
        var correctAnswers = 0
        for (i in questionsList.indices) {
            val question = questionsList[i]
            val userAnswerIndex = userAnswers[i]

            if (userAnswerIndex != null && question.answers[userAnswerIndex].is_correct == 1) {
                correctAnswers++
            }
        }
        return correctAnswers
    }
}
