package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.omouravictor.invest_view.data.remote.api.AlphaVantageApi
import com.omouravictor.invest_view.data.remote.api.CurrencyExchangeRatesApi
import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.CurrencyExchangeRateResponse
import com.omouravictor.invest_view.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class AssetsApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val alphaVantageApi: AlphaVantageApi,
    private val currencyExchangeRatesApi: CurrencyExchangeRatesApi
) : AssetsApiRepository {

    override suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageApi.getAssetsBySearch(keywords)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetsBySearch", "Keywords: $keywords", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageApi.getAssetGlobalQuote(symbol)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetGlobalQuote", "Symbol: $symbol", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getCurrencyExchangeRate(
        fromCurrency: String,
        toCurrency: String,
        amount: Int
    ): Result<CurrencyExchangeRateResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = currencyExchangeRatesApi.getCurrencyExchange(fromCurrency, toCurrency, amount)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetCurrencyExchangeRate", "From: $fromCurrency, To: $toCurrency", e)
                Result.failure(e)
            }
        }
    }

}