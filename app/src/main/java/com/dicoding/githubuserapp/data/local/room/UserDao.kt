package com.dicoding.githubuserapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.githubuserapp.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUser(): List<UserEntity>

    @Query("SELECT * FROM users WHERE isFavorite = 1")
    fun getFavoriteUser(): List<UserEntity>

    @Query("SELECT * FROM users WHERE login LIKE '%' || :login || '%'")
    fun getUserByLogin(login: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(vararg users: UserEntity)

    @Update
    fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isFavorite = :isFavorite WHERE login = :login")
    fun setUserFavorite(login: String, isFavorite: Boolean)

    @Query("DELETE FROM users WHERE isFavorite = 0")
    fun deleteAll()

    @Query("DELETE FROM users WHERE login = :login")
    fun deleteByLogin(login: String)

    @Query("SELECT EXISTS(SELECT * FROM users where login = :login AND isFavorite = 1)")
    fun isUserFavorite(login: String): Boolean
}
