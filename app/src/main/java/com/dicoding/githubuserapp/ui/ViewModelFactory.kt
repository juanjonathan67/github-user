package com.dicoding.githubuserapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.githubuserapp.data.UserRepository
import com.dicoding.githubuserapp.di.Injection

class ViewModelFactory private constructor(private val userRepository: UserRepository)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
            MainViewModel::class.java -> MainViewModel(userRepository)
            UserDetailViewModel::class.java -> UserDetailViewModel(userRepository)
            FavoritesViewModel::class.java -> FavoritesViewModel(userRepository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        } as T

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }

}