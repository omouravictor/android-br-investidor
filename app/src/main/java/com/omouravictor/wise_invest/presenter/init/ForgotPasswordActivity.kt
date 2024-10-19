package com.omouravictor.wise_invest.presenter.init

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityForgotPasswordBinding
import com.omouravictor.wise_invest.util.showErrorSnackBar
import com.omouravictor.wise_invest.util.showSuccessSnackBar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.incBtnReset.root.apply {
            text = getString(R.string.sendLink)
            setOnClickListener {
                val email = binding.edtForgotPasswordEmail.text.toString().trim()
                if (email.isNotEmpty())
                    resetPassword(email)
                else
                    showErrorSnackBar(getString(R.string.fillAllFields))
            }
        }

        binding.incBtnBack.root.apply {
            text = getString(R.string.back)
            setOnClickListener {
                startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun layoutIsLoading(isLoading: Boolean) {
        binding.incProgressBar.root.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        binding.incBtnReset.root.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    private fun resetPassword(email: String) {
        layoutIsLoading(true)
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                layoutIsLoading(false)
                showSuccessSnackBar(getString(R.string.recoveryLinkHasBeenSentMessage), hasCloseAction = true)
            }
            .addOnFailureListener {
                layoutIsLoading(false)
                showErrorSnackBar(getString(R.string.errorOnSendRecoveryLink))
            }
    }

}