package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.base.UiState
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.util.AppUtil
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.BindingUtil
import com.omouravictor.invest_view.util.EditTextUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.NavigationUtil
import com.omouravictor.invest_view.util.StringUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModel: AssetUiModel
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val saveViewModel: SaveViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupToolbar()
        setupViews()
        setupBtnSave()
        observeWalletUiState()
    }

    override fun onStop() {
        super.onStop()
        saveViewModel.resetUiStateFlow()
    }

    private fun setupToolbar() {
        val activity = requireActivity()
        val assetType = assetUiModel.assetType

        activity.findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            findViewById<TextView>(R.id.tvToolbarCenterText).apply { text = getString(assetType.nameResId) }
        }

        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_info, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                AppUtil.showInfoBottomSheetDialog(activity, assetType)
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun initEssentialVars() {
        val assetBySearchUiModel = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchUiModel
        assetUiModel = AssetUiModel(
            symbol = assetBySearchUiModel.symbol,
            name = assetBySearchUiModel.name,
            assetType = AssetUtil.getAssetType(assetBySearchUiModel.symbol, assetBySearchUiModel.type),
            region = assetBySearchUiModel.region,
            currency = assetBySearchUiModel.currency,
            price = assetBySearchUiModel.price
        )
    }

    private fun setupViews() {
        val context = requireContext()
        val assetType = assetUiModel.assetType
        val assetTypeColor = context.getColor(assetType.colorResId)
        val ietAmount = binding.ietAmount
        val ietTotalInvested = binding.ietTotalInvested
        val currency = assetUiModel.currency
        val etSymbol = binding.etSymbol
        val etLocation = binding.etLocation
        val incItemListAsset = binding.incItemListAsset

        etSymbol.setText(AssetUtil.getFormattedSymbol(assetUiModel.symbol))
        etLocation.setText(assetUiModel.region)
        ietTotalInvested.hint = LocaleUtil.getFormattedCurrencyValue(currency, 0.0)
        EditTextUtil.setEditTextsAfterTextChanged({ updateCurrentPosition() }, ietAmount, ietTotalInvested)
        EditTextUtil.setEditTextLongNumberFormatMask(ietAmount)
        EditTextUtil.setEditTextCurrencyFormatMask(ietTotalInvested, currency)
        incItemListAsset.color.setBackgroundColor(assetTypeColor)
        ietAmount.setText("1")
    }

    private fun requiredFieldsNotEmpty(): Boolean {
        val ietAmountText = binding.ietAmount.text.toString()
        return ietAmountText.isNotEmpty() && ietAmountText != "0"
    }

    private fun getAmount(): Long {
        val amount = binding.ietAmount.text.toString()
        return if (amount.isNotEmpty()) {
            StringUtil.getOnlyNumbers(amount).toLong()
        } else {
            0
        }
    }

    private fun getTotalInvested(): Double {
        val totalInvested = binding.ietTotalInvested.text.toString()
        return if (totalInvested.isNotEmpty()) {
            StringUtil.getOnlyNumbers(totalInvested).toDouble() / 100
        } else {
            0.0
        }
    }

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (requiredFieldsNotEmpty()) {
                val totalAssetPrice = assetUiModel.price * getAmount()
                val totalInvested = getTotalInvested()
                val currency = assetUiModel.currency

                tvSymbol.text = binding.etSymbol.text.toString()
                tvAmount.text = getString(R.string.placeholderAssetAmount, binding.ietAmount.text.toString())
                tvName.text = assetUiModel.name
                tvTotal.text = LocaleUtil.getFormattedCurrencyValue(currency, totalAssetPrice)
                tvInfoMessage.visibility = View.INVISIBLE
                layoutAssetInfo.visibility = View.VISIBLE
                binding.btnSave.isEnabled = true
                BindingUtil.setupVariationViews(this, currency, totalInvested, totalAssetPrice)

            } else {
                tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
                tvInfoMessage.visibility = View.VISIBLE
                layoutAssetInfo.visibility = View.INVISIBLE
                binding.btnSave.isEnabled = false
            }
        }
    }

    private fun observeWalletUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                saveViewModel.uiStateFlow.collectLatest {
                    when (it) {
                        is UiState.Initial -> Unit

                        is UiState.Loading -> {
                            binding.layout.visibility = View.INVISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is UiState.Error -> {
                            val activity = requireActivity()
                            binding.layout.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE
                            AppUtil.showErrorSnackBar(activity, AppUtil.getGenericErrorMessage(activity, it.e))
                        }

                        is UiState.Success -> {
                            walletViewModel.addAsset(it.data)
                            NavigationUtil.clearPileAndNavigateToStart(findNavController())
                        }
                    }
                }
            }
        }
    }

    private fun setupBtnSave() {
        binding.btnSave.setOnClickListener {
            assetUiModel.amount = getAmount()
            assetUiModel.totalInvested = getTotalInvested()
            saveViewModel.saveAsset(assetUiModel)
        }
    }

}