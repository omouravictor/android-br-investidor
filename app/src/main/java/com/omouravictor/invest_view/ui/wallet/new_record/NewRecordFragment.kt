package com.omouravictor.invest_view.ui.wallet.new_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.invest_view.databinding.FragmentNewRecordBinding
import com.omouravictor.invest_view.ui.base.ViewPagerAdapter

class NewRecordFragment : Fragment() {

    private var _binding: FragmentNewRecordBinding? = null
    private val binding get() = _binding!!

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

        val adapter = ViewPagerAdapter(requireActivity()).apply {
            addFragment(NewAssetFragment(), "Ativo")
            addFragment(NewCoinFragment(), "Moeda")
        }
        val tittles = adapter.getTittles()

        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = tittles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}