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

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_fragment_wallet),
            binding.drawerLayout
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_fragment_wallet -> handleWalletDestination()
                R.id.fragment_select_asset_type -> handleSelectAssetTypeDestination()
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
            R.id.nav_fragment_wallet -> setupToolbarMenu(walletGroupVisible = true)
            R.id.fragment_select_asset_type -> setupToolbarMenu(newAssetGroupVisible = true)
            R.id.fragment_select_asset -> setupToolbarMenu(newAssetGroupVisible = true)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> {
                navController.navigate(R.id.action_wallet_to_select_asset_type)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        newAssetGroupVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, walletGroupVisible)
        menu.setGroupVisible(R.id.new_asset_group, newAssetGroupVisible)
    }

    private fun setupDrawerLayout(lockMode: Int) {
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }

    private fun handleWalletDestination() {
        setupToolbarMenu(walletGroupVisible = true)
        setupDrawerLayout(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun handleSelectAssetTypeDestination() {
        setupToolbarMenu(newAssetGroupVisible = true)
        setupDrawerLayout(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}