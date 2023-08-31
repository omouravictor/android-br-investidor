package com.omouravictor.invest_view.ui.stocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StocksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is stocks Fragment"
    }
    val text: LiveData<String> = _text
}