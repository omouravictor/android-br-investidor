package com.omouravictor.wise_invest.presenter.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.wise_invest.data.remote.repository.FirebaseRepository
import com.omouravictor.wise_invest.presenter.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _userUiState = MutableStateFlow<UiState<UserUiModel>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()
    private val _user = MutableStateFlow(UserUiModel())
    val user = _user.asStateFlow()

    init {
        _userUiState.value = UiState.Loading

        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            viewModelScope.launch {
                try {
                    val user = getSavedUser(loggedUser)
                    _userUiState.value = UiState.Success(user)
                } catch (e: Exception) {
                    _userUiState.value = UiState.Error(e)
                }
            }
        } else {
            _userUiState.value = UiState.Initial
        }
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

    fun updateUser(user: UserUiModel) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val updatedUser = firebaseRepository
                    .saveUser(user)
                    .getOrThrow()

                _user.value = updatedUser
                _userUiState.value = UiState.Success(updatedUser)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun resetUserUiState() {
        _userUiState.value = UiState.Initial
    }

}