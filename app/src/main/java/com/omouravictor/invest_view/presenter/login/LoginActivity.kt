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
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityLoginBinding
import com.omouravictor.invest_view.presenter.MainActivity
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.userUiState.collectLatest {
                    handleUserUiState(it)
                }
            }
        }
    }

    private fun loginLayoutIsVisible(isVisible: Boolean) {
        binding.loginLayout.isVisible = isVisible
        binding.incProgressBar.root.isVisible = !isVisible
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleUserUiState(userUiState: UiState<FirebaseUser?>) {
        when (userUiState) {
            is UiState.Initial -> loginLayoutIsVisible(true)
            is UiState.Loading -> loginLayoutIsVisible(false)
            is UiState.Success -> startMainActivity()
            is UiState.Error -> {
                loginLayoutIsVisible(true)

                val message = when (val exception = userUiState.e) {
                    is FirebaseAuthWeakPasswordException -> getString(R.string.weakPassword)
                    is FirebaseAuthInvalidCredentialsException -> getString(R.string.invalidCredentials)
                    is FirebaseAuthUserCollisionException -> getString(R.string.userAlreadyExists)
                    is FirebaseTooManyRequestsException -> getString(R.string.tooManyRequests)
                    else -> "${getString(R.string.loginError)}: ${getGenericErrorMessage(exception)}."
                }

                showErrorSnackBar(message)
            }
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

}