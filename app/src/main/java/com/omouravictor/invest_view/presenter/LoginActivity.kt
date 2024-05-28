package com.omouravictor.invest_view.presenter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ActivityLoginBinding
import com.omouravictor.invest_view.util.AppUtil.showSnackBar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener { login() }
        binding.btnRegister.setOnClickListener { register() }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if (currentUser != null)
            startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startMainActivity()
                } else {
                    val message = when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> getString(R.string.invalidCredentials)
                        else -> "${getString(R.string.loginError)}: ${exception?.message}"
                    }

                    showSnackBar(this, message, isError = true)
                }
            }
        } else {
            showSnackBar(this, getString(R.string.fillAllFields), isError = true)
        }
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startMainActivity()
                } else {
                    val message = when (val exception = task.exception) {
                        is FirebaseAuthWeakPasswordException -> getString(R.string.weakPassword)
                        is FirebaseAuthUserCollisionException -> getString(R.string.userAlreadyExists)
                        else -> "${getString(R.string.registerError)}: ${exception?.message}."
                    }

                    showSnackBar(this, message, isError = true)
                }
            }
        } else {
            showSnackBar(this, getString(R.string.fillAllFields), isError = true)
        }
    }

}