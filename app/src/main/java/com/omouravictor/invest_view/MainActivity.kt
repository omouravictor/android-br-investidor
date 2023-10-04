package com.omouravictor.invest_view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
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
            setOf(R.id.fragment_wallet),
            binding.drawerLayout
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_wallet -> handleWalletDestination()
                R.id.fragment_select_asset -> handleSelectAssetDestination()
                else -> {
                    clearToolbarMenu()
                    setupDrawerLayout(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
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
            R.id.fragment_wallet -> setupToolbarMenu(walletGroupVisible = true)
            R.id.fragment_select_asset -> setupToolbarMenu(saveItemVisible = true)
            else -> clearToolbarMenu()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> navController.navigate(WalletFragmentDirections.walletFragmentToSelectAssetTypeFragment())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun enableSaveItem(isEnabled: Boolean) {
        isSaveItemEnabled = isEnabled
        binding.toolbar.menu.findItem(R.id.save_item)?.isEnabled = isSaveItemEnabled
    }

    private fun clearToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, false)
        menu.findItem(R.id.save_item)?.isVisible = false
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        saveItemVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, walletGroupVisible)
        menu.findItem(R.id.save_item)?.isVisible = saveItemVisible
        menu.findItem(R.id.save_item)?.isEnabled = isSaveItemEnabled
    }

    private fun setupDrawerLayout(lockMode: Int) {
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }

    private fun handleWalletDestination() {
        setupToolbarMenu(walletGroupVisible = true)
        setupDrawerLayout(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun handleSelectAssetDestination() {
        setupToolbarMenu(saveItemVisible = true)
    }
}