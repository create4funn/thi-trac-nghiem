package com.example.thitracnghiem.Fragment

import android.os.Bundle
import android.provider.Settings.Global
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thitracnghiem.ApiService.RetrofitInstance
import com.example.thitracnghiem.adapter.MeaningAdapter
import com.example.thitracnghiem.databinding.FragmentDictionaryBinding
import com.example.thitracnghiem.model.WordResult

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding

    private lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            val word = binding.edtSearch.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.recyViewMeaning.layoutManager = LinearLayoutManager(requireContext())
        binding.recyViewMeaning.adapter = adapter
    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null) {
                    throw (Exception())
                }
                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUI(response: WordResult) {
        binding.tvWord.text = response.word
        binding.tvPhonetic.text = response.phonetic
        adapter.updateNewData(response.meanings)

    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.btnSearch.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnSearch.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}