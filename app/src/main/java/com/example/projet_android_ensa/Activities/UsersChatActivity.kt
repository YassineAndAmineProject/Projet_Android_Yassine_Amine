package com.example.projet_android_ensa.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android_ensa.Models.User
import com.example.projet_android_ensa.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class UsersChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var rvUsers: RecyclerView
    lateinit var editSearch : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_chat)
        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        rvUsers = findViewById(R.id.rvUsers)
        editSearch = findViewById(R.id.editSearch)

        val usersRecycleAdapter =UsersRecycleAdapter()
        rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersChatActivity)
            adapter = usersRecycleAdapter
        }

        val users = mutableListOf<User>()

        db.collection("users")
            .whereNotEqualTo("email",currentUser?.email)
            .get().addOnSuccessListener { result->
                for(document in result)
                {
                    val uuid = document.id
                    val email = document.getString("email")
                    val fullName = document.getString("fullname")
                    users.add(User(uuid,email ?: "",fullName ?: "",null))
                }
            usersRecycleAdapter.items = users
        }.addOnFailureListener{exception ->
            Log.e("UsersSearchActivity","error getting users",exception)
        }
        editSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usersRecycleAdapter.filter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}