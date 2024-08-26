package com.example.thitracnghiem.Activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.example.thitracnghiem.R

class BottomSheetFragment(
    private val numOfQues: Int,
    private val examName: String,
    private val duration: Int,
    private val classId: String
) : BottomSheetDialogFragment() {

    private lateinit var questions: MutableList<CreateQuestionFragment>
    private var callback: OnExamSavedListener? = null

    interface OnExamSavedListener {
        fun onExamSaved()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnExamSavedListener) {
            callback = context
        } else {
            throw RuntimeException("$context must implement OnExamSavedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
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

        questions = MutableList(numOfQues) { CreateQuestionFragment() }
        viewPager.adapter = QuestionsPagerAdapter(requireActivity(), questions)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Câu ${position + 1}"
        }.attach()

        btnDone.setOnClickListener {
            saveQuestionsAndExamToFirestore()
        }
    }

    private fun saveQuestionsAndExamToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        val questionRefs = mutableListOf<String>()

        for ((index, questionFragment) in questions.withIndex()) {
            val view = questionFragment.view ?: continue
            val questionText = view.findViewById<EditText>(R.id.edtQuestion).text.toString()
            val options = listOf(
                view.findViewById<EditText>(R.id.edtAnswer1).text.toString(),
                view.findViewById<EditText>(R.id.edtAnswer2).text.toString(),
                view.findViewById<EditText>(R.id.edtAnswer3).text.toString(),
                view.findViewById<EditText>(R.id.edtAnswer4).text.toString()
            )
            val correctAnswer = view.findViewById<EditText>(R.id.edtCorrectAnswer).text.toString()

            if (questionText.isEmpty() || options.any { it.isEmpty() } || correctAnswer.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin cho câu ${index + 1}", Toast.LENGTH_SHORT).show()
                return
            }

            val questionData = hashMapOf(
                "text" to questionText,
                "options" to options,
                "answer" to correctAnswer
            )

            val questionRef = db.collection("questions").document()
            questionRefs.add(questionRef.id)
            batch.set(questionRef, questionData)
        }

        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveExamToFirestore(questionRefs)
            } else {
                Toast.makeText(requireContext(), "Đã xảy ra lỗi khi lưu các câu hỏi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExamToFirestore(questionRefs: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val examData = hashMapOf(
            "examName" to examName,
            "duration" to duration,
            "numOfQues" to numOfQues,
            "questions" to questionRefs
        )

        db.collection("classrooms").document(classId)
            .collection("exams")
            .add(examData)
            .addOnSuccessListener {
                callback?.onExamSaved()
                Toast.makeText(requireContext(), "Bài kiểm tra đã được lưu thành công", Toast.LENGTH_SHORT).show()
                dismiss()

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Đã xảy ra lỗi khi lưu bài kiểm tra", Toast.LENGTH_SHORT).show()
            }
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