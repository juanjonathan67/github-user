package com.dicoding.githubuserapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapp.R
import com.dicoding.githubuserapp.data.Result
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private val favoritesViewModel by viewModels<FavoritesViewModel> { ViewModelFactory.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.layoutManager = layoutManager


        val listUserAdapter = ListUserAdapter {user ->
            if (user.isFavorite == true) {
                favoritesViewModel.setFavoriteUser(user, false)
            } else {
                favoritesViewModel.setFavoriteUser(user, true)
            }
        }
        listUserAdapter.submitList(mutableListOf())
        binding.rvFavorites.adapter = listUserAdapter


        supportActionBar?.hide()

        binding.topAppBar.setNavigationOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainActivityIntent)
            finish()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
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
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }

        favoritesViewModel.userFavorites.observe(this) {result ->
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
                favoritesViewModel.setFavoriteUser(user, false)
            } else {
                favoritesViewModel.setFavoriteUser(user, true)
            }
        }
        listUserAdapter.submitList(listUser)
        binding.rvFavorites.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserEntity) {
                showUserDetails(data)
            }
        })

    }


    private fun showUserDetails(data: UserEntity) {
        val userDetailIntent = Intent(this@FavoritesActivity, UserDetailActivity::class.java)
        userDetailIntent.putExtra(UserDetailActivity.EXTRA_USER, data)
        startActivity(userDetailIntent)
    }
}