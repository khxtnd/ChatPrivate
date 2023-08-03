package com.chatprivate.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chatprivate.R
import com.chatprivate.adapter.ChatAdapter
import com.chatprivate.databinding.ActivityChatBinding
import com.chatprivate.model.Chat
import com.chatprivate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var intent = intent
        var userId = intent.getStringExtra("userId")

        binding.imgBackCA.setOnClickListener {
            onBackPressed()
        }

        binding.recycleViewCA.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.ibSendMessage.setOnClickListener {
            var message: String = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(firebaseUser!!.uid, userId.toString(), message)
                binding.etMessage.setText("")
            }
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)



        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.tvUsernameCA.text = user!!.userName
                if (user.userImg == "") {
                    binding.circleImgCA.setImageResource(R.drawable.logo_user)
                } else {
                    Glide.with(this@ChatActivity).load(user.userImg).into(binding.circleImgCA)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        readMess(firebaseUser!!.uid, userId.toString())
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().reference

        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message

        reference!!.child("Chat").push().setValue(hashMap)
    }

    private fun readMess(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if ((chat!!.senderId == senderId && chat!!.receiverId == receiverId)
                        || (chat!!.senderId == receiverId && chat!!.receiverId == senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                var adapter = ChatAdapter(this@ChatActivity, chatList)
                binding.recycleViewCA.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}