package com.omouravictor.invest_view.presenter.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentWalletBinding
import com.omouravictor.invest_view.presenter.base.BaseViewPagerAdapter
import com.omouravictor.invest_view.presenter.wallet.assets.AssetsFragment
import com.omouravictor.invest_view.presenter.wallet.details.DetailsFragment

class WalletFragment : Fragment() {

    private lateinit var binding: FragmentWalletBinding
    private val walletViewModel: WalletViewModel by activityViewModels()

    companion object {
        const val VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT = 0
        const val VIEW_FLIPPER_CHILD_WALLET_LAYOUT = 1
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
        setupEmptyWalletLayout()
        observeAssets()
    }

    private fun setupTabLayoutWithViewPager2() {
        val fragments = listOf(
            Pair(AssetsFragment(), getString(R.string.Assets)),
            Pair(DetailsFragment(), getString(R.string.details))
        )

        binding.viewPager2.adapter = BaseViewPagerAdapter(requireActivity(), fragments)

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = fragments[position].second
        }.attach()
    }

    private fun setupEmptyWalletLayout() {
        binding.incEmptyWalletLayout.btnAddAsset.setOnClickListener {
            findNavController().navigate(WalletFragmentDirections.navToAssetSearchFragment())
        }
    }

    private fun observeAssets() {
        walletViewModel.assetsLiveData.observe(viewLifecycleOwner) {
            binding.viewFlipper.displayedChild = if (it.isEmpty()) {
                VIEW_FLIPPER_CHILD_EMPTY_WALLET_LAYOUT
            } else {
                VIEW_FLIPPER_CHILD_WALLET_LAYOUT
            }
        }
    }

}