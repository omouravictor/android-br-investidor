package com.omouravictor.invest_view.presenter.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.presenter.model.UiState
import com.omouravictor.invest_view.presenter.user.UserUiModel
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

    private val _userUiState = MutableStateFlow<UiState<Unit>>(UiState.Initial)
    val userUiState = _userUiState.asStateFlow()

    init {
        _userUiState.value = UiState.Loading

        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            viewModelScope.launch {
                try {
                    checkUserIsSavedOrSave(loggedUser)
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

                checkUserIsSavedOrSave(loggedUser)

                _userUiState.value = UiState.Success(Unit)

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

                saveUser(UserUiModel(loggedUser.uid, name))

                _userUiState.value = UiState.Success(Unit)

            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

    fun resetUserUiState() {
        _userUiState.value = UiState.Initial
    }

    private suspend fun checkUserIsSavedOrSave(user: FirebaseUser) {
        val savedUser = firebaseRepository
            .getUser(user.uid)
            .getOrThrow()

        if (savedUser != null)
            _userUiState.value = UiState.Success(Unit)
        else
            saveUser(UserUiModel(uid = user.uid))
    }

    private suspend fun saveUser(user: UserUiModel) {
        firebaseRepository
            .saveUser(user)
            .getOrThrow()
    }

}