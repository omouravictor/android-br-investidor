package com.omouravictor.invest_view.presenter.wallet.assets

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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentAssetsBinding

class AssetsFragment : Fragment() {

    private lateinit var binding: FragmentAssetsBinding
    private val assetsViewModel: AssetsViewModel by activityViewModels()
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

    private fun setupChart() {
        val context = requireContext()
        val assets = assetsViewModel.currentAssets
        val assetTypes = assetsViewModel.currentAssetTypes
        val textSize12 = 12f
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val appWindowBackColor = ContextCompat.getColor(context, R.color.appWindowBackColor)
        val greenColor = ContextCompat.getColor(context, R.color.green)
        val boldTypeface = Typeface.DEFAULT_BOLD

        val pieEntries = assetTypes.map { assetType ->
            val count = assets.count { it.assetType.name == assetType.name }
            PieEntry(count.toFloat(), assetType.getName(context))
        }

        val colors = assetTypes.map { it.getColor(context) }

        val pieDataSet = PieDataSet(pieEntries, "Tipos de Ativos").apply {
            this.colors = colors
            sliceSpace = 3f
            setDrawIcons(false)
        }

        val pieChart = binding.pieChart
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
            setCenterTextColor(greenColor)
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
        assetsViewModel.assetsList.observe(viewLifecycleOwner) {
            assetsAdapter.updateItemsList(it)
        }
    }

}