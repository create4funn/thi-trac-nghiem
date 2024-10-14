package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.AnnouncementItem
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementAdapter(private val announcementList: List<AnnouncementItem>) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val time: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val currentItem = announcementList[position]

        holder.text.text = currentItem.text
        holder.time.text = formatTimestamp(currentItem.create_at)
    }

    override fun getItemCount(): Int {
        return announcementList.size
    }

    // Helper function to format timestamp based on the conditions
    private fun formatTimestamp(timestamp: java.sql.Timestamp?): String {
        val currentTime = Calendar.getInstance()
        val createAtTime = Calendar.getInstance().apply { timeInMillis = timestamp!!.time }

        // Check if create_at is today
        return if (isSameDay(currentTime, createAtTime)) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(createAtTime.time)
        } else {
            SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(createAtTime.time)
        }
    }

    // Helper function to check if two dates are on the same day
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}