package com.omouravictor.invest_view.ui.wallet.coins

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoinsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is coins Fragment"
    }
    val text: LiveData<String> = _text
}