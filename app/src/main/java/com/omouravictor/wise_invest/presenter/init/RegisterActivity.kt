package com.omouravictor.wise_invest.presenter.init

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityRegisterBinding
import com.omouravictor.wise_invest.presenter.MainActivity
import com.omouravictor.wise_invest.presenter.user.UserUiModel
import com.omouravictor.wise_invest.util.ConstantUtil
import com.omouravictor.wise_invest.util.getErrorMessage
import com.omouravictor.wise_invest.util.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
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

    private fun register() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            registerLayoutIsLoading(true)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { saveUser(UserUiModel(it.user!!.uid, name)) }
                .addOnFailureListener { handleFailure(it) }

        } else {
            showErrorSnackBar(getString(R.string.fillAllFields))
        }
    }

    private fun registerLayoutIsLoading(isLoading: Boolean) {
        binding.registerLayout.isVisible = !isLoading
        binding.incProgressBar.root.isVisible = isLoading
    }

    private fun saveUser(user: UserUiModel) {
        firestore
            .collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { goToMainActivity(user) }
            .addOnFailureListener { handleFailure(it) }
    }

    private fun handleFailure(exception: Exception) {
        registerLayoutIsLoading(false)
        showErrorSnackBar(getErrorMessage(exception))
    }

    private fun goToMainActivity(userUiModel: UserUiModel) {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(ConstantUtil.USER_UI_MODEL_INTENT_EXTRA, userUiModel)
        )
        finish()
    }

}