package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.SubjectItem
import com.squareup.picasso.Picasso

class SubjectAdapter(private val subjects: List<SubjectItem>, private val onClickListener: OnSubjectClickListener) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    interface OnSubjectClickListener{
        fun onSubjectClick(item: SubjectItem)
    }
    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName: TextView = itemView.findViewById(R.id.subjectName)
        val subjectImage: ImageView = itemView.findViewById(R.id.subjectImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentItem = subjects[position]
        holder.subjectName.text = currentItem.subject_name

        Picasso.get().load(currentItem.subject_img).into(holder.subjectImage)

        holder.itemView.setOnClickListener {
            onClickListener.onSubjectClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return subjects.size
    }
}
