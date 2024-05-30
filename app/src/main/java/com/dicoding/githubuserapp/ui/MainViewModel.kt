package com.dicoding.githubuserapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.dicoding.githubuserapp.data.UserRepository
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.data.Result
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val query = MutableLiveData<String>()
    private val username = MutableLiveData<String>()
    val user: LiveData<Result<List<UserEntity>>> = query.switchMap { getUserFromRepo() }
    val userDetails: LiveData<Result<List<UserEntity>>> = username.switchMap { getUserDetailsFromRepo() }
    lateinit var pref: SettingPreferences

    init {
        query.value = "arif"
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    private fun getUserFromRepo() = userRepository.getUser(query.value ?: "")

    fun getUser(q: String) = apply { query.value = q }

    private fun getUserDetailsFromRepo() = userRepository.getUserDetails( username.value ?: "" )

    fun getUserDetails(q : String) = apply { username.value = q }

    fun setFavoriteUser(userEntity: UserEntity, favoriteState: Boolean) = userRepository.setFavoriteUser(userEntity, favoriteState)

}