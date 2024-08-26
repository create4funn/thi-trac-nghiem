package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.Activity.ClassroomActivity
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.ClassAdapter
import com.example.thitracnghiem.model.ClassItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ClrStudentFragment : Fragment(), ClassAdapter.OnItemClickListener {

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
        return inflater.inflate(R.layout.fragment_class_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val edtSearch = view.findViewById<EditText>(R.id.edtClassSearch)
        val btnSearchh = view.findViewById<ImageButton>(R.id.btnSearch)

        recyclerViewClasses = view.findViewById(R.id.rcvStudentClass)

        recyclerViewClasses.layoutManager = LinearLayoutManager(context)
        classList = mutableListOf()
        classAdapter = ClassAdapter(classList, this)
        recyclerViewClasses.adapter = classAdapter


        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        db = FirebaseFirestore.getInstance()
        loadClasses()
        btnSearchh.setOnClickListener {
            val idSearch = edtSearch.text.toString()
            if (idSearch.isNotEmpty()) {
                searchAndAddClassroom(idSearch)
            }else{
                Toast.makeText(context, "Vui lòng nhập id", Toast.LENGTH_SHORT)
            }
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

    private fun searchAndAddClassroom(classroomId: String) {

        db.collection("classrooms").document(classroomId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    db.collection("users").document(userId)
                        .update("class", FieldValue.arrayUnion(classroomId))
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Tham gia lớp thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadClasses()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to add classroom: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }else{
                    Toast.makeText(context, "Lớp không tồn tại", Toast.LENGTH_SHORT).show()
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