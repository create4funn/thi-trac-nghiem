package com.example.thitracnghiem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.QuestionItem

class AnswerAdapter(
    context: Context,
    private val questions: List<QuestionItem>,
    private val userAnswers: List<Int?>,
    private val showCorrectAnswers: Boolean = false
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return questions.size
    }

    override fun getItem(position: Int): Any {
        return questions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_gridview_list_answer, null)
            holder = ViewHolder()
            holder.tvNumAns = view.findViewById(R.id.tv_num_answer)
            holder.tvYourAns = view.findViewById(R.id.tv_your_answer)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val question = questions[position]
        holder.tvNumAns.text = "Câu ${position + 1}:"

        if (showCorrectAnswers) {
            // Hiển thị đáp án đúng
            var correctAnswer = ""
            for(i in question.answers.indices){
                if(question.answers[i].is_correct == 1){
                    correctAnswer = when(i){
                        0 -> "A"
                        1 -> "B"
                        2 -> "C"
                        3 -> "D"
                        else -> ""
                    }
                }
            }
            holder.tvYourAns.text = correctAnswer
        } else {
            // Hiển thị câu trả lời của người dùng
            val selectedAnswerIndex = userAnswers[position]
            val selectedAnswer = if (selectedAnswerIndex != null && selectedAnswerIndex >= 0) {
                when (selectedAnswerIndex) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    3 -> "D"
                    else -> ""
                }
            } else {
                ""
            }
            holder.tvYourAns.text = selectedAnswer
        }

        return view
    }

    private class ViewHolder {
        lateinit var tvNumAns: TextView
        lateinit var tvYourAns: TextView
    }
}
