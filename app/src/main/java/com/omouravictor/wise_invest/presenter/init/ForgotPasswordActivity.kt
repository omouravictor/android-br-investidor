package com.omouravictor.wise_invest.presenter.init

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.databinding.ActivityForgotPasswordBinding
import com.omouravictor.wise_invest.util.showErrorSnackBar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    private fun resetPassword(email: String) {
        binding.incProgressBar.root.visibility = View.VISIBLE
        binding.incBtnReset.root.visibility = View.INVISIBLE
        mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Reset Password link has been sent to your registered Email",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error :- " + e.message, Toast.LENGTH_SHORT).show()
                binding.incProgressBar.root.visibility = View.INVISIBLE
                binding.incBtnReset.root.visibility = View.VISIBLE
            }
    }

}