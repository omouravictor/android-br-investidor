package com.omouravictor.invest_view.presenter.user

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {

    private val _user = MutableStateFlow(UserUiModel())
    val user = _user.asStateFlow()

    fun updateUser(user: UserUiModel) {
        _user.value = user
    }
}