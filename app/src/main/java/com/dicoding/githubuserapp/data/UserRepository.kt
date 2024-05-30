package com.dicoding.githubuserapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.data.local.room.UserDao
import com.dicoding.githubuserapp.data.remote.response.FollowResponseItem
import com.dicoding.githubuserapp.data.remote.response.UserDetailsResponse
import com.dicoding.githubuserapp.data.remote.response.UserResponse
import com.dicoding.githubuserapp.data.remote.retrofit.ApiService
import com.dicoding.githubuserapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val appExecutors: AppExecutors
) {
    val userResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userDetailsResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userFollowersResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userFollowingResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userFollowersDetailsResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userFollowingDetailsResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }
    val userFavoriteResult: MutableLiveData<Result<List<UserEntity>>> by lazy { MutableLiveData<Result<List<UserEntity>>>() }

    private var listTemp = ArrayList<UserEntity>()
    private var currentSearchQuery = ""
    private var currentUserQuery = ""

    fun getUser(query: String) : LiveData<Result<List<UserEntity>>> {
        currentSearchQuery = query
        userResult.postValue(Result.Loading)
        val client = apiService.getUsers(query)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    listTemp.clear()
                    val users = response.body()?.items
                    val userList = ArrayList<UserEntity>()
                    appExecutors.diskIO.execute {
                        users?.forEach {user ->
                            val isFavorite = userDao.isUserFavorite(user.login)
                            val userEntity = UserEntity(
                                id = user.id,
                                login = user.login,
                                isFavorite = isFavorite
                            )
                            userList.add(userEntity)
                        }
                        userDao.deleteAll()
                        userDao.insertUser(*userList.toTypedArray())

                        val localData = userDao.getUserByLogin(query)
                        userResult.postValue(Result.Success(localData))
                    }
                } else {
                    userResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                userResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userResult
    }

    fun getUserDetails(query: String) : LiveData<Result<List<UserEntity>>> {
        userDetailsResult.postValue(Result.Loading)
        val client = apiService.getUserDetails(query)
        client.enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(
                call: Call<UserDetailsResponse>,
                response: Response<UserDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val userDetails = response.body()
                    appExecutors.diskIO.execute {
                        val isFavorite = userDetails?.let { userDao.isUserFavorite(it.login) }
                        val userEntity = UserEntity(
                            id = userDetails?.id,
                            login = userDetails?.login,
                            name = userDetails?.name,
                            avatarUrl = userDetails?.avatarUrl,
                            followers = userDetails?.followers,
                            following = userDetails?.following,
                            publicRepos = userDetails?.publicRepos,
                            isFavorite = isFavorite
                        )
                        userDao.updateUser(userEntity)

                        listTemp.add(userEntity)
                        userDetailsResult.postValue(Result.Success(listTemp.toList()))
                    }
                } else {
                    userDetailsResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                userDetailsResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userDetailsResult
    }

    fun getUserFollowers(query: String) : LiveData<Result<List<UserEntity>>> {
        currentUserQuery = query
        userFollowersResult.postValue(Result.Loading)
        val client = apiService.getUserFollowers(query)
        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                if (response.isSuccessful) {
                    listTemp.clear()
                    val followers = response.body()
                    val followerList = ArrayList<UserEntity>()
                    appExecutors.diskIO.execute {
                        followers?.forEach {user ->
                            val isFavorite = userDao.isUserFavorite(user.login)
                            val userEntity = UserEntity(
                                id = user.id,
                                login = user.login,
                                isFavorite = isFavorite
                            )
                            followerList.add(userEntity)
                        }
                        userDao.deleteAll()
                        userDao.insertUser(*followerList.toTypedArray())

                        userFollowersResult.postValue(Result.Success(followerList.toList()))
                    }
                } else {
                    userFollowersResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                userFollowersResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userFollowersResult
    }

    fun getUserFollowing(query: String) : LiveData<Result<List<UserEntity>>> {
        currentUserQuery = query
        userFollowingResult.postValue(Result.Loading)
        val client = apiService.getUserFollowing(query)
        client.enqueue(object : Callback<List<FollowResponseItem>> {
            override fun onResponse(
                call: Call<List<FollowResponseItem>>,
                response: Response<List<FollowResponseItem>>
            ) {
                if (response.isSuccessful) {
                    listTemp.clear()
                    val following = response.body()
                    val followingList = ArrayList<UserEntity>()
                    appExecutors.diskIO.execute {
                        following?.forEach { user ->
                            val isFavorite = userDao.isUserFavorite(user.login)
                            val userEntity = UserEntity(
                                id = user.id,
                                login = user.login,
                                isFavorite = isFavorite
                            )
                            followingList.add(userEntity)
                        }
                        userDao.deleteAll()
                        userDao.insertUser(*followingList.toTypedArray())

                        userFollowingResult.postValue(Result.Success(followingList.toList()))
                    }
                } else {
                    userFollowingResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<List<FollowResponseItem>>, t: Throwable) {
                userFollowingResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userFollowingResult
    }

    fun getUserFollowerDetails(query: String) : LiveData<Result<List<UserEntity>>>{
        userFollowersDetailsResult.postValue(Result.Loading)
        val client = apiService.getUserDetails(query)
        client.enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(
                call: Call<UserDetailsResponse>,
                response: Response<UserDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val userFollowerDetails = response.body()
                    appExecutors.diskIO.execute {
                        val isFavorite = userFollowerDetails?.let { userDao.isUserFavorite(it.login) }
                        val userEntity = UserEntity(
                            id = userFollowerDetails?.id,
                            login = userFollowerDetails?.login,
                            name = userFollowerDetails?.name,
                            avatarUrl = userFollowerDetails?.avatarUrl,
                            followers = userFollowerDetails?.followers,
                            following = userFollowerDetails?.following,
                            publicRepos = userFollowerDetails?.publicRepos,
                            isFavorite = isFavorite
                        )
                        userDao.updateUser(userEntity)

                        listTemp.add(userEntity)
                        userFollowersDetailsResult.postValue(Result.Success(listTemp.toList()))
                    }
                } else {
                    userFollowersDetailsResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                userFollowersDetailsResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userFollowersDetailsResult
    }

    fun getUserFollowingDetails(username: String) : LiveData<Result<List<UserEntity>>>{
        userFollowingDetailsResult.postValue(Result.Loading)
        val client = apiService.getUserDetails(username)
        client.enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(
                call: Call<UserDetailsResponse>,
                response: Response<UserDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val userFollowerDetails = response.body()
                    appExecutors.diskIO.execute {
                        val isFavorite = userFollowerDetails?.let { userDao.isUserFavorite(it.login) }
                        val userEntity = UserEntity(
                            id = userFollowerDetails?.id,
                            login = userFollowerDetails?.login,
                            name = userFollowerDetails?.name,
                            avatarUrl = userFollowerDetails?.avatarUrl,
                            followers = userFollowerDetails?.followers,
                            following = userFollowerDetails?.following,
                            publicRepos = userFollowerDetails?.publicRepos,
                            isFavorite = isFavorite
                        )
                        userDao.updateUser(userEntity)

                        listTemp.add(userEntity)
                        userFollowingDetailsResult.postValue(Result.Success(listTemp.toList()))
                    }
                } else {
                    userFollowingDetailsResult.postValue(Result.Error(response.message()))
                }
            }
            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                userFollowingDetailsResult.postValue(Result.Error(t.message.toString()))
            }
        })
        return userFollowingDetailsResult
    }


    fun getFavoriteUser() : LiveData<Result<List<UserEntity>>> {
        appExecutors.diskIO.execute {
            val localData = userDao.getFavoriteUser()
            userFavoriteResult.postValue(Result.Success(localData))
        }
        return userFavoriteResult
    }

    fun setFavoriteUser(user: UserEntity, favoriteState: Boolean)  {
        appExecutors.diskIO.execute {
            user.isFavorite = favoriteState
            userDao.updateUser(user)

            val localData = userDao.getFavoriteUser()
            userFavoriteResult.postValue(Result.Success(localData))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userDao: UserDao,
            appExecutors: AppExecutors
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userDao, appExecutors)
            }.also { instance = it }
    }

}