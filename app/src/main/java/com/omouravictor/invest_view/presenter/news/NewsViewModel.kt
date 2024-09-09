package com.omouravictor.invest_view.presenter.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omouravictor.invest_view.data.remote.model.news.toNewsUiModel
import com.omouravictor.invest_view.data.remote.repository.NewsApiRepository
import com.omouravictor.invest_view.presenter.model.ArticleUiModel
import com.omouravictor.invest_view.presenter.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsApiRepository: NewsApiRepository
) : ViewModel() {

    private val _getNewsListUiState = MutableStateFlow<UiState<List<ArticleUiModel>>>(UiState.Initial)
    val getNewsListUiState = _getNewsListUiState.asStateFlow()

    init {
        loadNewsBySearch("Bolsa de valores")
    }

    fun loadNewsBySearch(keywords: String) {
        _getNewsListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = newsApiRepository.getNewsBySearch(keywords)
                if (result.isSuccess) {
                    val newsBySearchList = result.getOrThrow()
                        .toNewsUiModel()
                        .filter { it.urlToImage != null }
                    _getNewsListUiState.value = UiState.Success(newsBySearchList)
                } else
                    _getNewsListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _getNewsListUiState.value = UiState.Error(e)
            }
        }
    }

}