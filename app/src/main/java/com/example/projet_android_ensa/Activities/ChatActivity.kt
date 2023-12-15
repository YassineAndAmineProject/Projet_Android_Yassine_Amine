package com.example.projet_android_ensa.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_android_ensa.Adapters.ChatRecyclerAdapter
import com.example.projet_android_ensa.Models.Friend
import com.example.projet_android_ensa.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.projet_android_ensa.Models.Message
import com.example.projet_android_ensa.Models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var fabSendMessage : FloatingActionButton
    lateinit var editMessage : EditText
    lateinit var rvChatList : RecyclerView
    lateinit var chatRecyclerAdapter:ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser


        fabSendMessage = findViewById(R.id.fabSendMessage)
        editMessage = findViewById(R.id.editMessage)
        rvChatList = findViewById(R.id.rvChatList)
        val userUuid = intent.getStringExtra("Friend")!!

        db.collection("users").document(userUuid)
        .get()
        .addOnSuccessListener{ result->
            if(result != null){
                var user = result.toObject(User::class.java)
                user?.let {
                    user.uuid = userUuid
                    setUserData(user)
                }
            }
        }.addOnFailureListener{
            Log.e("chatActivity","une erreur",it)
        }
    }
    private fun setUserData(user: User) {
        supportActionBar?.title = user.fullname
        chatRecyclerAdapter  = ChatRecyclerAdapter()
        //Message(sender = "Nadim", receiver = "Yassine", text = "Salut",123456789 ,isReceived = false)
        val messages = mutableListOf<Message>()
        rvChatList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatRecyclerAdapter
        }
        fabSendMessage.setOnClickListener{
            val message = editMessage.text.toString()
            if(message.isNotEmpty())
            {
                val message = Message(currentUser!!.uid, receiver = user.uuid,message, timeStamp = System.currentTimeMillis(), isReceived = false)
                editMessage.setText("")

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editMessage.windowToken,0)

                db.collection("messages").add(message).addOnSuccessListener {
                    rvChatList.scrollToPosition(messages.size-1)
                }.addOnFailureListener{
                    Log.e("ChatActivity","erreur fonctionnelle !!",it)
                }
                val friend = Friend("",user.fullname,message.text,
                    timeStamp = System.currentTimeMillis(),
                    image = user.image ?: "")
                db.collection("users")
                    .document(currentUser!!.uid)
                    .collection("friends")
                    .document(user.uuid)
                    .set(friend)
                    .addOnSuccessListener {
                        Log.d("ChatActivity","Ajouté à la base")
                    }.addOnFailureListener{
                        Log.e("ChatActivity","erreur fonctionnelle !!",it)
                    }

            }

        }
        val sentQuery=db.collection("messages")
            .whereEqualTo("sender",currentUser!!.uid)
            .whereEqualTo("receiver",user.uuid)
            .orderBy("timeStamp",Query.Direction.ASCENDING)

        val receivedQuery=db.collection("messages")
            .whereEqualTo("sender",user.uuid)//user.uuid
            .whereEqualTo("receiver",currentUser!!.uid)
            .orderBy("timeStamp",Query.Direction.ASCENDING)

        sentQuery.addSnapshotListener{snapshot,exception ->
            if(exception != null)
            {
                Log.e("ChatActivity","une erreur dans la récupération des messages",exception)
                return@addSnapshotListener
            }
            for(document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let{
                    message.isReceived = false
                    if(!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }
            if(messages.isNotEmpty()){
                chatRecyclerAdapter.items = messages.sortedBy { it.timeStamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }

        receivedQuery.addSnapshotListener{snapshot,exception ->
            if(exception != null)
            {
                Log.e("ChatActivity","une erreur dans la récupération des messages",exception)
                return@addSnapshotListener
            }
            for(document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let{
                    message.isReceived = true
                    if(!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }
            if(messages.isNotEmpty()){
                chatRecyclerAdapter.items = messages.sortedBy { it.timeStamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }
    }
}