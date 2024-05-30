package com.dicoding.githubuserapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class UserEntity (
    @field:ColumnInfo(name = "id")
    @PrimaryKey
    var id: Int? = 0,

    @ColumnInfo(name = "login")
    var login: String? = "",

    @ColumnInfo(name = "name")
    var name: String? = "",

    @ColumnInfo(name = "avatarUrl")
    var avatarUrl: String? = "",

    @ColumnInfo(name= "followers")
    var followers: Int? = 0,

    @ColumnInfo(name= "following")
    var following: Int? = 0,

    @ColumnInfo(name = "repos")
    var publicRepos: Int? = 0,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean? = false,
) : Parcelable
