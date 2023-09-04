package com.omouravictor.invest_view.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewRecordViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is new record Fragment"
    }
    val text: LiveData<String> = _text
}