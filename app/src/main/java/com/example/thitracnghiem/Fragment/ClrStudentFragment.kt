package com.example.thitracnghiem.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.Activity.ClassroomActivity
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AllClassAdapter
import com.example.thitracnghiem.model.ClassItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClrStudentFragment : Fragment(), AllClassAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var frame1: FrameLayout
    private lateinit var frame2: FrameLayout
    private lateinit var classService: ClassService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classService = RetrofitClient.instance(requireContext()).create(ClassService::class.java)
        val joinedClassFragment = ClassJoinedFragment()
        var allClassFragment = AllClassFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_joined_class, joinedClassFragment)
            .replace(R.id.frame_layout_all_class, allClassFragment)
            .commit()


        val edtSearch = view.findViewById<EditText>(R.id.edtClassSearch)
        val btnSearch = view.findViewById<ImageButton>(R.id.btnSearch)
        val btnfilter = view.findViewById<ImageButton>(R.id.btnFilter)
        frame1 = view.findViewById(R.id.frame_layout_joined_class)
        frame2 = view.findViewById(R.id.frame_layout_all_class)
        recyclerView = view.findViewById(R.id.recyclerViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(context)

        btnSearch.setOnClickListener {

//            val searchClassFragment = AllClassFragment()
//            val bundle = Bundle()
//            bundle.putString("keyword", keyword)
//            searchClassFragment.arguments = bundle
//
//            childFragmentManager.beginTransaction()
//                .remove(joinedClassFragment)
//                .remove(allClassFragment)
//                .replace(R.id.frame_layout_joined_class, searchClassFragment)
//                .commit()

            frame1.visibility = View.GONE
            frame2.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE


            searchClass(edtSearch.text.trim().toString())
        }

        btnfilter.setOnClickListener {
            showBottomDialog()
        }
    }

    private fun searchClass(keyword: String){
        classService.searchClass(keyword).enqueue(object : Callback<List<ClassItem>>{
            override fun onResponse(call: Call<List<ClassItem>>, response: Response<List<ClassItem>>) {
                if(response.isSuccessful){
                    val classList = response.body()!!
                    if (classList.isEmpty()) {
//                        textView.text = "Không có kết quả trùng khớp"
                    } else {
                        //textView.text = "Tất cả lớp học trùng khớp"
                        recyclerView.adapter = AllClassAdapter(classList, this@ClrStudentFragment)
                    }

                }
            }

            override fun onFailure(call: Call<List<ClassItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun filterClass(subject: String?, grade: String?){
        classService.filterClass(subject, grade).enqueue(object : Callback<List<ClassItem>>{
            override fun onResponse(call: Call<List<ClassItem>>, response: Response<List<ClassItem>>) {
                if(response.isSuccessful){
                    val classList = response.body()!!
                    if (classList.isEmpty()) {
//                        textView.text = "Không có kết quả trùng khớp"
                    } else {
                        //textView.text = "Tất cả lớp học trùng khớp"
                        recyclerView.adapter = AllClassAdapter(classList, this@ClrStudentFragment)
                    }

                }
            }

            override fun onFailure(call: Call<List<ClassItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showBottomDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)

        // Tạo Bottom Sheet Dialog
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        // Ánh xạ các view
        val filterGrade: AutoCompleteTextView = bottomSheetView.findViewById(R.id.filterGrade)
        val filterSubject: AutoCompleteTextView = bottomSheetView.findViewById(R.id.filterSubject)
        val btnApply : Button = bottomSheetView.findViewById(R.id.btnApplyFilter)

        val monHoc = resources.getStringArray(R.array.list_mon_hoc)
        val lop = resources.getStringArray(R.array.list_lop)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, monHoc)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_dropdown, lop)

        filterGrade.setAdapter(arrayAdapter2)
        filterSubject.setAdapter(arrayAdapter)

        btnApply.setOnClickListener {
            if(filterSubject.text.isNotEmpty() || filterGrade.text.isNotEmpty()){
                frame1.visibility = View.GONE
                frame2.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                filterClass(filterSubject.text.toString(), filterGrade.text.toString())
                bottomSheetDialog.cancel()
            }else{
                Toast.makeText(context, "Vui lòng chọn tiêu chí để lọc", Toast.LENGTH_SHORT).show()
            }
        }
        // Hiển thị Bottom Sheet Dialog
        bottomSheetDialog.show()
    }

    override fun onItemClick(item: ClassItem) {

    }


}