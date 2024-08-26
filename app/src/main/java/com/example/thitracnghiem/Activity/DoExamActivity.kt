package com.example.thitracnghiem.Activity

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.CheckAnswerAdapter
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.QuestionItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DoExamActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private val questionsList = mutableListOf<QuestionItem>()

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
    private val userAnswers = mutableListOf<Int?>()
    private var sumQuestion : Int = 0
    private lateinit var countDownTimer: CountDownTimer
    private var totalTime: Long = 0
    private var timeLeft: Long = 0
    private var username : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_exam)

        initView()


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

        btnSubmit.setOnClickListener{
//            val dialog = SubmitDialogFragment(questionsList, userAnswers)
//            dialog.show(supportFragmentManager, "SubmitDialog")

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.check_answer_dialog)
            dialog.setTitle("Danh sách câu trả lời")
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = lp

            // truyền giá arr_Ques cho dialog
            val answerAdapter = CheckAnswerAdapter(this, questionsList, userAnswers)
            val gvLsQuestion = dialog.findViewById<GridView>(R.id.gridview)
            gvLsQuestion.adapter = answerAdapter

            val btnCancel: Button = dialog.findViewById(R.id.btn_Cancel)
            val btnFinish: Button = dialog.findViewById(R.id.btn_Finish)
            btnCancel.setOnClickListener { dialog.dismiss() }

            btnFinish.setOnClickListener {
                dialog.dismiss()
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


                btnAnswer.setOnClickListener {
                    // Logic to show detailed results
                    showAnswer()
                }
                btnSaveScore.setOnClickListener {
                    // Logic to save results
                    saveScore(score, tvCorrect.text.toString())
                }
                btnExit.setOnClickListener {
                    // Logic to exit
                    resultDialog.dismiss()
                    finish()

                }

                resultDialog.show()
            }


            dialog.show()
        }

        val questionIds = intent.getStringArrayListExtra("questions")
        sumQuestion = questionIds!!.size

        val duration = intent.getLongExtra("time", 0)
        totalTime = duration * 60000L
        startTimer(totalTime)


        val check = intent.getIntExtra("check",0)
        if (check == 0) {
            fetchQuestions(questionIds)
        } else {
            val dbHelper = ExamDatabaseHelper(this)
            val offlineQuestions = dbHelper.getQuestionsByIds(questionIds)
            questionsList.addAll(offlineQuestions)
            for (i in questionsList.indices) {
                userAnswers.add(null)
            }
            if (questionsList.isNotEmpty()) {
                displayQuestion(questionsList[0])
            }
        }
    }

    private fun saveScore(score : String, correctAnswers: String) {

        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(currentTime))

        val subject = intent.getStringExtra("subject")
        val idExam = intent.getStringExtra("idExam")
        if (subject == null) return
        val historyData = hashMapOf(
            "username" to username,
            "score" to score,
            "correctAnswers" to correctAnswers,
            "timestamp" to formattedDate
        )

        db = FirebaseFirestore.getInstance()
        db.collection("subjects").document(subject!!).collection("exams").document(idExam!!)
            .collection("histories").add(historyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Kết quả đã được lưu thành công", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lưu kết quả thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
        val correctAnswerAdapter = CheckAnswerAdapter(this, questionsList, userAnswers, showCorrectAnswers = true)
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
        btnNext = findViewById(R.id.txtNext)
        btnBack = findViewById(R.id.txtBack)
        tvCurrentQues = findViewById(R.id.txtCurentPosition)
        timer = findViewById(R.id.tvTimer)
        btnSubmit = findViewById(R.id.tvSubmit)
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

    private fun submitExam() {

        //

    }

    private fun fetchQuestions(questionIds: List<String>) {
        db = FirebaseFirestore.getInstance()
        for (questionId in questionIds) {
            db.collection("questions").document(questionId).get()
                .addOnSuccessListener { document ->
                    val text = document.getString("text") ?: ""
                    val listAnswer = document.get("options") as? List<String> ?: listOf()
                    val correct = document.getString("answer") ?: ""

                    val questionItem = QuestionItem(questionId, text, listAnswer, correct)
                    questionsList.add(questionItem)
                    userAnswers.add(null) // Initialize with null for each question

                    if (questionsList.size == 1) {
                        displayQuestion(questionsList[0])
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("DoExamActivity", "Error getting documents.", e)
                }
        }

        val user = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(user!!.uid).get().addOnSuccessListener {
            username = it.getString("Username").toString()
        }
    }


    private fun displayQuestion(questionItem: QuestionItem) {
        tvNum.text = "Câu ${currentQuestionIndex + 1}"
        tvCurrentQues.text = "${currentQuestionIndex + 1}/$sumQuestion"
        tvQuestion.text = questionItem.text
        radA.text = "A. ${questionItem.listAnswer[0]}"
        radB.text = "B. ${questionItem.listAnswer[1]}"
        radC.text = "C. ${questionItem.listAnswer[2]}"
        radD.text = "D. ${questionItem.listAnswer[3]}"
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

    private fun calculateResults(): Int{
        var correctAnswers = 0
        for (i in questionsList.indices) {
            val question = questionsList[i]
            val userAnswerIndex = userAnswers[i]

            if (userAnswerIndex != null && question.listAnswer[userAnswerIndex] == question.correct) {
                correctAnswers++
            }
        }
        return correctAnswers
    }
}
