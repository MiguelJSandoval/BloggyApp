package com.mjimenez.bloggy.ui.components.blogview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mjimenez.bloggy.R
import com.mjimenez.bloggy.service.model.Post
import com.mjimenez.bloggy.ui.components.OnInteractionListener

class BlogItemViewAdapter(
    private val listener: OnInteractionListener
) :
    ListAdapter<Post, BlogItemViewAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_blog_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, listener)
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val parent: ConstraintLayout = view.findViewById(R.id.item_parent)
        private val title: TextView = view.findViewById(R.id.title)
        private val author: TextView = view.findViewById(R.id.author)
        private val date: TextView = view.findViewById(R.id.date)
        private val content: TextView = view.findViewById(R.id.content)

        fun bind(post: Post, listener: OnInteractionListener) {
            title.text = post.title
            author.text = post.author
            date.text = post.date
            content.text =
                if (post.content.length > 70) post.content.substring(0, 70) else post.content

            parent.setOnClickListener {
                listener.onClick(post)
            }
        }
    }
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.postId == newItem.postId
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
