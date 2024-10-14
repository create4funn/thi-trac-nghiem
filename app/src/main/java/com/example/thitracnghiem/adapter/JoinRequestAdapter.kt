package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.UserItem

class JoinRequestAdapter(private val list: List<UserItem>, private val itemClickListener: OnItemClickListener, val check: Boolean) : RecyclerView.Adapter<JoinRequestAdapter.JoinRequestViewHolder>() {

    interface OnItemClickListener {
        fun onAcceptClick(item: UserItem)
        fun onRejectClick(item: UserItem)
        fun onKickClick(item: UserItem)
    }

    class JoinRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUser: ImageView = itemView.findViewById(R.id.img_user)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
        val btnKick: ImageView = itemView.findViewById(R.id.btn_KickStudent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinRequestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_join_request, parent, false)
        return JoinRequestViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: JoinRequestViewHolder, position: Int) {
        val currentItem = list[position]
        holder.tvUsername.text = currentItem.username
        holder.tvPhone.text = currentItem.phone

        if (check){
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
            holder.btnKick.visibility = View.VISIBLE
        }
        holder.btnAccept.setOnClickListener {
            itemClickListener.onAcceptClick(currentItem)
        }

        holder.btnReject.setOnClickListener {
            itemClickListener.onRejectClick(currentItem)
        }

        holder.btnKick.setOnClickListener {
            itemClickListener.onKickClick(currentItem)
        }
    }
}
