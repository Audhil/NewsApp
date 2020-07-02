package com.example.newsapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.ListItemBinding
import com.example.newsapp.util.CallBack

class FeedListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items by lazy {
        arrayListOf<Article>()
    }

    var clickListener: CallBack<Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        RepoViewHolder(
            //  list_item.xml
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder) {
            is RepoViewHolder ->
                holder.bindTo(position, items[position], clickListener)

            else ->
                Unit
        }

    fun addItems(newItems: List<Article>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition].publishedAt == newItems[newItemPosition].publishedAt

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = newItems.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                items[oldItemPosition] == newItems[newItemPosition]

        })
        items.run {
            clear()
            addAll(newItems)
        }
        diffResult.dispatchUpdatesTo(this)
    }

    private inner class RepoViewHolder(
        private val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            position: Int,
            articlee: Article,
            clickListener: CallBack<Int>?
        ) {
            binding.run {
                article = articlee
                itemCardView.setOnClickListener {
                    clickListener?.invoke(position)
                }
                executePendingBindings()
            }
        }
    }
}