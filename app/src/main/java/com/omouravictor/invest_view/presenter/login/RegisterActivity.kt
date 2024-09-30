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
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityRegisterBinding
import com.omouravictor.invest_view.presenter.MainActivity
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.util.getGenericErrorMessage
import com.omouravictor.invest_view.util.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.incBtnRegister.root.apply {
            text = getString(R.string.register)
            setOnClickListener { register() }
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
        binding.registerLayout.isVisible = isVisible
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

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty())
            loginViewModel.register(email, password)
        else
            showErrorSnackBar(getString(R.string.fillAllFields))
    }

}