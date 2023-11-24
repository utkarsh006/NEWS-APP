package com.example.newsapp.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var activityBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NewsApp)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(activityBinding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsnavfragment) as NavHostFragment
        val navController = navHostFragment.navController

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        activityBinding.bottomnavigation.setupWithNavController(navController)
    }
}
