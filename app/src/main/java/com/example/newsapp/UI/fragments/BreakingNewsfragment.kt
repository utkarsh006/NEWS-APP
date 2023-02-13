package com.example.newsapp.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.UI.MainActivity
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.UI.adapters.NewsAdapter
import com.example.newsapp.UI.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.UI.util.Resource
import kotlinx.android.synthetic.main.fragment_breakingnews.*

class BreakingNewsfragment: Fragment(R.layout.fragment_breakingnews) {

    private val viewModel by activityViewModels<NewsViewModel>()

    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsfragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setonItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsfragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
            responce ->
            when(responce){
                is Resource.Success -> {
                    hideProgressBar()
                    responce.data?.let {
                        newsResponce ->
                        newsAdapter.differ.submitList(newsResponce.articles.toList())
                        val totalPages = newsResponce.totalResults / QUERY_PAGE_SIZE + 2
                        isLastpage = viewModel.breakingNewsPage == totalPages
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
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
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
            val isTotalMoreThenVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLoading && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThenVisible && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("IN")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsfragment.scrollListener)
        }
    }
}