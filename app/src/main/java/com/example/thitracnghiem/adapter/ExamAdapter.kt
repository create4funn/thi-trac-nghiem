package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ExamItem

class ExamAdapter(private val list:List<ExamItem>, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    interface OnItemClickListener {
        fun onSaveClick(item: ExamItem)
        fun onDoExamClick(item: ExamItem)
        fun onHistory(item: ExamItem)
    }

    class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.tv_examTitle)
        val num : TextView = itemView.findViewById(R.id.tv_numOfQues)
        val time : TextView = itemView.findViewById(R.id.tv_duration)
        val btnSave: TextView = itemView.findViewById(R.id.tv_save)
        val btnDoExam: TextView = itemView.findViewById(R.id.tv_do)
        val btnHistory: TextView = itemView.findViewById(R.id.tv_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_exam, parent, false)
        return ExamViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val currentItem = list[position]
        holder.name.text = currentItem.name
        holder.num.text = "${currentItem.numOfQues} câu"
        holder.time.text = "${currentItem.duration} phút"

        holder.btnSave.setOnClickListener {
            itemClickListener.onSaveClick(currentItem)
        }

        holder.btnDoExam.setOnClickListener {
            itemClickListener.onDoExamClick(currentItem)
        }

        holder.btnHistory.setOnClickListener {
            itemClickListener.onHistory(currentItem)
        }
    }
}