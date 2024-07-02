package com.omouravictor.invest_view.presenter.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentWalletBinding
import com.omouravictor.invest_view.presenter.base.BaseViewPagerAdapter
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypesFragment
import com.omouravictor.invest_view.presenter.wallet.currencies.CurrenciesFragment
import com.omouravictor.invest_view.util.getGenericErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding
    private val walletViewModel: WalletViewModel by activityViewModels()

    companion object {
        const val VIEW_FLIPPER_CHILD_FILLED_WALLET_LAYOUT = 0
        const val VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT = 1
        const val VIEW_FLIPPER_CHILD_PROGRESS_BAR = 2
        const val VIEW_FLIPPER_CHILD_WALLET_ERROR_LAYOUT = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayoutWithViewPager2()
        setupButtons()
        observeAssetsListUiState()
    }

    private fun setupTabLayoutWithViewPager2() {
        val fragments = listOf(
            Pair(AssetTypesFragment(), getString(R.string.Assets)),
            Pair(CurrenciesFragment(), getString(R.string.currencies)),
        )
        val viewPager2 = binding.viewPager2

        viewPager2.adapter = BaseViewPagerAdapter(requireActivity(), fragments)

        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, position ->
            tab.text = fragments[position].second
        }.attach()
    }

    private fun setupButtons() {
        binding.incEmptyWalletLayout.incBtnAddAssets.root.apply {
            text = getString(R.string.addAssets)
            setOnClickListener {
                findNavController().navigate(WalletFragmentDirections.navToAssetSearchFragment())
            }
        }

        binding.incWalletErrorLayout.incBtnTryAgain.root.apply {
            text = getString(R.string.tryAgain)
            setOnClickListener {
                walletViewModel.loadAssets()
            }
        }
    }

    private fun observeAssetsListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.assetsListUiStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial, UiState.Loading -> binding.viewFlipper.displayedChild =
                            VIEW_FLIPPER_CHILD_PROGRESS_BAR

                        is UiState.Success -> {
                            if (it.data.isNotEmpty())
                                binding.viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_FILLED_WALLET_LAYOUT
                            else
                                binding.viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT
                        }

                        is UiState.Error -> {
                            binding.viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_WALLET_ERROR_LAYOUT
                            binding.incWalletErrorLayout.tvInfoMessage.text =
                                binding.root.context.getGenericErrorMessage(it.e)
                        }
                    }
                }
            }
        }
    }

}