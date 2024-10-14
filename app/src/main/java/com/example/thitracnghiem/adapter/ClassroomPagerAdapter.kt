package com.example.thitracnghiem.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.thitracnghiem.Fragment.ExamTabFragment
import com.example.thitracnghiem.Fragment.AnnounceTabFragment
import com.example.thitracnghiem.model.ClassItem

class ClassroomPagerAdapter(fragmentActivity: FragmentActivity, private val classItem: ClassItem) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = ExamTabFragment()
                val bundle = Bundle()
                bundle.putSerializable("classItem", classItem)
                fragment.arguments = bundle
                fragment
            }
            1 -> {
                val fragment = AnnounceTabFragment()
                val bundle = Bundle()
                bundle.putSerializable("classItem", classItem)
                fragment.arguments = bundle
                fragment
            }
            else -> throw IllegalArgumentException("Vị trí không hợp lệ")
        }
    }
}

