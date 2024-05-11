package com.omouravictor.invest_view.presenter.wallet.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.omouravictor.invest_view.databinding.FragmentDetailsBinding
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.LocaleUtil

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvWalletTotalInvested.text = LocaleUtil.getFormattedCurrencyValue(walletViewModel.totalInvested)
        binding.tvWalletTotalPrice.text = LocaleUtil.getFormattedCurrencyValue(walletViewModel.totalPrice)
        binding.tvWalletTotalVariation.text = LocaleUtil.getFormattedCurrencyValue(walletViewModel.totalVariation)
    }

}