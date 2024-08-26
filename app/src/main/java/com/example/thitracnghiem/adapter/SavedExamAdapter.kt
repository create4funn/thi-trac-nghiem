package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ExamItem

class SavedExamAdapter(private val list:MutableList<ExamItem>, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<SavedExamAdapter.ExamViewHolder>() {

    interface OnItemClickListener {
        fun onDeleteClick(item: ExamItem)
        fun onDoExamClick(item: ExamItem)
    }

    class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.tv_examTitle_saved)
        val num : TextView = itemView.findViewById(R.id.tv_numOfQues_saved)
        val time : TextView = itemView.findViewById(R.id.tv_duration_saved)
        val btnDelete: TextView = itemView.findViewById(R.id.tv_delete)
        val btnDoExam: TextView = itemView.findViewById(R.id.tv_do)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_exam, parent, false)
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

        holder.btnDelete.setOnClickListener {
            itemClickListener.onDeleteClick(currentItem)
        }

        holder.btnDoExam.setOnClickListener {
            itemClickListener.onDoExamClick(currentItem)
        }
    }

    fun removeItem(item: ExamItem) {
        val position = list.indexOf(item)
        if (position != -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}