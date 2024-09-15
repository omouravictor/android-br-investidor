package com.omouravictor.invest_view.presenter.news.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.omouravictor.invest_view.databinding.ItemNewsBinding
import com.omouravictor.invest_view.presenter.base.BaseRecyclerViewAdapter
import com.omouravictor.invest_view.presenter.news.model.ArticleUiModel
import com.omouravictor.invest_view.util.LocaleUtil


class ArticleAdapter : BaseRecyclerViewAdapter<ArticleUiModel, ArticleAdapter.NewsBySearchViewHolder>() {

    private val glideRequestOptions = RequestOptions()
        .transform(CenterCrop(), RoundedCorners(16))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsBySearchViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsBySearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsBySearchViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    inner class NewsBySearchViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(articleUiModel: ArticleUiModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(articleUiModel.urlToImage)
                    .apply(glideRequestOptions)
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