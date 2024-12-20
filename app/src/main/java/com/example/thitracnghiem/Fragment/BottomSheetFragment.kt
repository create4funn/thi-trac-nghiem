package com.example.thitracnghiem.Activity

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.thitracnghiem.ApiService.CreateExamRequest
import com.example.thitracnghiem.ApiService.ExamService
import com.example.thitracnghiem.ApiService.QuestionService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AnswerAdapter2
import com.example.thitracnghiem.model.Answer
import com.example.thitracnghiem.model.QuestionItem
import com.google.firebase.storage.FirebaseStorage
import okhttp3.ResponseBody
import retrofit2.*
import java.util.*

class BottomSheetFragment(private val exam_id: Int?, private val numOfQues: Int, private val examName: String, private val duration: Int, private val classId: Int, private val pdfUri: Uri?, private val pdfUrl: String?, private val fileName: String?, private val flag: Int) : BottomSheetDialogFragment() {

    private lateinit var questions: MutableList<CreateQuestionFragment>
    private var questionItems = mutableListOf<QuestionItem>()
    private var questionList : List<QuestionItem> = emptyList()
    private val examService = RetrofitClient.retrofit.create(ExamService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)
        val btnDone = view.findViewById<Button>(R.id.done)
        val rcvAnswer = view.findViewById<RecyclerView>(R.id.recyclerViewAnswers)
        rcvAnswer.layoutManager = LinearLayoutManager(context)

        // flag = 1 là tạo đề có pdf, flag = 2 -> sửa đề có pdf
        if(flag > 0){
            tabLayout.visibility = View.GONE
            viewPager.visibility = View.GONE
            rcvAnswer.visibility = View.VISIBLE

            if(flag==2){ //sửa đề
                getQuestionList(exam_id!!, rcvAnswer)
            }else{
                //khởi tạo questionList rỗng
                questionList = List(numOfQues) {
                    QuestionItem(null,null,
                        answers = List(4) {
                            Answer(null, null, 0)
                        }
                    )
                }
                rcvAnswer.adapter = AnswerAdapter2(questionList)
            }


            btnDone.setOnClickListener {
                getInput2(questionList)
            }
        }else{
            questions = MutableList(numOfQues) { CreateQuestionFragment() }
            viewPager.adapter = QuestionsPagerAdapter(requireActivity(), questions)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = "Câu ${position + 1}"
            }.attach()

            btnDone.setOnClickListener {
                getInput1()
            }
        }

    }

    private fun getInput1() {
        // Lặp qua từng fragment trong ViewPager
        for (fragment in questions) {
            val view = fragment.view ?: continue

            // Lấy dữ liệu từ EditText trong mỗi tab
            val questionText = view.findViewById<EditText>(R.id.edtQuestion).text.toString()
            val answer1 = view.findViewById<EditText>(R.id.edtAnswer1).text.toString()
            val answer2 = view.findViewById<EditText>(R.id.edtAnswer2).text.toString()
            val answer3 = view.findViewById<EditText>(R.id.edtAnswer3).text.toString()
            val answer4 = view.findViewById<EditText>(R.id.edtAnswer4).text.toString()

            // Lấy đáp án đúng từ RadioGroup
            val rgCorrect = view.findViewById<RadioGroup>(R.id.rgCorrect)
            val correctAnswer = when (rgCorrect.checkedRadioButtonId) {
                R.id.buttonA -> 1
                R.id.buttonB -> 2
                R.id.buttonC -> 3
                R.id.buttonD -> 4
                else -> -1
            }

            if (questionText.isEmpty() || answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty() || answer4.isEmpty() || correctAnswer == -1) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return
            }
            // Tạo danh sách các đáp án
            val answers = listOf(
                Answer(0, answer1, if (correctAnswer == 1) 1 else 0),
                Answer(0, answer2, if (correctAnswer == 2) 1 else 0),
                Answer(0, answer3, if (correctAnswer == 3) 1 else 0),
                Answer(0, answer4, if (correctAnswer == 4) 1 else 0)
            )

            // Tạo đối tượng QuestionItem và thêm vào danh sách
            val questionItem = QuestionItem(0, questionText, answers)
            questionItems.add(questionItem)
        }

        val createExamRequest = CreateExamRequest(
            examName, classId, null, duration, numOfQues, null,
            questions = questionItems
        )

        createExam(createExamRequest)
    }

    private fun getInput2(questionList : List<QuestionItem>) {
        val progressDialog = ProgressDialog(context).apply {
            setTitle("Đang tạo bài kiểm tra...")
            setMessage("Vui lòng chờ.")
            setCancelable(false)
        }
        var examRequest : CreateExamRequest

        if(pdfUri != null){
            progressDialog.show()
            // Tạo đường dẫn cho file PDF
            val storageRef = FirebaseStorage.getInstance().reference
            val fileRef = storageRef.child("exam_pdf/$fileName")
            pdfUri.let {
                fileRef.putFile(it)
                    .addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            progressDialog.cancel()
                            if(flag == 1){
                                examRequest = CreateExamRequest(
                                    examName, classId, null, duration, numOfQues, uri.toString(),
                                    questions = questionList
                                )
                                createExam(examRequest)
                            }else if(flag == 2){
                                examRequest = CreateExamRequest(
                                    examName, null, null, duration, numOfQues, uri.toString(),
                                    questions = questionList
                                )
                                updateExam(exam_id!!, examRequest)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Lỗi khi tải file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }else{
            examRequest = CreateExamRequest(
                examName, null, null, duration, numOfQues, pdfUrl.toString(),
                questions = questionList
            )
            updateExam(exam_id!!, examRequest)
        }

    }


    private fun createExam(createExamRequest : CreateExamRequest){

        examService.createExam(createExamRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Bài kiểm tra đã được tạo thành công", Toast.LENGTH_SHORT).show()
                    dismiss()
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra khi tạo bài kiểm tra", Toast.LENGTH_SHORT).show()
                    val errorBody = response.errorBody()?.string()
                    Log.d("abc", "${response.code()}, $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Không thể kết nối với server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateExam(exam_id: Int, updateRequest : CreateExamRequest){

        examService.updateExam(exam_id, updateRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cập nhật bài kiểm tra thành công", Toast.LENGTH_SHORT).show()
                    dismiss()
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra khi tạo bài kiểm tra", Toast.LENGTH_SHORT).show()
                    val errorBody = response.errorBody()?.string()
                    Log.d("abc", "${response.code()}, $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Không thể kết nối với server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getQuestionList(examId: Int, recyclerView: RecyclerView)  {
        val questionService = RetrofitClient.retrofit.create(QuestionService::class.java)

        questionService.getQuestionsByExamId(examId).enqueue(object : Callback<List<QuestionItem>> {
            override fun onResponse(call: Call<List<QuestionItem>>, response: Response<List<QuestionItem>>) {
                if (response.isSuccessful) {
                    questionList = response.body()!!
                    recyclerView.adapter = AnswerAdapter2(questionList)
                }
            }

            override fun onFailure(call: Call<List<QuestionItem>>, t: Throwable) {
                Log.e("API Error", "Failed to make API call", t)
            }
        })
    }

    inner class QuestionsPagerAdapter(fa: FragmentActivity, private val questions: List<CreateQuestionFragment>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = questions.size
        override fun createFragment(position: Int): Fragment = questions[position]
    }
}




class CreateQuestionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_item, container, false)
    }
}