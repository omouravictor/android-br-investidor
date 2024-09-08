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

    private val _newsListUiState = MutableStateFlow<UiState<List<ArticleUiModel>>>(UiState.Initial)
    val newsListUiState = _newsListUiState.asStateFlow()

    init {
        loadNewsBySearch("Bolsa de valores")
    }

    fun loadNewsBySearch(keywords: String) {
        _newsListUiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val result = newsApiRepository.getNewsBySearch(keywords)
                if (result.isSuccess) {
                    val newsBySearchList = result.getOrThrow().toNewsUiModel()
                    _newsListUiState.value = UiState.Success(newsBySearchList)
                } else
                    _newsListUiState.value = UiState.Error(result.exceptionOrNull() as Exception)
            } catch (e: Exception) {
                _newsListUiState.value = UiState.Error(e)
            }
        }
    }

}