package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUiModel(
    val uid: String = "",
    val name: String = ""
) : Parcelable
