package com.omouravictor.wise_invest.presenter.wallet.currency_exchange_rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.model.toCurrencyExchangeRateUiModel
import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.repository.CurrencyExchangeRatesApiRepository
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.wallet.model.CurrencyExchangeRateUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyExchangeRatesViewModel @Inject constructor(
    private val currencyExchangeRatesApiRepository: CurrencyExchangeRatesApiRepository
) : ViewModel() {

    private val _getExchangeRateUiState =
        MutableStateFlow<UiState<CurrencyExchangeRateUiModel>>(UiState.Initial)
    val getExchangeRateUiState = _getExchangeRateUiState.asStateFlow()

    private val _exchangeRate = MutableStateFlow<CurrencyExchangeRateUiModel?>(null)
    val exchangeRate = _exchangeRate.asStateFlow()

    fun getExchangeRate(baseCurrency: String, currencies: String) {
        _getExchangeRateUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = currencyExchangeRatesApiRepository.getLatestRate(baseCurrency, currencies)
                    .getOrThrow()
                    .toCurrencyExchangeRateUiModel()

                _exchangeRate.value = result
                _getExchangeRateUiState.value = UiState.Success(result)

            } catch (e: Exception) {
                _exchangeRate.value = null
                _getExchangeRateUiState.value = UiState.Error(e)
            }
        }
    }

}