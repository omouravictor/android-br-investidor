package com.omouravictor.invest_view.presenter.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import com.omouravictor.invest_view.data.remote.repository.FirebaseRepository
import com.omouravictor.invest_view.presenter.model.UiState
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
        getUser()
    }

    fun getUser() {
        _userUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                    ?: throw FirebaseNoSignedInUserException("user not signed in")

                val result = firebaseRepository
                    .getUser(userId)
                    .getOrThrow()

                if (result != null) {
                    _user.value = result
                    _userUiState.value = UiState.Success(result)
                } else {
                    _userUiState.value = UiState.Error(
                        FirebaseFirestoreException(
                            "user not found in firestore", FirebaseFirestoreException.Code.NOT_FOUND
                        )
                    )
                }
            } catch (e: Exception) {
                _userUiState.value = UiState.Error(e)
            }
        }
    }

}