package com.omouravictor.invest_view.presenter.login

import android.content.Intent
import android.os.Bundle
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

        binding.incBtnRegister.root.apply {
            text = getString(R.string.register)
            setOnClickListener { register() }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.userUiState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Initial -> loginLayoutIsVisible(true)
                        is UiState.Loading -> loginLayoutIsVisible(false)
                        is UiState.Success -> startMainActivity()
                        is UiState.Error -> {
                            loginLayoutIsVisible(true)

                            val message = when (val exception = uiState.e) {
                                is FirebaseAuthWeakPasswordException -> getString(R.string.weakPassword)
                                is FirebaseAuthInvalidCredentialsException -> getString(R.string.invalidCredentials)
                                is FirebaseAuthUserCollisionException -> getString(R.string.userAlreadyExists)
                                is FirebaseTooManyRequestsException -> getString(R.string.tooManyRequests)
                                else -> "${getString(R.string.loginError)}: ${getGenericErrorMessage(exception)}."
                            }

                            showErrorSnackBar(message, hasCloseAction = true)
                        }
                    }
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

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty())
            loginViewModel.login(email, password)
        else
            showErrorSnackBar(getString(R.string.fillAllFields), hasCloseAction = true)
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty())
            loginViewModel.register(email, password)
        else
            showErrorSnackBar(getString(R.string.fillAllFields), hasCloseAction = true)
    }

}