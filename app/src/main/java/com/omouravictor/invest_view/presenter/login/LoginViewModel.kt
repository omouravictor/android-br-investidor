package com.omouravictor.invest_view.presenter.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.invest_view.presenter.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _userUiState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val currentUser = auth.currentUser
        if (currentUser != null)
            _userUiState.value = UiState.Success(currentUser)
    }

    fun login(email: String, password: String) {
        _userUiState.value = UiState.Loading

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userUiState.value = UiState.Success(task.result.user)
            } else {
                _userUiState.value = UiState.Error(task.exception ?: Exception())
            }
        }
    }

    fun register(email: String, password: String) {
        _userUiState.value = UiState.Loading

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userUiState.value = UiState.Success(task.result.user)
            } else {
                _userUiState.value = UiState.Error(task.exception ?: Exception())
            }
        }
    }
}