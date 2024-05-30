package com.dicoding.githubuserapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.dicoding.githubuserapp.data.Result
import com.dicoding.githubuserapp.data.UserRepository
import com.dicoding.githubuserapp.data.local.entity.UserEntity

class UserDetailViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val username = MutableLiveData<String>()
    private val usernameFollowers = MutableLiveData<String>()
    private val usernameFollowing = MutableLiveData<String>()
    private val usernameDetailsFollowers = MutableLiveData<String>()
    private val usernameDetailsFollowing = MutableLiveData<String>()
    val userDetails: LiveData<Result<List<UserEntity>>> = username.switchMap { getUserDetailsFromRepo() }
    val userFollowers: LiveData<Result<List<UserEntity>>> = usernameFollowers.switchMap { getUserFollowersFromRepo() }
    val userFollowing: LiveData<Result<List<UserEntity>>> = usernameFollowing.switchMap { getUserFollowingFromRepo() }
    val userDetailsFollowers: LiveData<Result<List<UserEntity>>> = usernameDetailsFollowers.switchMap { getUserFollowersDetailsFromRepo() }
    val userDetailsFollowing: LiveData<Result<List<UserEntity>>> = usernameDetailsFollowing.switchMap { getUserFollowingDetailsFromRepo() }

    companion object{
        private const val TAG = "UserDetailViewModel"
    }

    private fun getUserDetailsFromRepo() = userRepository.getUserDetails( username.value ?: "" )
    fun getUserDetails(q : String) = apply { username.value = q }

    private fun getUserFollowersFromRepo() = userRepository.getUserFollowers( usernameFollowers.value ?: "")
    fun getUserFollowers(q: String) = apply { usernameFollowers.value = q }

    private fun getUserFollowingFromRepo() = userRepository.getUserFollowing( usernameFollowing.value ?: "")
    fun getUserFollowing(q: String) = apply { usernameFollowing.value = q }

    private fun getUserFollowersDetailsFromRepo() = userRepository.getUserFollowerDetails( usernameDetailsFollowers.value ?: "")
    fun getUserFollowerDetails(q: String) = apply { usernameDetailsFollowers.value = q }

    private fun getUserFollowingDetailsFromRepo() = userRepository.getUserFollowingDetails( usernameDetailsFollowing.value ?: "")
    fun getUserFollowingDetails(q: String) = apply { usernameDetailsFollowing.value = q }

    fun setFavoriteUser(userEntity: UserEntity, favoriteState: Boolean) = userRepository.setFavoriteUser(userEntity, favoriteState)

}