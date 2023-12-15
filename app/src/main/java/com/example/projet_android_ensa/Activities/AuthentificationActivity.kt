package com.example.projet_android_ensa.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.projet_android_ensa.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthentificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var tvRegister : TextView
    lateinit var authEmail : TextInputLayout
    lateinit var authPass : TextInputLayout
    lateinit var authConnect : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)
        auth = Firebase.auth
        tvRegister = findViewById(R.id.register)
        authConnect = findViewById(R.id.authConnect)
        authPass = findViewById(R.id.authPass)
        authEmail = findViewById(R.id.authEmail)
    }
    override fun onStart() {
        super.onStart()
        tvRegister.setOnClickListener{
            Intent(this,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
            authConnect.setOnClickListener {
            authEmail.isErrorEnabled = false
            authPass.isErrorEnabled = false
            val email = authEmail.editText?.text.toString()
            val pass = authPass.editText?.text.toString()
            if(email.isEmpty() || pass.isEmpty())
            {
                if(pass.isEmpty()){
                    authPass.isErrorEnabled = true
                    Toast.makeText(this,"Informations requises qui manquent !! (Email)",Toast.LENGTH_LONG).show()
                }
                if(email.isEmpty()){
                    authEmail.isErrorEnabled = true
                    Toast.makeText(this,"Informations requises qui manquent !! (Mot de passe)",Toast.LENGTH_LONG).show()
                }
            } else {
                signIn(email,pass)
            }
        }
    }
    fun signIn(email:String,password:String)
    {
        Log.d("sigIn","signIn user ...")
        auth.signInWithEmailAndPassword(email,password). addOnCompleteListener{task ->
                if(task.isSuccessful)
                {
                    Intent(this,HomeActivity::class.java).also {
                        startActivity(it)
                    }
                    finish()
                }
                else {
                    Toast.makeText(this,"Email ou mot de passe incorrecte",Toast.LENGTH_LONG).show()
                    authPass.isErrorEnabled = true
                    authEmail.isErrorEnabled = true
                }
        }
    }
}