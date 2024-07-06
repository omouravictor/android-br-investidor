package com.omouravictor.invest_view.presenter.wallet.asset_details

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetDetailsBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.asset_search.AssetSearchViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAssetPrice
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalInvested
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedTotalPrice
import com.omouravictor.invest_view.presenter.wallet.model.getTotalPrice
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.ConstantUtil
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent
import com.omouravictor.invest_view.util.calculateAndSetupVariationLayout
import com.omouravictor.invest_view.util.clearPileAndNavigateToStart
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.setupLayoutVariationVisibility
import com.omouravictor.invest_view.util.setupToolbarCenterText
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AssetDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAssetDetailsBinding
    private lateinit var assetUiModel: AssetUiModel
    private val assetSearchViewModel: AssetSearchViewModel by activityViewModels()
    private val walletViewModel: WalletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAssetUiModel()
        setupToolbar()
        setupViews()
        setupButtons()
        observeAssetQuoteUiState()
        observeAssetUiState()
        assetSearchViewModel.loadAssetQuote(assetUiModel.symbol)
    }

    override fun onStop() {
        super.onStop()
        assetSearchViewModel.resetAssetQuoteUiState()
        walletViewModel.resetUiState()
    }

    private fun initAssetUiModel() {
        assetUiModel = findNavController().currentBackStackEntry?.savedStateHandle?.get<AssetUiModel>(
            ConstantUtil.SAVED_STATE_HANDLE_KEY_OF_UPDATED_ASSET_UI_MODEL
        ) ?: AssetDetailsFragmentArgs.fromBundle(requireArguments()).assetUiModel
    }

    private fun setupToolbar() {
        val activity = requireActivity()

        activity.setupToolbarCenterText(assetUiModel.getFormattedSymbol())

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_edit, menu)
                val menuItem = menu.findItem(R.id.editMenuItem)
                val spanStr = SpannableString(menuItem.title.toString())
                spanStr.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(activity, R.color.green)), 0, spanStr.length, 0
                )
                menuItem.title = spanStr
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                findNavController().navigate(AssetDetailsFragmentDirections.navToSaveAssetFragment(assetUiModel))
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun setupViews() {
        val context = requireContext()
        binding.tvAssetType.text = getString(assetUiModel.assetType.nameResId)
        binding.tvAssetType.backgroundTintList = getColorStateList(context, assetUiModel.assetType.colorResId)
        binding.tvAssetCurrency.text = assetUiModel.currency
        binding.tvAssetCurrency.backgroundTintList =
            getColorStateList(context, AssetUtil.getCurrencyResColor(assetUiModel.currency))
        binding.tvName.text = assetUiModel.name
        binding.tvPrice.text = assetUiModel.getFormattedAssetPrice()
        binding.tvAmount.text = assetUiModel.getFormattedAmount()
        binding.tvTotalInvested.text = assetUiModel.getFormattedTotalInvested()
        binding.tvTotalPrice.text = assetUiModel.getFormattedTotalPrice()

        if (assetUiModel.totalInvested > 0.0) {
            binding.incLayoutYield.calculateAndSetupVariationLayout(
                textSize = 14f,
                currency = assetUiModel.currency,
                reference = assetUiModel.getTotalPrice(),
                totalReference = assetUiModel.totalInvested
            )
        } else {
            binding.incLayoutYield.setupLayoutVariationVisibility(false, "-")
        }

        binding.ivReloadVariation.setOnClickListener { assetSearchViewModel.loadAssetQuote(assetUiModel.symbol) }
    }

    private fun setupButtons() {
        binding.incBtnDelete.root.apply {
            text = getString(R.string.delete)
            setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.deleteAsset))
                    setMessage(getString(R.string.deleteAssetAlertMessage))
                    setPositiveButton(getString(R.string.yes)) { _, _ -> walletViewModel.deleteAsset(assetUiModel) }
                    setNegativeButton(getString(R.string.not)) { dialog, _ -> dialog.dismiss() }
                    setIcon(R.drawable.ic_delete)
                }.show()
            }
        }

        binding.incBtnNewAddition.root.apply {
            text = getString(R.string.newAddition)
            setOnClickListener {
                findNavController().navigate(AssetDetailsFragmentDirections.navToNewAdditionFragment(assetUiModel))
            }
        }
    }

    private fun setupLoadingLayoutForAssetQuote(isLoading: Boolean) {
        if (isLoading) {
            binding.incShimmerItemVariation.root.startShimmer()
            binding.incShimmerItemVariation.root.visibility = View.VISIBLE
            binding.tvChange.visibility = View.INVISIBLE
            binding.ivReloadVariation.visibility = View.INVISIBLE
        } else {
            binding.incShimmerItemVariation.root.stopShimmer()
            binding.incShimmerItemVariation.root.visibility = View.INVISIBLE
            binding.tvChange.visibility = View.VISIBLE
            binding.ivReloadVariation.visibility = View.INVISIBLE
        }
    }

    private fun setupChangeTextView(change: Double, changePercent: Double?) {
        val formattedValue = getFormattedCurrencyValue(assetUiModel.currency, change)
        val formattedPercent = changePercent?.div(100)?.let { getFormattedValueForPercent(it) }

        binding.tvChange.apply {
            text = when {
                changePercent != null -> "$formattedValue (${if (change > 0) "+" else ""}$formattedPercent)"
                change > 0 -> "+$formattedValue"
                else -> formattedValue
            }
            setTextColor(
                ContextCompat.getColor(
                    context, when {
                        change > 0 -> R.color.green
                        change < 0 -> R.color.red
                        else -> R.color.gray
                    }
                )
            )
        }
    }

    private fun observeAssetQuoteUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                assetSearchViewModel.assetQuoteUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> setupLoadingLayoutForAssetQuote(true)
                        is UiState.Success -> {
                            setupLoadingLayoutForAssetQuote(false)
                            val change = it.data.change
                            val changePercent = it.data.changePercent.removeSuffix("%").toDoubleOrNull()
                            setupChangeTextView(change, changePercent)
                        }

                        is UiState.Error -> {
                            setupLoadingLayoutForAssetQuote(false)
                            binding.tvChange.visibility = View.INVISIBLE
                            binding.ivReloadVariation.visibility = View.VISIBLE
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeAssetUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.assetUiState.collectLatest {
                    when (it) {
                        is UiState.Loading -> {
                            binding.mainLayout.visibility = View.INVISIBLE
                            binding.incProgressBar.root.visibility = View.VISIBLE
                        }

                        is UiState.Success -> findNavController().clearPileAndNavigateToStart()

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.mainLayout.visibility = View.VISIBLE
                            binding.incProgressBar.root.visibility = View.GONE
                            activity.showErrorSnackBar(activity.getGenericErrorMessage(it.e))
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

}