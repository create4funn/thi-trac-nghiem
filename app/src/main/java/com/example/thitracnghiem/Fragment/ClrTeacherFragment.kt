package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.Activity.ClassroomActivity
import com.example.thitracnghiem.Activity.CreateClassActivity
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ClassAdapter
import com.example.thitracnghiem.model.ClassItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ClrTeacherFragment : Fragment(), ClassAdapter.OnItemClickListener {

    private lateinit var recyclerViewClasses: RecyclerView
    private lateinit var classAdapter: ClassAdapter
    private lateinit var classList: MutableList<ClassItem>
    private lateinit var userId: String
    private lateinit var db : FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clr_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreateClass = view.findViewById<Button>(R.id.btnCreateClass1)
        recyclerViewClasses = view.findViewById(R.id.recyclerView)

        recyclerViewClasses.layoutManager = LinearLayoutManager(context)
        classList = mutableListOf()
        classAdapter = ClassAdapter(classList, this)
        recyclerViewClasses.adapter = classAdapter


        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        db = FirebaseFirestore.getInstance()
        loadClasses()

        btnCreateClass.setOnClickListener {
            val intent = Intent(context, CreateClassActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadClasses() {

        db.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
            val classIds = documentSnapshot.get("class") as? List<String> ?: listOf()

            for (classId in classIds) {
                db.collection("classrooms").document(classId).get().addOnSuccessListener { classDocument ->
                    val classroom = classDocument.toObject<ClassItem>()
                    classroom?.let {
                        it.setId(classDocument.id)
                        classList.add(it)
                        classAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onItemClick(item: ClassItem) {
        val intel = Intent(context, ClassroomActivity::class.java)
        intel.putExtra("classname", "${item.tenLop}")
        intel.putExtra("classId", "${item.id}")
        startActivity(intel)

    }
}
