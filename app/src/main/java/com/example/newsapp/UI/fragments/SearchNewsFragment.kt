package com.example.newsapp.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.UI.adapters.NewsAdapter
import com.example.newsapp.UI.util.Constants
import com.example.newsapp.UI.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newsapp.UI.util.Resource
import kotlinx.android.synthetic.main.fragment_breakingnews.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.*


class SearchNewsFragment : Fragment(R.layout.fragment_search) {

    val viewModel by activityViewModels<NewsViewModel>()

    lateinit var newsAdapter: NewsAdapter

    val TAG = "SearchNewsFragment"




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setonItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job:Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
                responce ->
            when(responce){
                is Resource.Success -> {
                    hideProgressBar()
                    responce.data?.let {
                            newsResponce ->
                        newsAdapter.differ.submitList(newsResponce.articles.toList())
                        val totalPages = newsResponce.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastpage = viewModel.searchNewsPage == totalPages
                        if (isLastpage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    responce.message?.let {
                            message ->
                        Toast.makeText(activity,"an Error occurred: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        prgnavigationbar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        prgnavigationbar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastpage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLoading = !isLoading && !isLastpage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThenVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLoading && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThenVisible && isScrolling

            if(shouldPaginate){
                viewModel.searchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}

