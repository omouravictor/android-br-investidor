package com.omouravictor.invest_view.presenter.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.wallet.model.UserUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val auth = Firebase.auth
    private val _userUiState = MutableStateFlow<UiState<UserUiModel>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUser(auth.currentUser)
        }
    }

    fun login(email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()

                getUser(authResult.user)

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
                    val savedUser = firebaseRepository.saveUser(UserUiModel(user.uid, name))

                    if (savedUser.isSuccess)
                        _userUiState.value = UiState.Success(savedUser.getOrThrow())
                    else
                        _userUiState.value = UiState.Error(savedUser.exceptionOrNull() as Exception)

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

    private suspend fun getUser(user: FirebaseUser?) {
        if (user != null) {
            try {
                val result = firebaseRepository.getUser("user.uid")
                _userUiState.value = UiState.Success(result.getOrThrow())

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        } else {
            _userUiState.value = UiState.Error(Exception("User is null"))
        }
    }
}