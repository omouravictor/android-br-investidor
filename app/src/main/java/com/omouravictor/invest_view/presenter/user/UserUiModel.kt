package com.omouravictor.invest_view.presenter.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiModel(
    val uid: String = "",
    val name: String = ""
) : Parcelable
