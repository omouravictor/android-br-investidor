package com.omouravictor.invest_view.ui.new_asset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewAssetViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is new assets Fragment"
    }
    val text: LiveData<String> = _text
}