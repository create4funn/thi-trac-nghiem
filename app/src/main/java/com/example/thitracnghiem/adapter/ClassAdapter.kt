package com.example.thitracnghiem.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.ClassItem
import com.squareup.picasso.Picasso

class ClassAdapter(private val classList: List<ClassItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: ClassItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_classroom, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentItem = classList[position]
        holder.tenLop.text = currentItem.tenLop
        holder.classSubject.text = currentItem.monHoc
        holder.classGrade.text = currentItem.lop
        Picasso.get().load(currentItem.imageUrl).into(holder.imgClass)

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount() = classList.size

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgClass: ImageView = itemView.findViewById(R.id.imgClass)
        val tenLop: TextView = itemView.findViewById(R.id.tenLop)
        val classSubject: TextView = itemView.findViewById(R.id.class_subject)
        val classGrade: TextView = itemView.findViewById(R.id.grade)
    }
}
