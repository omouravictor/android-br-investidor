package com.omouravictor.wise_invest.presenter.news

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.wise_invest.R
import com.omouravictor.wise_invest.data.remote.model.news.toNewsUiModel
import com.omouravictor.wise_invest.data.remote.repository.NewsApiRepository
import com.omouravictor.wise_invest.presenter.model.UiState
import com.omouravictor.wise_invest.presenter.news.article.ArticleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val newsApiRepository: NewsApiRepository
) : ViewModel() {

    private val _getNewsListUiState = MutableStateFlow<UiState<List<ArticleUiModel>>>(UiState.Initial)
    val getNewsListUiState = _getNewsListUiState.asStateFlow()

    init {
        getNewsList(context.getString(R.string.defaultNewsSearch))
    }

    fun getNewsList(keywords: String) {
        _getNewsListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val newsBySearchList = newsApiRepository.getNewsBySearch(keywords).getOrThrow()
                    .toNewsUiModel()
                    .filter { it.urlToImage != null && it.title != null }

                _getNewsListUiState.value = UiState.Success(newsBySearchList)
            } catch (e: Exception) {
                _getNewsListUiState.value = UiState.Error(e)
            }
        }
    }

}