package com.omouravictor.invest_view.presenter.wallet.save_asset

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentSaveAssetBinding
import com.omouravictor.invest_view.presenter.wallet.assets.AssetsViewModel
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getAssetType
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.EditTextUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.NavigationUtil
import com.omouravictor.invest_view.util.StringUtil

class SaveAssetFragment : Fragment() {

    private lateinit var binding: FragmentSaveAssetBinding
    private lateinit var assetUiModel: AssetUiModel
    private val saveAssetViewModel: SaveAssetViewModel by viewModels()
    private val assetsViewModel: AssetsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupViews()
        setupBtnSave()
    }

    private fun initEssentialVars() {
        val assetBySearchUiModel = SaveAssetFragmentArgs.fromBundle(requireArguments()).assetBySearchUiModel
        assetUiModel = AssetUiModel(
            symbol = assetBySearchUiModel.symbol,
            name = assetBySearchUiModel.name,
            assetType = assetBySearchUiModel.getAssetType(),
            region = assetBySearchUiModel.region,
            currency = assetBySearchUiModel.currency,
            price = assetBySearchUiModel.price
        )
    }

    private fun setupViews() {
        val assetType = assetUiModel.assetType
        val assetTypeColor = assetType.getColor(requireContext())
        val ietAmount = binding.ietAmount
        val ietTotalInvested = binding.ietTotalInvested
        val textInputLayoutAmount = binding.textInputLayoutAmount
        val textInputLayoutTotalInvested = binding.textInputLayoutTotalInvested
        val currency = assetUiModel.currency
        val etSymbol = binding.etSymbol
        val etLocation = binding.etLocation
        val incItemListAsset = binding.incItemListAsset

        (requireActivity() as AppCompatActivity).supportActionBar?.title = assetType.getName(requireContext())
        etSymbol.setText(AssetUtil.getFormattedSymbol(assetUiModel.symbol))
        etLocation.setText(assetUiModel.region)
        ietTotalInvested.hint = LocaleUtil.getFormattedValueForCurrency(currency, 0.0)
        EditTextUtil.setEditTextsAfterTextChanged({ updateCurrentPosition() }, ietAmount, ietTotalInvested)
        EditTextUtil.setEditTextsHighLightColor(assetTypeColor.defaultColor, ietAmount, ietTotalInvested)
        EditTextUtil.setEditTextLongNumberFormatMask(ietAmount)
        EditTextUtil.setEditTextCurrencyFormatMask(ietTotalInvested, currency)
        setEditTextsFocusChange(assetTypeColor, ietAmount, ietTotalInvested)
        textInputLayoutAmount.setBoxStrokeColorStateList(assetTypeColor)
        textInputLayoutTotalInvested.setBoxStrokeColorStateList(assetTypeColor)
        incItemListAsset.color.backgroundTintList = assetTypeColor
        ietAmount.setText("1")
    }

    private fun setEditTextsFocusChange(assetTypeColor: ColorStateList, vararg editTexts: EditText) {
        val grayColor = requireContext().getColorStateList(R.color.gray)
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { v, hasFocus ->
                v.backgroundTintList = if (hasFocus) assetTypeColor else grayColor
            }
        }
    }

    private fun requiredFieldsNotEmpty(): Boolean {
        val ietAmountText = binding.ietAmount.text.toString()
        return ietAmountText.isNotEmpty() && ietAmountText != "0"
    }

    private fun updateCurrentPosition() {
        binding.incItemListAsset.apply {
            if (requiredFieldsNotEmpty()) {
                val ietAmountText = binding.ietAmount.text.toString()
                val totalAssetPrice = saveAssetViewModel.getTotalAssetPrice(assetUiModel.price, ietAmountText)
                val ietTotalInvestedText = binding.ietTotalInvested.text.toString()
                val totalInvested = saveAssetViewModel.getTotalInvested(ietTotalInvestedText)

                tvSymbol.text = binding.etSymbol.text.toString()
                tvAmount.text = getString(R.string.placeholderAmount, ietAmountText)
                tvName.text = assetUiModel.name
                tvTotal.text = LocaleUtil.getFormattedValueForCurrency(assetUiModel.currency, totalAssetPrice)
                AssetUtil.setupVariationViews(
                    this,
                    assetUiModel.currency,
                    totalInvested,
                    totalAssetPrice
                )
                tvInfoMessage.visibility = View.INVISIBLE
                layoutAssetInfo.visibility = View.VISIBLE
                binding.btnSave.isEnabled = true

            } else {
                tvInfoMessage.hint = getString(R.string.fillTheFieldsToView)
                tvInfoMessage.visibility = View.VISIBLE
                layoutAssetInfo.visibility = View.INVISIBLE
                binding.btnSave.isEnabled = false
            }
        }
    }

    private fun setupBtnSave() {
        binding.btnSave.setOnClickListener {
            assetUiModel.amount = StringUtil.getOnlyNumbers(binding.ietAmount.text.toString()).toLong()
            assetUiModel.totalInvested = saveAssetViewModel.getTotalInvested(binding.ietTotalInvested.text.toString())
            assetsViewModel.addAsset(assetUiModel)
            NavigationUtil.clearPileAndNavigateToStart(findNavController())
        }
    }

}