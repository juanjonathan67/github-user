package com.dicoding.githubuserapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuserapp.data.Result
import com.dicoding.githubuserapp.data.UserRepository
import com.dicoding.githubuserapp.data.local.entity.UserEntity

class FavoritesViewModel(private val userRepository: UserRepository) : ViewModel() {
    val userFavorites: LiveData<Result<List<UserEntity>>> = getFavoriteUser()

    private fun getFavoriteUser() = userRepository.getFavoriteUser()

    fun setFavoriteUser(userEntity: UserEntity, favoriteState: Boolean) = userRepository.setFavoriteUser(userEntity, favoriteState)
}