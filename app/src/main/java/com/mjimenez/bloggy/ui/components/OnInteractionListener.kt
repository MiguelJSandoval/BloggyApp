package com.mjimenez.bloggy.ui.components

import com.mjimenez.bloggy.service.model.Post

interface OnInteractionListener {
    fun onClick(post: Post)
}
