package com.omouravictor.invest_view.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.omouravictor.invest_view.databinding.FragmentNewRecordBinding

class NewRecordFragment : Fragment() {

    private var _binding: FragmentNewRecordBinding? = null
    private val binding get() = _binding!!
    private val newRecordViewModel: NewRecordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newRecordViewModel.text.observe(viewLifecycleOwner) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}