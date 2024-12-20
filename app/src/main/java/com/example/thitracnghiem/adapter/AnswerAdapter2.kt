package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.QuestionItem

class AnswerAdapter2(private val questionList: List<QuestionItem>) : RecyclerView.Adapter<AnswerAdapter2.AnswerViewHolder>() {

    class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionNumber: TextView = itemView.findViewById(R.id.question_number)
        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroupAnswers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val question = questionList[position]

        // Hiển thị số câu hỏi
        holder.questionNumber.text = "Câu ${position + 1}:"


        holder.radioGroup.setOnCheckedChangeListener(null)  //Xóa lắng nghe sự kiện trước đó để tránh xót lại sự kiện cũ khi cuộn

        // Đặt trạng thái đã chọn của RadioGroup dựa trên giá trị is_correct của các Answer
        when (question.answers.indexOfFirst { it.is_correct == 1 }) {
            0 -> holder.radioGroup.check(R.id.radioButtonA)
            1 -> holder.radioGroup.check(R.id.radioButtonB)
            2 -> holder.radioGroup.check(R.id.radioButtonC)
            3 -> holder.radioGroup.check(R.id.radioButtonD)
            else -> holder.radioGroup.clearCheck()
        }

        // Lắng nghe sự kiện thay đổi của RadioGroup
        holder.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Reset 4 đáp án thành is_correct = 0
            question.answers.forEach { it.is_correct = 0 }

            // Cập nhật đáp án đã chọn
            when (checkedId) {
                R.id.radioButtonA -> question.answers[0].is_correct = 1
                R.id.radioButtonB -> question.answers[1].is_correct = 1
                R.id.radioButtonC -> question.answers[2].is_correct = 1
                R.id.radioButtonD -> question.answers[3].is_correct = 1
            }
        }
    }

    override fun getItemCount(): Int = questionList.size
}

