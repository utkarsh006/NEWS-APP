package com.example.newsapp.UI.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.UI.NewsViewModel
import com.example.newsapp.UI.adapters.NewsAdapter
import com.example.newsapp.UI.utils.Constants.QUERY_PAGE_SIZE
import com.example.newsapp.UI.utils.PaginationHelper
import com.example.newsapp.UI.utils.ProgressBarManager.hideProgressBar
import com.example.newsapp.UI.utils.ProgressBarManager.showProgressBar
import com.example.newsapp.UI.utils.Resource
import com.example.newsapp.databinding.FragmentBreakingnewsBinding

class BreakingNewsFragment : Fragment(R.layout.fragment_breakingnews) {

    private val viewModel by activityViewModels<NewsViewModel>()

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentBreakingnewsBinding

    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreakingnewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        newsAdapter.setonItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsfragment_to_articleFragment,
                bundle
            )
        }

        listenUiState()
    }

    private fun listenUiState() {
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar(binding.paginationProgressBar) { isLoading = it }
                    response.data?.let { newsResponse ->
                        Log.d("#DEBUG", newsResponse.articles.toList().toString())
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar(binding.paginationProgressBar) { isLoading = it }
                    response.message?.let { message ->
                        Toast.makeText(activity, "Error occurred: $message", Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar(binding.paginationProgressBar) { isLoading = it }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

            addOnScrollListener(PaginationHelper(
                layoutManager = layoutManager as LinearLayoutManager,
                isLoading = { isLoading },
                isLastPage = { isLastPage },
                onLoadMore = { viewModel.getBreakingNews("us") }
            ))
        }
    }
}