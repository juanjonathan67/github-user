package com.dicoding.githubuserapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapp.R
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.databinding.ActivityMainBinding
import com.dicoding.githubuserapp.data.Result

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchResult: String
    private val mainViewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.dataStore)
        mainViewModel.pref = pref

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.swTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.swTheme.isChecked = false
            }
        }


        binding.swTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager

        val listUserAdapter = ListUserAdapter {user ->
            if (user.isFavorite == true) {
                mainViewModel.setFavoriteUser(user, false)
            } else {
                mainViewModel.setFavoriteUser(user, true)
            }
        }
        listUserAdapter.submitList(mutableListOf())
        binding.rvMain.adapter = listUserAdapter

        supportActionBar?.hide()
        binding.topAppBar.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainActivityIntent)
                    true
                }
                R.id.menu_favorite -> {
                    val favoriteActivityIntent = Intent(this, FavoritesActivity::class.java)
                    startActivity(favoriteActivityIntent)
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                binding.searchView.hide()
                searchResult = binding.searchView.text.toString()
                mainViewModel.getUser(searchResult)
                false
            }

        mainViewModel.user.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        for (user in result.data) {
                            user.login?.let { mainViewModel.getUserDetails(it) }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "User Search Error", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivitySearchResult", result.error)
                    }
                }
            }
        }

        mainViewModel.userDetails.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setUserData(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "User Search Error", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivitySearchResult", result.error)
                    }
                }
            }
        }
    }

    private fun setUserData(listUser: List<UserEntity>) {
        val listUserAdapter = ListUserAdapter {user ->
            if (user.isFavorite == true) {
                mainViewModel.setFavoriteUser(user, false)
            } else {
                mainViewModel.setFavoriteUser(user, true)
            }
        }
        listUserAdapter.submitList(listUser)
        binding.rvMain.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserEntity) {
                showUserDetails(data)
            }
        })

    }

    private fun showUserDetails(data: UserEntity) {
        val userDetailIntent = Intent(this@MainActivity, UserDetailActivity::class.java)
        userDetailIntent.putExtra(UserDetailActivity.EXTRA_USER, data)
        startActivity(userDetailIntent)
    }
}