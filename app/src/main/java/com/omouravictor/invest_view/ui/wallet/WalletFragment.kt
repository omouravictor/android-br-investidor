package com.omouravictor.invest_view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.invest_view.databinding.FragmentWalletBinding
import com.omouravictor.invest_view.ui.base.ViewPagerAdapter
import com.omouravictor.invest_view.ui.wallet.assets.AssetsFragment
import com.omouravictor.invest_view.ui.wallet.coins.CoinsFragment
import com.omouravictor.invest_view.ui.wallet.details.DetailsFragment

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(requireActivity()).apply {
            addFragment(AssetsFragment(), "Ativos")
            addFragment(CoinsFragment(), "Moedas")
            addFragment(DetailsFragment(), "Detalhes")
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