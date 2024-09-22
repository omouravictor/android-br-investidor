package com.omouravictor.invest_view.presenter.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentWalletBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.asset_currencies.AssetCurrenciesFragment
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypesFragment
import com.omouravictor.invest_view.util.getGenericErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding
    private val walletViewModel: WalletViewModel by activityViewModels()

    companion object {
        private const val VIEW_FLIPPER_CHILD_FILLED_WALLET_LAYOUT = 0
        private const val VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT = 1
        private const val VIEW_FLIPPER_CHILD_PROGRESS_BAR = 2
        private const val VIEW_FLIPPER_CHILD_WALLET_ERROR_LAYOUT = 3

        private class WalletFragmentStateAdapter(
            fragmentManager: FragmentManager,
            lifecycle: Lifecycle,
            private val fragments: List<Pair<Fragment, String>>
        ) : FragmentStateAdapter(fragmentManager, lifecycle) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position].first
        }
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
        observeGetAssetListUiState()
    }

    private fun setupTabLayoutWithViewPager2() {
        val fragments = listOf(
            AssetTypesFragment() to getString(R.string.Assets),
            AssetCurrenciesFragment() to getString(R.string.currencies)
        )
        val viewPager2 = binding.viewPager2

        viewPager2.adapter = WalletFragmentStateAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            fragments = fragments
        )

        viewPager2.offscreenPageLimit = fragments.size

        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, position ->
            tab.text = fragments[position].second
        }.attach()
    }

    private fun setupButtons() {
        val navController = findNavController()
        binding.incEmptyWalletLayout.incBtnAddAssets.root.apply {
            text = getString(R.string.addAssets)
            setOnClickListener {
                navController.navigate(WalletFragmentDirections.navToAssetSearchFragment())
            }
        }

        binding.incWalletErrorLayout.incBtnTryAgain.root.apply {
            text = getString(R.string.tryAgain)
            setOnClickListener {
                walletViewModel.loadAssetList()
            }
        }
    }

    private fun observeGetAssetListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.getAssetListUiState.collectLatest {
                    when (it) {
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

                        else -> binding.viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_PROGRESS_BAR
                    }
                }
            }
        }
    }

}