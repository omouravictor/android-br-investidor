package com.omouravictor.invest_view.presenter.init

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
import com.omouravictor.invest_view.presenter.user.UserUiModel
import com.omouravictor.invest_view.util.ConstantUtil
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
        binding.tvTittle.text = SpannableString(getString(R.string.appNameTitle)).apply {
            val startIndex = indexOf(" ") + 1
            setSpan(ForegroundColorSpan(getColor(R.color.green)), startIndex, length, 0)
        }

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

    private fun registerLayoutIsVisible(isVisible: Boolean) {
        binding.registerLayout.isVisible = isVisible
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.userUiState.collectLatest {
                    when (it) {
                        is UiState.Initial -> registerLayoutIsVisible(true)
                        is UiState.Loading -> registerLayoutIsVisible(false)
                        is UiState.Success -> handleUserSuccess(it.data)
                        is UiState.Error -> {
                            registerLayoutIsVisible(true)
                            showErrorSnackBar(getErrorMessage(it.e))
                        }
                    }
                }
            }
        }
    }

}