package com.dicoding.githubuserapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuserapp.data.local.entity.UserEntity
import com.dicoding.githubuserapp.databinding.ItemRowUserBinding

class FollowAdapter (private val onFavoriteClick: (UserEntity) -> Unit) : ListAdapter<UserEntity, FollowAdapter.FollowViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(getItem(holder.adapterPosition))
        }

        val btFavorite = holder.binding.btFavorite
        btFavorite.isActivated = (user.isFavorite == true)
        btFavorite.setOnClickListener {
            onFavoriteClick(user)
            btFavorite.isActivated = !btFavorite.isActivated
        }
    }
    class FollowViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserEntity){
            binding.tvItemFullname.text = user.name
            binding.tvItemUsername.text = user.login
            binding.tvItemRepositories.text = user.publicRepos.toString() + " repositories"
            Glide.with(binding.root.context)
                .load(user.avatarUrl)
                .into(binding.imgItemPhoto)
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserEntity>() {
            override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserEntity)
    }
}