package com.mjimenez.bloggy.ui.usecases.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.mjimenez.bloggy.R
import com.mjimenez.bloggy.databinding.ActivityHomeBinding
import com.mjimenez.bloggy.service.RetrofitInstance
import com.mjimenez.bloggy.service.model.Post
import com.mjimenez.bloggy.ui.components.OnInteractionListener
import com.mjimenez.bloggy.ui.components.PaginationScrollListener
import com.mjimenez.bloggy.ui.components.blogview.BlogItemViewAdapter
import com.mjimenez.bloggy.ui.usecases.createpost.CreatePostActivity
import com.mjimenez.bloggy.ui.usecases.post.PostActivity
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity(), OnInteractionListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityHomeBinding
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var query: String? = null

    // To prevent losses
    private var fromCollapse = false
    private var isNoSearching = true

    private val itemsPerPage = 10
    private var currentPage = 0
    private var isLastPage = false
    private var items = listOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fabText.shrink()
        binding.fabText.setOnClickListener {
            if (binding.fabText.isExtended) {
                // Navigate to new post
                binding.fabText.shrink()

                startActivity(
                    Intent(applicationContext, CreatePostActivity::class.java)
                )
                finish()
            } else {
                binding.fabText.extend()
            }
        }

        setUpList()
    }

    private fun setUpList() {
        binding.refresh.setOnRefreshListener(this@HomeActivity)

        val adapter = BlogItemViewAdapter(this@HomeActivity)
        binding.recycler.adapter = adapter

        scope.launch {
            searchPosts()
//            setPostsInitialState()
        }

        val itemDecorator =
            DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(
            ContextCompat.getDrawable(
                applicationContext, R.drawable.divider
            )!!
        )

        binding.recycler.addItemDecoration(itemDecorator)

        binding.recycler.addOnScrollListener(object :
            PaginationScrollListener(binding.recycler.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                scope.launch {
                    loadMorePosts()
                }
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = binding.refresh.isRefreshing
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSearchIntent(intent)
    }

    private fun handleSearchIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { newQuery ->
                query = newQuery
                // Find data
                scope.launch {
                    searchPosts()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val menuItem = menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView

        searchView.setSearchableInfo(
            sm.getSearchableInfo(componentName)
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    query = newText
                    scope.launch {
                        searchPosts()
                    }
                } else if (!fromCollapse) {
                    query = ""
                    scope.launch {
                        searchPosts()
                    }
                }

                fromCollapse = false
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val view: View? = findViewById(android.R.id.content)
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }

                return true
            }
        })

        menuItem.setOnActionExpandListener(object :
            MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                query = ""
                scope.launch {
                    searchPosts()
                }
                isNoSearching = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                query = null
                scope.launch {
                    searchPosts()
                }
                fromCollapse = true
                isNoSearching = true
                return true
            }
        })
        return true
    }

    private suspend fun searchPosts() {
        isLastPage = false
        currentPage = 0

        binding.refresh.isRefreshing = true
        // Fetch data
        val searchResults = RetrofitInstance.api.getPosts(query, currentPage, itemsPerPage)
        binding.refresh.isRefreshing = false
        if (searchResults.body() != null) {
            items = searchResults.body()!!.toList()
            (binding.recycler.adapter as BlogItemViewAdapter).submitList(searchResults.body())
        }
    }

    private suspend fun loadMorePosts() {
        currentPage += 1
        binding.refresh.isRefreshing = true
        // Fetch data
        val searchResults = RetrofitInstance.api.getPosts(query, currentPage, itemsPerPage)
        binding.refresh.isRefreshing = false
        val newPosts = searchResults.body()

        if (newPosts != null) {
            if (newPosts.isEmpty()) {
                isLastPage = true
                return
            }
            items = items.plus(newPosts.toList())
            (binding.recycler.adapter as BlogItemViewAdapter).submitList(items)
        }
    }

    override fun onClick(post: Post) {
        startActivity(
            Intent(
                this@HomeActivity, PostActivity::class.java
            ).apply {
                putExtra(PostActivity.POST, Gson().toJson(post))
            }
        )
    }

    override fun onRefresh() {
        scope.launch {
            searchPosts()
        }
    }
}