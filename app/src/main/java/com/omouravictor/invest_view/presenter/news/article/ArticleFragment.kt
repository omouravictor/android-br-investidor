package com.omouravictor.invest_view.presenter.news.article

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentArticleBinding
import com.omouravictor.invest_view.presenter.model.ArticleUiModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var articleUiModel: ArticleUiModel
    private val args by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleUiModel = args.articleUiModel
        binding = FragmentArticleBinding.bind(view)

        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(articleUiModel.url)
        }

    }

}