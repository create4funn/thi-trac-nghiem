package com.example.thitracnghiem.Fragment


import com.example.thitracnghiem.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.GridView
import androidx.fragment.app.DialogFragment
import com.example.thitracnghiem.adapter.CheckAnswerAdapter
import com.example.thitracnghiem.model.QuestionItem

class SubmitDialogFragment(private val questions: List<QuestionItem>, private val userAnswers: List<Int?>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.check_answer_dialog, null)

        val gridView: GridView = dialogView.findViewById(R.id.gridview)
        val adapter = CheckAnswerAdapter(requireContext(), questions, userAnswers)
        gridView.adapter = adapter

        builder.setView(dialogView)
            .setPositiveButton("Đóng") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Nộp bài") { dialog, _ ->
                // Handle submission logic if needed
                dialog.dismiss()
                // For example, you can call a method in DoExamActivity to submit the exam

            }

        return builder.create()
    }
}
