package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ExamItem

class ExamAdapter2(private var list:List<ExamItem>, private val itemClickListener: OnItemClickListener): RecyclerView.Adapter<ExamAdapter2.ExamViewHolder>() {

    interface OnItemClickListener {
        fun onUpdateClick(item: ExamItem)
        fun onDeleteClick(item: ExamItem)
        fun onStatusClick(item: ExamItem)
    }
    fun updateList(newList: List<ExamItem>) {
        list = newList
        notifyDataSetChanged() // Thông báo cho adapter cập nhật
    }
    class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.tv_examClass)
        val num : TextView = itemView.findViewById(R.id.tv_numOfQuesClass)
        val time : TextView = itemView.findViewById(R.id.tv_durationClass)
        val btnUpdate: TextView = itemView.findViewById(R.id.tv_update)
        val btnDelete: TextView = itemView.findViewById(R.id.tv_deleteClass)
        val btnStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tv_hide: TextView = itemView.findViewById(R.id.tv_hide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_exam_class, parent, false)
        return ExamViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val currentItem = list[position]
        holder.name.text = currentItem.exam_name
        holder.num.text = "${currentItem.numOfQues} câu"
        holder.time.text = "${currentItem.duration} phút"

        holder.btnUpdate.setOnClickListener {
            itemClickListener.onUpdateClick(currentItem)
        }

        holder.btnDelete.setOnClickListener {
            itemClickListener.onDeleteClick(currentItem)
        }

        holder.btnStatus.setOnClickListener {
            itemClickListener.onStatusClick(currentItem)
        }

        // Hiển thị hoặc ẩn textView dựa trên status của item
        if (currentItem.status == 0){
            holder.tv_hide.visibility =  View.VISIBLE
            holder.btnStatus.text = "Bỏ ẩn"
        }
    }
}