package com.omouravictor.invest_view.presenter.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityLoginBinding
import com.omouravictor.invest_view.presenter.MainActivity
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.model.UserUiModel
import com.omouravictor.invest_view.util.ConstantUtil.USER_UI_MODEL_INTENT_EXTRA
import com.omouravictor.invest_view.util.getErrorMessage
import com.omouravictor.invest_view.util.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        observeUserUiState()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.resetUserUiState()
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

    private fun startMainActivity(data: UserUiModel) {
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(USER_UI_MODEL_INTENT_EXTRA, data)

        startActivity(intent)
        finish()
    }

    private fun observeUserUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Initial -> loginLayoutIsVisible(true)
                        is UiState.Loading -> loginLayoutIsVisible(false)
                        is UiState.Success -> startMainActivity(it.data)
                        is UiState.Error -> {
                            loginLayoutIsVisible(true)
                            showErrorSnackBar(getErrorMessage(it.e))
                        }
                    }
                }
            }
        }
    }

}