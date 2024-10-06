package com.omouravictor.invest_view.presenter.user

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow(UserUiModel())
    val user = _user.asStateFlow()

    fun updateUser(user: UserUiModel) {
        _user.value = user
    }

    fun logout() {
        auth.signOut()
    }

}