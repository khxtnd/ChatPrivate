package com.chatprivate.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chatprivate.R
import com.chatprivate.adapter.UserAdapter
import com.chatprivate.databinding.ActivityMainBinding
import com.chatprivate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var userList = arrayListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recycleViewMA.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getUsersList()

        binding.circleImgMA.setOnClickListener {
            var intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.tvUserNameMA.text = user!!.userName
                if (user.userImg == "") {
                    binding.circleImgMA.setImageResource(R.drawable.logo_user)
                } else {
                    Glide.with(this@MainActivity).load(user.userImg).into(binding.circleImgMA)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                val currentUser = snapshot.getValue(User::class.java)

                if (currentUser!!.userImg == "") {
                    binding.circleImgMA.setImageResource(R.drawable.logo_user)
                } else {
                    Glide.with(this@MainActivity).load(currentUser.userImg)
                        .into(binding.circleImgMA)
                }
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    if (user!!.userId != firebase.uid && user!!.userId != "") {
                        userList.add(user)
                    }
                }

                var adapter = UserAdapter(this@MainActivity, userList)
                binding.recycleViewMA.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}