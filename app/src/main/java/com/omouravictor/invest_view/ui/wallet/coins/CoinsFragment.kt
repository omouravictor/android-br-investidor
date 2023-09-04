package com.omouravictor.invest_view.ui.wallet.coins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.omouravictor.invest_view.databinding.FragmentCoinsBinding

class CoinsFragment : Fragment() {

    private var _binding: FragmentCoinsBinding? = null
    private val binding get() = _binding!!
    private val coinsViewModel: CoinsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinsViewModel.text.observe(viewLifecycleOwner) {
            binding.textCurrencies.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}