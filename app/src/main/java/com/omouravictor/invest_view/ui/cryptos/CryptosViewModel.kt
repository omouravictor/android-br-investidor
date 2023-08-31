package com.omouravictor.invest_view.ui.cryptos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CryptosViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cryptos Fragment"
    }
    val text: LiveData<String> = _text
}