package com.example.thitracnghiem.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thitracnghiem.ApiService.ClassService
import com.example.thitracnghiem.ApiService.RetrofitClient
import com.example.thitracnghiem.R
import com.example.thitracnghiem.adapter.AnnouncementAdapter
import com.example.thitracnghiem.model.AnnouncementItem
import com.example.thitracnghiem.model.ClassItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AnnounceTabFragment : Fragment() {

    private lateinit var btnPost : Button
    private lateinit var input : EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var classItem : ClassItem
//    private lateinit var announceList : List<AnnouncementItem>

    private val classService = RetrofitClient.retrofit.create(ClassService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_noti_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            classItem = it.getSerializable("classItem") as ClassItem
        }

        btnPost = view.findViewById(R.id.btnPost)
        input = view.findViewById(R.id.inputAnnounce)
        recyclerView = view.findViewById(R.id.recyclerViewAnnounce)
        recyclerView.layoutManager = LinearLayoutManager(context)

        getAnnouncement()

        btnPost.setOnClickListener {
            if(input.text.isNotEmpty()){
                val announcementItem = AnnouncementItem(classItem.classroom_id, input.text.toString(), null)
                postAnnouncement(announcementItem)
            }
        }
    }

    private fun postAnnouncement(announcementItem: AnnouncementItem){
        classService.createAnnouncement(announcementItem).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    getAnnouncement()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }

    private fun getAnnouncement(){
        classService.getAnnouncements(classItem.classroom_id!!).enqueue(object : Callback<List<AnnouncementItem>> {
            override fun onResponse(call: Call<List<AnnouncementItem>>, response: Response<List<AnnouncementItem>>) {
                if(response.isSuccessful){
                    val announceList = response.body()!!
                    recyclerView.adapter = AnnouncementAdapter(announceList)
                }
            }

            override fun onFailure(call: Call<List<AnnouncementItem>>, t: Throwable) {

            }


        })
    }

}