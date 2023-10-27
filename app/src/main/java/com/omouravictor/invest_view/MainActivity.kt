package com.omouravictor.invest_view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.ui.wallet.WalletFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isSaveItemEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentWallet, R.id.fragmentExchange)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentWallet -> handleWalletDestination()
                R.id.fragmentCreateAsset -> handleSelectAssetDestination()
                R.id.fragmentExchange -> handleExchangeDestination()
                else -> handleDefaultDestination()
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (navController.currentDestination?.id) {
            R.id.fragmentWallet -> setupToolbarMenu(walletGroupVisible = true)

            R.id.fragmentCreateAsset -> setupToolbarMenu(
                saveItemVisible = true,
                saveItemEnabled = isSaveItemEnabled
            )

            R.id.fragmentExchange -> setupToolbarMenu(exchangeGroupVisible = true)

            else -> hideToolbarMenu()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_asset -> navController.navigate(WalletFragmentDirections.navToSelectAssetTypeFragment())
            R.id.add_coin -> {
                Toast.makeText(this, "Add Coin", Toast.LENGTH_SHORT).show()
            }

            R.id.save_item -> navController.popBackStack(R.id.fragmentWallet, false)
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupSaveItemMenu(saveItemEnabled: Boolean) {
        isSaveItemEnabled = saveItemEnabled
        binding.toolbar.menu.findItem(R.id.save_item)?.isEnabled = isSaveItemEnabled
    }

    private fun hideToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, false)
        menu.setGroupVisible(R.id.exchange_group, false)
        menu.findItem(R.id.save_item)?.isVisible = false
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        exchangeGroupVisible: Boolean = false,
        saveItemVisible: Boolean = false,
        saveItemEnabled: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, walletGroupVisible)
        menu.setGroupVisible(R.id.exchange_group, exchangeGroupVisible)
        menu.findItem(R.id.save_item)?.isVisible = saveItemVisible
        setupSaveItemMenu(saveItemEnabled)
    }

    private fun handleWalletDestination() {
        setupToolbarMenu(walletGroupVisible = true)
    }

    private fun handleSelectAssetDestination() {
        setupToolbarMenu(saveItemVisible = true)
    }

    private fun handleExchangeDestination() {
        setupToolbarMenu(exchangeGroupVisible = true)
    }

    private fun handleDefaultDestination() {
        hideToolbarMenu()
    }
}