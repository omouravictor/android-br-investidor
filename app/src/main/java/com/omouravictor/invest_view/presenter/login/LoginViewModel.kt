package com.omouravictor.invest_view.presenter.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.invest_view.presenter.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userUiState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()

    init {
        val currentUser = auth.currentUser
        if (currentUser != null)
            _userUiState.value = UiState.Success(currentUser)
    }

    fun login(email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                _userUiState.value = UiState.Success(authResult.user)
            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun register(email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                _userUiState.value = UiState.Success(authResult.user)
            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }
}