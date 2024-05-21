package com.omouravictor.invest_view.presenter.wallet.countries

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding
import com.omouravictor.invest_view.presenter.base.AssetsAdapter
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel

class CountriesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAssetsBinding
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val assetsAdapter = AssetsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        setupAdapter()
        setupRecyclerView()
        observeAssets()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val region = (e as PieEntry).label
        val filteredAssets = walletViewModel.assetsList.filter { it.region.substringBefore("/") == region }
        assetsAdapter.updateItemsList(filteredAssets)
    }

    override fun onNothingSelected() {
        assetsAdapter.updateItemsList(walletViewModel.assetsList)
    }

    private fun setupChart() {
        val context = requireContext()
        val assets = walletViewModel.assetsList
        val assetCountries = walletViewModel.assetRegionsList
        val assetTypes = walletViewModel.assetTypesList
        val textSize12 = 12f
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val appWindowBackColor = ContextCompat.getColor(context, R.color.appWindowBackColor)
        val appTextColor = ContextCompat.getColor(context, R.color.appTextColor)
        val boldTypeface = Typeface.DEFAULT_BOLD
        val pieEntries = assetCountries.map { region ->
            val count = assets.count { it.region == region }
            PieEntry(count.toFloat(), region.substringBefore("/"))
        }
        val colors = assetTypes.map { ContextCompat.getColor(context, it.colorResId) }
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.currencies)).apply {
            this.colors = colors
            sliceSpace = 3f
            setDrawIcons(false)
        }
        val pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }
        val pieData = PieData(pieDataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(textSize12)
            setValueTextColor(whiteColor)
        }
        val assetSize = assets.size
        val assetText = getString(if (assetSize == 1) R.string.asset else R.string.assets)
        val spannableString = SpannableString("$assetSize\n$assetText").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, assetSize.toString().length, 0)
        }

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = false
            isDrawHoleEnabled = true
            centerText = spannableString
            setUsePercentValues(true)
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(100)
            setTransparentCircleColor(appWindowBackColor)
            setEntryLabelTextSize(textSize12)
            setEntryLabelTypeface(boldTypeface)
            setCenterTextColor(appTextColor)
            setCenterTextSize(16f)
            animateY(1400, Easing.EaseInOutQuad)
        }
    }

    private fun setupAdapter() {
        assetsAdapter.updateOnClickItem {
            Snackbar.make(binding.root, "Item clicked: $it", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssets() {
        walletViewModel.assetsLiveData.observe(viewLifecycleOwner) {
            assetsAdapter.updateItemsList(it)
        }
    }

}