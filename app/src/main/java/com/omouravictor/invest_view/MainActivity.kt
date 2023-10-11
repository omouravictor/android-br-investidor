package com.omouravictor.invest_view

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
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
    var saveItemClickAction: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragmentWallet),
            binding.drawerLayout
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentWallet -> handleWalletDestination()
                R.id.fragmentCreateAsset -> handleSelectAssetDestination()
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

            else -> hideToolbarMenu()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> navController.navigate(WalletFragmentDirections.navToSelectAssetTypeFragment())
            R.id.save_item -> saveItemClickAction()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setupSaveItemMenu(saveItemEnabled: Boolean) {
        isSaveItemEnabled = saveItemEnabled

        binding.toolbar.menu.findItem(R.id.save_item)?.apply {
            isEnabled = isSaveItemEnabled
            title = getColoredText(title.toString(), getSaveItemTextColor())
        }
    }

    private fun getSaveItemTextColor() =
        if (isSaveItemEnabled) getColor(R.color.green) else getColor(R.color.darkGray)

    private fun getColoredText(text: String, color: Int): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(color), 0, spannableString.length, 0)
        return spannableString
    }

    private fun hideToolbarMenu() {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, false)
        menu.findItem(R.id.save_item)?.isVisible = false
    }

    private fun setupToolbarMenu(
        walletGroupVisible: Boolean = false,
        saveItemVisible: Boolean = false,
        saveItemEnabled: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.wallet_group, walletGroupVisible)
        menu.findItem(R.id.save_item)?.isVisible = saveItemVisible
        setupSaveItemMenu(saveItemEnabled)
    }

    private fun setupDrawerLayout(lockMode: Int) {
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }

    private fun handleWalletDestination() {
        setupToolbarMenu(walletGroupVisible = true)
        setupDrawerLayout(LOCK_MODE_UNLOCKED)
    }

    private fun handleSelectAssetDestination() {
        setupToolbarMenu(saveItemVisible = true)
    }

    private fun handleDefaultDestination() {
        hideToolbarMenu()
        setupDrawerLayout(LOCK_MODE_LOCKED_CLOSED)
    }
}