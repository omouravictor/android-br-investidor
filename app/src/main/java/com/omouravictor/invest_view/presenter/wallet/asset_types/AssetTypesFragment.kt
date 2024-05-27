package com.omouravictor.invest_view.presenter.wallet.asset_types

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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.AppUtil

class AssetTypesFragment : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentAssetsBinding
    private lateinit var pieChart: PieChart
    private val walletViewModel: WalletViewModel by activityViewModels()
    private val assetTypesAdapter = AssetTypesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEssentialVars()
        setupChart()
        setupAdapter()
        setupRecyclerView()
        observeAssets()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val assetType = (e as PieEntry).label
        val filteredAssets = walletViewModel.assetsList.filter { getString(it.assetType.nameResId) == assetType }

        updatePieChartCenterText(filteredAssets.size)
        assetTypesAdapter.updateItemsList(filteredAssets)
    }

    override fun onNothingSelected() {
        val assetsList = walletViewModel.assetsList

        updatePieChartCenterText(assetsList.size)
        assetTypesAdapter.updateItemsList(assetsList)
    }

    private fun initEssentialVars() {
        pieChart = binding.pieChart.also { it.setOnChartValueSelectedListener(this) }
    }

    private fun updatePieChartCenterText(assetSize: Int) {
        val assetText = getString(if (assetSize == 1) R.string.asset else R.string.assets)
        val spannableString = SpannableString("$assetSize\n$assetText").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, assetSize.toString().length, 0)
        }

        pieChart.centerText = spannableString
    }

    private fun setupChart() {
        val context = requireContext()
        val assets = walletViewModel.assetsList
        val assetTypes = walletViewModel.assetTypesList
        val colors = arrayListOf<Int>()
        val pieEntries = assetTypes.map { assetType ->
            colors.add(assetType.colorResId)
            val count = assets.count { it.assetType == assetType }
            PieEntry(count.toFloat(), getString(assetType.nameResId))
        }
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.assetTypes)).apply {
            this.colors = colors.map { ContextCompat.getColor(context, it) }
            sliceSpace = 3f
            setDrawIcons(false)
        }

        AppUtil.showPieChart(context, pieChart, pieDataSet)
        updatePieChartCenterText(assets.size)
    }

    private fun setupAdapter() {
        assetTypesAdapter.updateOnClickItem {
            Snackbar.make(binding.root, "Item clicked: $it", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = assetTypesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeAssets() {
        walletViewModel.assetsLiveData.observe(viewLifecycleOwner) {
            assetTypesAdapter.updateItemsList(it)
        }
    }

}