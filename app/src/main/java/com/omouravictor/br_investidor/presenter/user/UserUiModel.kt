package com.omouravictor.br_investidor.presenter.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiModel(
    val uid: String = "",
    val email: String = "",
    val name: String = ""
) : Parcelable

fun UserUiModel.getFormattedName() = name.substringBefore(" ")