package com.omouravictor.invest_view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.ui.wallet.WalletFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.bottomNav.itemIconTintList = null

        navController =
            (supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment).navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.fragmentWallet,
                R.id.fragmentExchange,
                R.id.fragmentProfile
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentWallet -> handleWalletDestination()
                R.id.fragmentExchange -> handleExchangeDestination()
                else -> handleDefaultDestination()
            }
        }

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.walletNavMenu,
                R.id.exchangeNavMenu,
                R.id.profileNavMenu -> navigateToMenuItem(menuItem.itemId)

                else -> false
            }
        }

        supportActionBar?.title = navController.currentDestination?.label
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (navController.currentDestination?.id) {
            R.id.fragmentWallet -> setupToolbarMenu(walletGroupVisible = true)
            R.id.fragmentExchange -> setupToolbarMenu(exchangeGroupVisible = true)
            else -> hideToolbarMenu()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addAsset -> navController.navigate(WalletFragmentDirections.navToSelectAssetTypeFragment())
            R.id.addCoin -> {
                Toast.makeText(this, "Add Coin", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun hideToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.walletGroup, false)
        menu.setGroupVisible(R.id.exchangeGroup, false)
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        exchangeGroupVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.walletGroup, walletGroupVisible)
        menu.setGroupVisible(R.id.exchangeGroup, exchangeGroupVisible)
    }

    private fun handleWalletDestination() {
        setupToolbarMenu(walletGroupVisible = true)
    }

    private fun handleExchangeDestination() {
        setupToolbarMenu(exchangeGroupVisible = true)
    }

    private fun handleDefaultDestination() {
        hideToolbarMenu()
    }

    private fun navigateToMenuItem(menuItemId: Int): Boolean {
        val startDestination = navController.graph.findNode(menuItemId)!!.id
        navController.popBackStack(startDestination, true)
        navController.navigate(menuItemId)
        return true
    }
}