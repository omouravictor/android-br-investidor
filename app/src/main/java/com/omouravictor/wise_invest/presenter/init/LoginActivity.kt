package com.omouravictor.wise_invest.presenter.init

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityLoginBinding
import com.omouravictor.wise_invest.presenter.MainActivity
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserUiModel
import com.omouravictor.wise_invest.util.ConstantUtil
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        checkUserLoggedIn()
    }

    override fun onStart() {
        super.onStart()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        observeUserUiState()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.resetUserUiState()
    }

    private fun checkUserLoggedIn() {
        if (loginViewModel.userLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty())
            loginViewModel.login(email, password)
        else
            showErrorSnackBar(getString(R.string.fillAllFields))
    }

    private fun setupViews() {
        binding.tvTittle.text = SpannableString(getString(R.string.appNameTitle)).apply {
            val startIndex = indexOf(" ") + 1
            setSpan(ForegroundColorSpan(getColor(R.color.green)), startIndex, length, 0)
        }

        binding.incBtnLogin.root.apply {
            text = getString(R.string.login)
            setOnClickListener { login() }
        }

        binding.tvRegister.apply {
            text = SpannableString(getString(R.string.registerMessage)).apply {
                val startIndex = indexOf("?") + 1
                setSpan(StyleSpan(Typeface.BOLD), startIndex, length, 0)
                setSpan(ForegroundColorSpan(getColor(R.color.defaultButtonBackgroundColor)), startIndex, length, 0)
            }

            setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun loginLayoutIsVisible(isVisible: Boolean) {
        binding.loginLayout.isVisible = isVisible
        binding.incProgressBar.root.isVisible = !isVisible
    }

    private fun handleUserSuccess(userUiModel: UserUiModel) {
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(ConstantUtil.USER_UI_MODEL_INTENT_EXTRA, userUiModel)

        startActivity(intent)
        finish()
    }

    private fun observeUserUiState() {
        lifecycleScope.launch {
            loginViewModel.userUiState.collectLatest {
                when (it) {
                    is UiState.Initial -> loginLayoutIsVisible(true)
                    is UiState.Loading -> loginLayoutIsVisible(false)
                    is UiState.Success -> handleUserSuccess(it.data)
                    is UiState.Error -> {
                        loginLayoutIsVisible(true)
                        showErrorSnackBar(getErrorMessage(it.e))
                    }
                }
            }
        }
    }

}