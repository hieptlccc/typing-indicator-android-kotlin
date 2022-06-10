package com.example.typingindicator.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cometchat.pro.models.User
import com.example.typingindicator.R
import com.example.typingindicator.activity.ChatActivity

class UsersAdapter(
    private val context: Context,
    private val users: List<User>,
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        Glide.with(context)
            .load(user.avatar)
            .circleCrop()
            .into(holder.userAvatarIv);
        holder.userNameTxt.text = user.name
        holder.userLl.setOnClickListener(View.OnClickListener {
            goToChatActivity(user)
        })
    }

    private fun goToChatActivity(user: User?) {
        val intent = Intent(this.context, ChatActivity::class.java)
        intent.putExtra("uid", user?.uid)
        this.context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userAvatarIv: ImageView = itemView.findViewById(R.id.userAvatarIv)
        val userNameTxt: TextView = itemView.findViewById(R.id.userNameTxt)
        val userLl: LinearLayout = itemView.findViewById(R.id.userLl)
    }
}