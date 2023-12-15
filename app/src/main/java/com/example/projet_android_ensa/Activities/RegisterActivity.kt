package com.example.projet_android_ensa.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.projet_android_ensa.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var layoutTextInputName : TextInputLayout
    lateinit var layoutTextInputEmail : TextInputLayout
    lateinit var layoutTextInputPassword : TextInputLayout
    lateinit var layoutTextInputConfirmPassword : TextInputLayout
    lateinit var btnRegister : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        layoutTextInputName = findViewById(R.id.layoutTextInputNameR)
        layoutTextInputEmail = findViewById(R.id.layoutTextInputEmailR)
        layoutTextInputPassword = findViewById(R.id.layoutTextInputPasswordR)
        layoutTextInputConfirmPassword = findViewById(R.id.layoutTextInputConfirmPasswordR)
        btnRegister = findViewById(R.id.btnRegisterR)
        btnRegister.setOnClickListener{
            initErrors()
            val name = layoutTextInputName.editText?.text.toString()
            val email = layoutTextInputEmail.editText?.text.toString()
            val password = layoutTextInputPassword.editText?.text.toString()
            val confirmPassword = layoutTextInputConfirmPassword.editText?.text.toString()
            if(email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if(password.isEmpty()){
                    layoutTextInputPassword.isErrorEnabled = true
                    Toast.makeText(this,"Informations requises qui manquent !! (Mot de passe)", Toast.LENGTH_LONG).show()
                }
                if(email.isEmpty()){
                    layoutTextInputEmail.isErrorEnabled = true
                    Toast.makeText(this,"Informations requises qui manquent !! (Email)",
                        Toast.LENGTH_LONG).show()
                }
                if(name.isEmpty()){
                    layoutTextInputName.isErrorEnabled = true
                    Toast.makeText(this,"Informations requises qui manquent !! (Nom)",
                        Toast.LENGTH_LONG).show()
                }
            } else
            {
                if(password != confirmPassword)
                {
                        Toast.makeText(this,"La conformation du mot de passe n'est pas exacte !",Toast.LENGTH_LONG).show()
                }
                else
                {
                    // creation d'un utilisateur dans le module d'authentification firebase
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                            if(task.isSuccessful)
                            {
                                val user = hashMapOf(
                                    "fullname" to name,
                                    "email" to email,
                                    )
                                val currentUser = auth.currentUser
                               // creation d'un utilisateur  dans le module Firestore
                                val db = Firebase.firestore
                                db.collection("users").document(currentUser!!.uid).set(user).addOnSuccessListener {
                                    Intent(this,HomeActivity::class.java).also {
                                        startActivity(it)
                                    }
                                }.addOnFailureListener{
                                    Toast.makeText(this,"Une erreur est survenu , essayez plus tard",Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(this,"Une erreur est survenu , essayez plus tard",Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }

        }
    }
    private fun initErrors() {
        layoutTextInputName.isErrorEnabled = false
        layoutTextInputEmail.isErrorEnabled = false
        layoutTextInputPassword.isErrorEnabled = false
        layoutTextInputConfirmPassword.isErrorEnabled = false
    }
}
