package com.omouravictor.invest_view.ui.wallet.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrenciesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is currencies Fragment"
    }
    val text: LiveData<String> = _text
}