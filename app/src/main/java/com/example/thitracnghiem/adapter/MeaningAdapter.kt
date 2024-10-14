package com.example.thitracnghiem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.R
import com.example.thitracnghiem.model.Meaning


class MeaningAdapter(private var meaningList: List<Meaning>) : RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder>() {

    class MeaningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPartOfSpeech: TextView = itemView.findViewById(R.id.tvPartOfSpeech)
        val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition)
        val tvSynonymsTitle: TextView = itemView.findViewById(R.id.tvSynonymsTitle)
        val tvSynonyms: TextView = itemView.findViewById(R.id.tvSynonyms)
        val tvAntonymsTitle: TextView = itemView.findViewById(R.id.tvAntonymsTitle)
        val tvAntonyms: TextView = itemView.findViewById(R.id.tvAntonyms)

        fun bind(meaning: Meaning) {

            tvPartOfSpeech.text = meaning.partOfSpeech

            tvDefinition.text = meaning.definitions.joinToString("\n\n") {
                var currentIndex = meaning.definitions.indexOf(it)
                (currentIndex + 1).toString() + ". " + it.definition.toString()
            }

            if (meaning.synonyms.isEmpty()) {
                tvSynonymsTitle.visibility = View.GONE
                tvSynonyms.visibility = View.GONE
            } else {
                tvSynonymsTitle.visibility = View.VISIBLE
                tvSynonyms.visibility = View.VISIBLE
                tvSynonyms.text = meaning.synonyms.joinToString(", ")
            }

            if (meaning.antonyms.isEmpty()) {
                tvAntonymsTitle.visibility = View.GONE
                tvAntonyms.visibility = View.GONE
            } else {
                tvAntonymsTitle.visibility = View.VISIBLE
                tvAntonyms.visibility = View.VISIBLE
                tvAntonyms.text = meaning.antonyms.joinToString(", ")
            }
        }
    }

    fun updateNewData(newMeaningList: List<Meaning>) {
        meaningList = newMeaningList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_meaning, parent, false)
        return MeaningViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return meaningList.size
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        holder.bind(meaningList[position])
    }
}