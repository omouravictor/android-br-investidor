package com.omouravictor.invest_view.presenter

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.presenter.wallet.WalletFragmentDirections
import com.omouravictor.invest_view.util.clearPileAndNavigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupMainNavigation()
        setupBottomNavigationView()
        addOnApplyWindowInsetsListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (navController.currentDestination?.id) {
            R.id.fragmentWallet -> setupOptionsMenuForWallet()
            else -> setupOptionsMenu()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addAssetMenuItem -> navController.navigate(WalletFragmentDirections.navToAssetSearchFragment())
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupMainNavigation() {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.fragmentWallet, R.id.fragmentProfile))

        navController =
            (supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment).navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val destinationId = destination.id

            when (destination.id) {
                R.id.fragmentWallet -> setupOptionsMenuForWallet()
                else -> setupOptionsMenu()
            }

            setupToolbarCenterTextVisibility(destinationId)
        }

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        supportActionBar?.title = navController.currentDestination?.label
    }

    private fun setupToolbarCenterTextVisibility(destinationId: Int) {
        binding.tvToolbarCenterText.isGone =
            destinationId != R.id.fragmentSaveAsset &&
                    destinationId != R.id.fragmentAssetDetail &&
                    destinationId != R.id.fragmentNewAddition
    }

    private fun setupBottomNavigationView() {
        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            val menuItemStartDestination = navController.graph.findNode(menuItem.itemId)!!.id
            navController.clearPileAndNavigateTo(menuItemStartDestination)
            true
        }
    }

    private fun addOnApplyWindowInsetsListener() {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = toWindowInsetsCompat(insets, view)
            binding.bottomNav.isGone = insetsCompat.isVisible(ime())
            view.onApplyWindowInsets(insets)
        }
    }

    private fun setupOptionsMenu(
        walletGroupVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.walletGroup, walletGroupVisible)
    }

    private fun setupOptionsMenuForWallet() = setupOptionsMenu(walletGroupVisible = true)

}