package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.Activity.DoExamActivity
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.SavedExamAdapter
import com.example.thitracnghiem.helper.ExamDatabaseHelper
import com.example.thitracnghiem.model.ExamItem

class SavedExamFragment : Fragment(), SavedExamAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedExamAdapter
    private lateinit var dbHelper: ExamDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcvSavedExam)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = ExamDatabaseHelper(requireContext())
        val savedExams = dbHelper.getExams()

        adapter = SavedExamAdapter(savedExams, this)
        recyclerView.adapter = adapter
    }

    override fun onDeleteClick(item: ExamItem) {
        dbHelper.deleteExamById(item.id)
        Toast.makeText(requireContext(), "Deleted: ${item.name}", Toast.LENGTH_SHORT).show()
        adapter.removeItem(item)
    }

    override fun onDoExamClick(item: ExamItem) {
        val intent = Intent(context, DoExamActivity::class.java)
        intent.putStringArrayListExtra("questions", ArrayList(item.questions))
        intent.putExtra("time", item.duration)
        intent.putExtra("check", 1)
        startActivity(intent)
    }
}
