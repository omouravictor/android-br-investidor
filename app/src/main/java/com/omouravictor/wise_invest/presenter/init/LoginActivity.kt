package com.omouravictor.wise_invest.presenter.init

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityLoginBinding
import com.omouravictor.wise_invest.presenter.MainActivity
import com.omouravictor.wise_invest.presenter.user.UserUiModel
import com.omouravictor.wise_invest.util.ConstantUtil.USER_UI_MODEL_INTENT_EXTRA
import com.omouravictor.wise_invest.util.FirebaseConstants
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.showErrorSnackBar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkLoggedInUser()
        setupViews()
    }

    private fun checkLoggedInUser() {
        loginLayoutIsLoading(true)
        val loggedUser = auth.currentUser
        if (loggedUser != null) loadUser(loggedUser.uid)
    }

    private fun setupViews() {
        binding.tvTittle.text = SpannableString(getString(R.string.appNameTitle)).apply {
            val startIndex = indexOf(" ") + 1
            setSpan(ForegroundColorSpan(getColor(R.color.green)), startIndex, length, 0)
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
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

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            loginLayoutIsLoading(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { loadUser(it.user!!.uid) }
                .addOnFailureListener { handleFailure(it) }

        } else {
            showErrorSnackBar(getString(R.string.fillAllFields))
        }
    }

    private fun loginLayoutIsLoading(isLoading: Boolean) {
        binding.loginLayout.isVisible = !isLoading
        binding.incProgressBar.root.isVisible = isLoading
    }

    private fun handleFailure(exception: Exception) {
        loginLayoutIsLoading(false)
        showErrorSnackBar(getErrorMessage(exception))
    }

    private fun loadUser(userId: String) {
        firestore
            .collection(FirebaseConstants.COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(UserUiModel::class.java)
                if (user != null) goToMainActivity(user)
                else saveUser(UserUiModel(uid = userId))
            }
            .addOnFailureListener { handleFailure(it) }
    }

    private fun goToMainActivity(userUiModel: UserUiModel) {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(USER_UI_MODEL_INTENT_EXTRA, userUiModel)
        )
        finish()
    }

    private fun saveUser(user: UserUiModel) {
        firestore
            .collection(FirebaseConstants.COLLECTION_USERS)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { goToMainActivity(user) }
            .addOnFailureListener { handleFailure(it) }
    }

}