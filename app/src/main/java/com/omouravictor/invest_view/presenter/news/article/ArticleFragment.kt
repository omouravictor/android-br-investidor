package com.omouravictor.invest_view.presenter.news.article

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.FragmentArticleBinding
import com.omouravictor.invest_view.presenter.model.ArticleUiModel
import com.omouravictor.invest_view.util.setupToolbarTitle

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var articleUiModel: ArticleUiModel
    private val args by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleUiModel = args.articleUiModel
        binding = FragmentArticleBinding.bind(view)

        requireActivity().setupToolbarTitle(articleUiModel.source.name ?: articleUiModel.url)

        val ivBack = binding.ivBack
        val ivForward = binding.ivForward
        val progressBar = binding.incProgressBar.progressBar
        val webView = binding.webView

        webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.isVisible = false
                    ivBack.isVisible = true
                    ivBack.isEnabled = canGoBack()
                    ivForward.isVisible = true
                    ivForward.isEnabled = canGoForward()
                }
            }

            loadUrl(articleUiModel.url)
        }

        ivBack.setOnClickListener {
            webView.goBack()
        }

        ivForward.setOnClickListener {
            webView.goForward()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webView.destroy()
    }

}