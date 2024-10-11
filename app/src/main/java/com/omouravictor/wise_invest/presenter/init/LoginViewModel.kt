package com.omouravictor.wise_invest.presenter.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.wise_invest.data.remote.repository.FirebaseRepository
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.user.UserUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _userUiState = MutableStateFlow<UiState<UserUiModel>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()

    init {
        _userUiState.value = UiState.Loading

        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            viewModelScope.launch {
                try {
                    _userUiState.value = UiState.Success(getSavedUser(loggedUser))
                } catch (e: Exception) {
                    _userUiState.value = UiState.Error(e)
                }
            }
        } else {
            _userUiState.value = UiState.Initial
        }
    }

    fun login(email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val loggedUser = auth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                    .user!!

                val user = getSavedUser(loggedUser)

                _userUiState.value = UiState.Success(user)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val loggedUser = auth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
                    .user!!

                val savedUser = saveUser(UserUiModel(loggedUser.uid, name))

                _userUiState.value = UiState.Success(savedUser)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetUserUiState() {
        _userUiState.value = UiState.Initial
    }

    private suspend fun getSavedUser(user: FirebaseUser): UserUiModel {
        return firebaseRepository
            .getUser(user.uid)
            .getOrThrow() ?: saveUser(UserUiModel(uid = user.uid))
    }

    private suspend fun saveUser(user: UserUiModel): UserUiModel {
        return firebaseRepository
            .saveUser(user)
            .getOrThrow()
    }

}