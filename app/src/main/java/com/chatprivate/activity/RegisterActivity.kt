package com.chatprivate.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chatprivate.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btRegisterRA.setOnClickListener {
            val userName = binding.etUsernameRA.text.toString()
            val email = binding.etEmailRA.text.toString()
            val password = binding.etPasswordRA.text.toString()

            if (userName.isNullOrEmpty() || email.isNullOrEmpty()
                || password.isNullOrEmpty()
            ) {
                Toast.makeText(applicationContext, "Không được để trống", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(userName, email, password)
            }
        }
        binding.btBackRA.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(userName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    var user: FirebaseUser? = firebaseAuth.currentUser
                    var userId: String = user!!.uid
                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["userImg"] = ""

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) { it ->
                        if (it.isSuccessful) {
                            finish()
                        }
                    }
                }
            }
    }
}