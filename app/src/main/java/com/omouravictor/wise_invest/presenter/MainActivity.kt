package com.omouravictor.wise_invest.presenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityMainBinding
import com.omouravictor.wise_invest.util.clearPileAndNavigateTo
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

    private fun setupToolbarWithCenterText() {
        binding.tvToolbarCenterText.isVisible = true
    }

    private fun setupToolbar() {
        binding.tvToolbarCenterText.isVisible = false
    }

    private fun setupMainNavigation() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragmentWallet,
                R.id.fragmentNews,
                R.id.fragmentProfile
            )
        )

        navController =
            (supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment).navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentSaveAsset,
                R.id.fragmentTransaction,
                R.id.changePersonalDataFragment -> setupToolbarWithCenterText()

                else -> setupToolbar()
            }
        }

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        supportActionBar?.title = navController.currentDestination?.label
    }

    private fun setupBottomNavigationView() {
        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            val menuItemStartDestination = navController.graph.findNode(menuItem.itemId)?.id
                ?: return@setOnItemSelectedListener false
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

}