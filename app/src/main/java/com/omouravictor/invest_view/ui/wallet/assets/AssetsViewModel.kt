package com.omouravictor.invest_view.ui.wallet.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.omouravictor.invest_view.data.repository.AssetsRepository
import com.omouravictor.invest_view.di.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AssetsViewModel @Inject constructor(
    private val assetsRepository: AssetsRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is assets Fragment"
    }
    val text: LiveData<String> = _text

    fun getAssets() {
//        viewModelScope.launch(dispatchers.io) {
//            assetsRepository.getRemoteAssets(apiFields).collectLatest { result ->
//                when (result) {
//                    is ResultStatus.Success -> handleNetworkSuccessResult(result.data)
//                    is ResultStatus.Error -> handleNetworkErrorResult(result.message)
//                    is ResultStatus.Loading -> handleNetworkLoadingResult()
//                }
//            }
//        }
    }
}