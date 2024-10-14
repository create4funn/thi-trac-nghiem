package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.HistoryItem

class HistoryAdapter(private val historyList: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.username.text = currentItem.username
        holder.txtNumTrue.text = currentItem.proportion
        holder.txtScore.text = currentItem.score.toString()
        holder.txtTime.text = currentItem.time
    }

    override fun getItemCount() = historyList.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txtUsername)
        val txtNumTrue: TextView = itemView.findViewById(R.id.txtNumTrue)
        val txtScore: TextView = itemView.findViewById(R.id.txtScore)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }
}
