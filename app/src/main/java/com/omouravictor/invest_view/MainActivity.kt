package com.omouravictor.invest_view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.ui.wallet.WalletFragmentDirections

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
            topLevelDestinationIds = setOf(
                R.id.fragmentWallet,
                R.id.fragmentExchange
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
                R.id.walletNavMenu -> {
                    onNavDestinationSelected(menuItem)
                    true
                }

                R.id.exchangeNavMenu -> {
                    onNavDestinationSelected(menuItem)
                    true
                }

                else -> false
            }
        }

        supportActionBar?.title = navController.currentDestination?.label
    }

    private fun onNavDestinationSelected(item: MenuItem) {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (
            navController.currentDestination!!.parent!!.findNode(item.itemId)
                    is ActivityNavigator.Destination
        ) {
            builder.setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
        } else {
            builder.setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
        }
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        navController.navigate(item.itemId, null, options)
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
            R.id.add_asset -> navController.navigate(WalletFragmentDirections.navToSelectAssetTypeFragment())
            R.id.add_coin -> {
                Toast.makeText(this, "Add Coin", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun hideToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, false)
        menu.setGroupVisible(R.id.exchange_group, false)
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        exchangeGroupVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, walletGroupVisible)
        menu.setGroupVisible(R.id.exchange_group, exchangeGroupVisible)
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
}