package com.omouravictor.invest_view.presenter.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.omouravictor.invest_view.databinding.ItemNewsBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.model.ArticleUiModel
import com.omouravictor.invest_view.util.LocaleUtil

class NewsAdapter : BaseRecyclerViewAdapter<ArticleUiModel, NewsAdapter.NewsBySearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsBySearchViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsBySearchViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class NewsBySearchViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(articleUiModel: ArticleUiModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(articleUiModel.urlToImage)
                    .into(articleImage)
                articleSource.text = articleUiModel.source.name
                articleTitle.text = articleUiModel.title
                articleDescription.text = articleUiModel.description
                articleDateTime.text = LocaleUtil.getFormattedDateTime(articleUiModel.publishedAt)
                itemView.setOnClickListener { onClickItem(articleUiModel) }
            }
        }
    }
}
