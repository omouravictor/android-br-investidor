package com.omouravictor.invest_view.presenter

import android.os.Build
import android.os.Bundle
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
import com.omouravictor.invest_view.presenter.user.UserUiModel
import com.omouravictor.invest_view.presenter.user.UserViewModel
import com.omouravictor.invest_view.presenter.user.getFormattedName
import com.omouravictor.invest_view.presenter.wallet.WalletViewModel
import com.omouravictor.invest_view.util.ConstantUtil
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        getUser()
        observeUser()
        setupMainNavigation()
        setupBottomNavigationView()
        addOnApplyWindowInsetsListener()
    }

    private fun getUser() {
        val user: UserUiModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(ConstantUtil.USER_UI_MODEL_INTENT_EXTRA, UserUiModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(ConstantUtil.USER_UI_MODEL_INTENT_EXTRA)
        }!!

        userViewModel.updateUser(user)
        walletViewModel.getUserAssetList(user.uid)
    }

    private fun observeUser() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.user.collectLatest {
                    setupToolbarSubtitle(it)
                }
            }
        }
    }

    private fun setupToolbarForWallet() {
        binding.tvToolbarCenterText.isVisible = false
        setupToolbarSubtitle(userViewModel.user.value)
    }

    private fun setupToolbarWithCenterText() {
        binding.tvToolbarCenterText.isVisible = true
        setupToolbarSubtitle(null)
    }

    private fun setupToolbar() {
        binding.tvToolbarCenterText.isVisible = false
        setupToolbarSubtitle(null)
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
                R.id.fragmentWallet -> setupToolbarForWallet()
                R.id.fragmentSaveAsset, R.id.fragmentTransaction -> setupToolbarWithCenterText()
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

    private fun setupToolbarSubtitle(user: UserUiModel?) {
        if (user != null)
            setupToolbarSubtitle("Olá, ${user.getFormattedName()}")
        else
            setupToolbarSubtitle("")
    }
}