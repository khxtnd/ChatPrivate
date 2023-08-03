package com.chatprivate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chatprivate.R
import com.chatprivate.activity.ChatActivity
import com.chatprivate.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val context: Context,
    private val userList: ArrayList<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.tvUsernameUI.text = user.userName
        if (user.userImg == "") {
            holder.circleImgUI.setImageResource(R.drawable.logo_user)
        } else {
            Glide.with(context).load(user.userImg).into(holder.circleImgUI)
        }
        holder.linearUI.setOnClickListener {
            var intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", user.userId)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsernameUI: TextView = view.findViewById(R.id.tvUsernameUI)
        val circleImgUI: CircleImageView = view.findViewById(R.id.circleImgUI)
        val linearUI: LinearLayout = view.findViewById(R.id.linearUI)
    }
}