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
        holder.txtPlayerName.text = currentItem.examName
        holder.txtNumTrue.text = currentItem.correctAnswers.toString()
        holder.txtScore.text = currentItem.score.toString()
        holder.txtTime.text = currentItem.timeTaken
    }

    override fun getItemCount() = historyList.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPlayerName: TextView = itemView.findViewById(R.id.txtPlayerName)
        val txtNumTrue: TextView = itemView.findViewById(R.id.txtNumTrue)
        val txtScore: TextView = itemView.findViewById(R.id.txtScore)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }
}
