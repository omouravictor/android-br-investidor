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
import com.omouravictor.invest_view.databinding.ActivityRegisterBinding
import com.omouravictor.invest_view.presenter.MainActivity
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.util.getErrorMessage
import com.omouravictor.invest_view.util.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        observeUserUiState()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.resetUserUiState()
    }

    private fun register() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty())
            loginViewModel.register(name, email, password)
        else
            showErrorSnackBar(getString(R.string.fillAllFields))
    }

    private fun setupViews() {
        binding.incBtnRegister.root.apply {
            text = getString(R.string.register)
            setOnClickListener { register() }
        }

        binding.tvLogin.apply {
            text = SpannableString(getString(R.string.doLoginMessage)).apply {
                val startIndex = indexOf("?") + 1
                setSpan(StyleSpan(Typeface.BOLD), startIndex, length, 0)
                setSpan(ForegroundColorSpan(getColor(R.color.defaultButtonBackgroundColor)), startIndex, length, 0)
            }

            setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun loginLayoutIsVisible(isVisible: Boolean) {
        binding.registerLayout.isVisible = isVisible
        binding.incProgressBar.root.isVisible = !isVisible
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun observeUserUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Initial -> loginLayoutIsVisible(true)
                        is UiState.Loading -> loginLayoutIsVisible(false)
                        is UiState.Success -> startMainActivity()
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