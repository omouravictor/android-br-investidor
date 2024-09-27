package com.omouravictor.invest_view.presenter.wallet.currency_exchange_rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.ConversionResultResponse
import com.omouravictor.invest_view.data.remote.repository.CurrencyExchangeRatesApiRepository
import com.omouravictor.invest_view.presenter.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyExchangeRatesViewModel @Inject constructor(
    private val currencyExchangeRatesApiRepository: CurrencyExchangeRatesApiRepository
) : ViewModel() {

    private val _getConversionResultUiState = MutableStateFlow<UiState<ConversionResultResponse>>(UiState.Initial)
    val getConversionResultUiState = _getConversionResultUiState.asStateFlow()

    fun convert(fromCurrency: String, toCurrency: String) {
        _getConversionResultUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = currencyExchangeRatesApiRepository.convert(fromCurrency, toCurrency)
                if (result.isSuccess)
                    _getConversionResultUiState.value = UiState.Success(result.getOrThrow())
                else
                    _getConversionResultUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getConversionResultUiState.value = UiState.Error(e)
            }
        }
    }

}