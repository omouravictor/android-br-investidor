package com.omouravictor.invest_view.ui.exchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExchangeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is exchange Fragment"
    }
    val text: LiveData<String> = _text
}