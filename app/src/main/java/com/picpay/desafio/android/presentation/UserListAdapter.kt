package com.picpay.desafio.android.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.picpay.desafio.android.R
import com.picpay.desafio.android.databinding.ListItemUserBinding
import com.picpay.desafio.android.domain.User
import com.picpay.desafio.android.domain.UserListDiffCallback
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class UserListAdapter : RecyclerView.Adapter<UserListItemViewHolder>() {

    var users = emptyList<User>()
        set(value) {
            val result = DiffUtil.calculateDiff(
                UserListDiffCallback(
                    field,
                    value
                )
            )
            result.dispatchUpdatesTo(this)
            field = value
        }

    private var _binding: ListItemUserBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListItemViewHolder {
        _binding = ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = _binding!!.root

        return UserListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserListItemViewHolder, position: Int) {
        val user = users[position]
        _binding!!.name.text = user.name
        _binding!!.username.text = user.username
        _binding!!.progressBar.visibility = View.VISIBLE
        Picasso.get()
            .load(user.img)
            .error(R.drawable.ic_round_account_circle)
            .into(_binding!!.picture, object : Callback {
                override fun onSuccess() {
                    _binding!!.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    _binding!!.progressBar.visibility = View.GONE
                }
            })
    }

    override fun getItemCount(): Int = users.size
}