package com.omouravictor.br_investidor.presenter.wallet

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.databinding.FragmentWalletBinding
import com.omouravictor.br_investidor.presenter.model.UiState
import com.omouravictor.br_investidor.presenter.user.UserViewModel
import com.omouravictor.br_investidor.presenter.wallet.asset.getFormattedSymbol
import com.omouravictor.br_investidor.presenter.wallet.asset_currencies.AssetCurrenciesFragment
import com.omouravictor.br_investidor.presenter.wallet.asset_types.AssetTypesFragment
import com.omouravictor.br_investidor.util.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WalletFragment : Fragment(R.layout.fragment_wallet) {

    private lateinit var binding: FragmentWalletBinding
    private lateinit var activity: FragmentActivity
    private lateinit var navController: NavController
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWalletBinding.bind(view)
        setupToolbar()
        setupTabLayoutWithViewPager2()
        setupButtons()
        observeGetUserAssetListUiState()
    }

    private fun setupToolbar() {
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_wallet, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.addAssetMenuItem ->
                        navController.navigate(WalletFragmentDirections.navToAssetSearchFragment())

                    R.id.deleteAllAssetsMenuItem -> showAlertDialogForDelete()
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun showAlertDialogForDelete() {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.deleteAllAssets))
            setMessage(getString(R.string.deleteAllAssetAlertMessage))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                walletViewModel.deleteAllUserAssets(userViewModel.user.value.uid)
            }
            setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
            setIcon(R.drawable.ic_delete)
        }.show()
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
                walletViewModel.getUserAssetList(userViewModel.user.value.uid)
            }
        }
    }

    private fun observeGetUserAssetListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.getUserAssetListUiState.collectLatest {
                    when (it) {
                        is UiState.Success -> {
                            if (it.data.isNotEmpty())
                                binding.viewFlipper.displayedChild =
                                    VIEW_FLIPPER_CHILD_FILLED_WALLET_LAYOUT
                            else
                                binding.viewFlipper.displayedChild =
                                    VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT
                        }

                        is UiState.Error -> {
                            binding.apply {
                                viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_WALLET_ERROR_LAYOUT
                                incWalletErrorLayout.tvInfoMessage.text =
                                    root.context.getErrorMessage(it.e)
                            }
                        }

                        else -> binding.viewFlipper.displayedChild = VIEW_FLIPPER_CHILD_PROGRESS_BAR
                    }
                }
            }
        }
    }

}