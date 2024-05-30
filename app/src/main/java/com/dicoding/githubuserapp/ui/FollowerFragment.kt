package com.dicoding.githubuserapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuserapp.data.Result
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.databinding.FragmentFollowerBinding

class FollowerFragment : Fragment() {
    private lateinit var binding: FragmentFollowerBinding
    private val userDetailViewModel by viewModels<UserDetailViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollower.layoutManager = layoutManager

        userDetailViewModel.userFollowers.observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        for (user in result.data) {
                            user.login?.let { userDetailViewModel.getUserFollowerDetails(it) }
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), "User Search Error", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivitySearchResult", result.error)
                    }
                }
            }
        }

        userDetailViewModel.userDetailsFollowers.observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setUserFollowers(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), "User Search Error", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivitySearchResult", result.error)
                    }
                }
            }
        }

        val userParcelable = if (Build.VERSION.SDK_INT >= 33) {
            requireActivity().intent.getParcelableExtra(UserDetailActivity.EXTRA_USER, UserEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().intent.getParcelableExtra(UserDetailActivity.EXTRA_USER)
        }

        if (userParcelable != null) {
            userDetailViewModel.getUserFollowers(userParcelable.login ?: "")
        }
    }

    private fun setUserFollowers(userFollowers: List<UserEntity>) {
        val followerAdapter = FollowAdapter {user ->
            if (user.isFavorite == true) {
                userDetailViewModel.setFavoriteUser(user, false)
            } else {
                userDetailViewModel.setFavoriteUser(user, true)
            }
        }
        followerAdapter.submitList(userFollowers)
        binding.rvFollower.adapter = followerAdapter

        followerAdapter.setOnItemClickCallback(object : FollowAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserEntity) {
                showUserDetails(data)
            }
        })
    }

    private fun showUserDetails(data: UserEntity) {
        val userDetailIntent = Intent(requireActivity(), UserDetailActivity::class.java)
        userDetailIntent.putExtra(UserDetailActivity.EXTRA_USER, data)
        startActivity(userDetailIntent)
    }

}