package com.omouravictor.br_investidor.presenter.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.br_investidor.data.remote.firebase.FirebaseRepository
import com.omouravictor.br_investidor.presenter.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    fun setUser(user: UserUiModel) {
        _user.value = user
    }

    fun updateUser(user: UserUiModel) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val updatedUser = firebaseRepository.saveUser(user).getOrThrow()
                _user.value = updatedUser
                _userUiState.value = UiState.Success(updatedUser)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val loggedUser = getLoggedUser()
                reauthenticateUser(loggedUser, currentPassword)
                loggedUser.updatePassword(newPassword).await()
                _userUiState.value = UiState.Success(user.value)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun deleteUser(password: String) {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val loggedUser = getLoggedUser()
                reauthenticateUser(loggedUser, password)

                val deletedUser = firebaseRepository.deleteUser(user.value).getOrThrow()
                loggedUser.delete().await()

                _userUiState.value = UiState.Success(deletedUser)

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

    private fun getLoggedUser() = auth.currentUser ?: throw Exception("User not logged in")

    private suspend fun reauthenticateUser(loggedUser: FirebaseUser, password: String) {
        loggedUser
            .reauthenticate(EmailAuthProvider.getCredential(user.value.email, password))
            .await()
    }

}