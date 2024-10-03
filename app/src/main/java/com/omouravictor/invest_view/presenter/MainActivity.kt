package com.omouravictor.invest_view.presenter

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityMainBinding
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.user.UserUiModel
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.presenter.user.getFormattedName
import com.omouravictor.invest_view.presenter.wallet.WalletFragmentDirections
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.ConstantUtil.USER_ID_INTENT_EXTRA
import com.omouravictor.invest_view.util.clearPileAndNavigateTo
import com.omouravictor.invest_view.util.setupToolbarSubtitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val userViewModel: UserViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra(USER_ID_INTENT_EXTRA)!!
        userViewModel.getUser(userId)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupMainNavigation()
        setupBottomNavigationView()
        addOnApplyWindowInsetsListener()
        observeUserUiState()
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
                R.id.fragmentWallet -> {
                    binding.tvToolbarCenterText.isVisible = false
                    setupOptionsMenuForWallet()
                    setupToolbarSubtitle(userViewModel.user.value)
                }

                R.id.fragmentSaveAsset, R.id.fragmentTransaction -> {
                    binding.tvToolbarCenterText.isVisible = true
                    setupOptionsMenu()
                    setupToolbarSubtitle(null)
                }

                else -> {
                    binding.tvToolbarCenterText.isVisible = false
                    setupOptionsMenu()
                    setupToolbarSubtitle(null)
                }
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

    private fun setupOptionsMenu(
        walletGroupVisible: Boolean = false
    ) {
        val menu = binding.toolbar.menu
        menu.setGroupVisible(R.id.walletGroup, walletGroupVisible)
    }

    private fun setupOptionsMenuForWallet() = setupOptionsMenu(walletGroupVisible = true)

    private fun observeUserUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Success -> {
                            binding.mainLayout.isVisible = true
                            binding.incProgressBar.root.isVisible = false
                            val user = it.data
                            setupToolbarSubtitle(user)
                            walletViewModel.getUserAssetList(user.uid)
                        }

                        is UiState.Error -> {
                            // todo: implementar lógica caso falhar ter botão para tentar denovo
                            binding.mainLayout.isVisible = false
                            binding.incProgressBar.root.isVisible = false
                        }

                        else -> {
                            binding.mainLayout.isVisible = false
                            binding.incProgressBar.root.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbarSubtitle(user: UserUiModel?) {
        if (user != null)
            setupToolbarSubtitle("Olá, ${user.getFormattedName()}")
        else
            setupToolbarSubtitle("")
    }
}