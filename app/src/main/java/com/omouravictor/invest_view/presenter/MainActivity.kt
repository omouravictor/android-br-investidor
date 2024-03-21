package com.omouravictor.invest_view.presenter

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
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
        setupNavigation()
        setupBottomNavigationView()
        addOnGlobalFocusChangeListener()
        addOnApplyWindowInsetsListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu_main, menu)
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
            R.id.addAsset -> navController.navigate(WalletFragmentDirections.navToAssetSearchFragment())
            R.id.addCoin -> {
                Toast.makeText(this, "Add Coin", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigation() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment).navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentWallet, R.id.fragmentExchange, R.id.fragmentProfile)
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
        supportActionBar?.title = navController.currentDestination?.label
    }

    private fun setupBottomNavigationView() {
        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.walletNavMenu,
                R.id.exchangeNavMenu,
                R.id.profileNavMenu -> navigateToMenuItem(menuItem.itemId)

                else -> false
            }
        }
    }

    private fun addOnApplyWindowInsetsListener() {
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = toWindowInsetsCompat(insets, view)
            binding.bottomNav.isGone = insetsCompat.isVisible(ime())
            view.onApplyWindowInsets(insets)
        }
    }

    private fun addOnGlobalFocusChangeListener() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener { oldView, newView ->
            if (newView !is EditText) {
                imm.hideSoftInputFromWindow(
                    (oldView ?: newView)?.windowToken ?: window.attributes.token, 0
                )
            }
        }
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