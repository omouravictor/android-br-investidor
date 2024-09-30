package com.omouravictor.invest_view.presenter.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.omouravictor.invest_view.presenter.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
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
                val user = authResult.user

                if (user != null) {
                    _userUiState.value = UiState.Success(user)
                } else {
                    _userUiState.value = UiState.Error(Exception("User is null"))
                }
            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user

                if (user != null) {
                    firestore.collection("users").document(user.uid).set("name" to name).await()
                    _userUiState.value = UiState.Success(user)
                } else {
                    _userUiState.value = UiState.Error(Exception("User is null"))
                }
            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetUserUiState() {
        _userUiState.value = UiState.Initial
    }
}