package com.example.projet_android_ensa.Activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.projet_android_ensa.Models.User
import com.example.projet_android_ensa.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null

    lateinit var ivUser : ShapeableImageView
    private lateinit var layoutTextInputName : TextInputLayout
    private lateinit var layoutTextInputEmail : TextInputLayout
    private lateinit var btnSave : Button
    var isImageChanged:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        ivUser = findViewById(R.id.ivUser)
        layoutTextInputEmail = findViewById(R.id.layoutTextInputEmail)
        layoutTextInputName = findViewById(R.id.layoutTextInputName)
        btnSave = findViewById(R.id.btnSave)
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let{
                Glide.with(this).load(it).placeholder(R.drawable.frmf).into(ivUser)
                isImageChanged = true
            }
        }
        ivUser.setOnClickListener{
            pickImage.launch("image/*")
        }
        if(currentUser != null) {
            db.collection("users").document(currentUser!!.uid).get().addOnSuccessListener { result ->
                if(result != null){
                    var user = result.toObject(User::class.java)
                    user?.let {
                        user.uuid = currentUser!!.uid
                        setUserData(user)
                    }
                }
            }
        }else {
            Log.d("Settings Activity","No user Found")
        }
    }

    private fun setUserData(user: User) {
        layoutTextInputEmail.editText?.setText(user.email)
        layoutTextInputName.editText?.setText(user.fullname)

        user.image?.let{
            Glide.with(this).load(it).placeholder(R.drawable.frmf).into(ivUser)
        }

        btnSave.setOnClickListener{
            layoutTextInputName.isErrorEnabled = false
            if(isImageChanged){
                uploadImageToFireBaseStorage(user)
            }else if(layoutTextInputName.editText?.text.toString() != user.fullname){
                val fullName = layoutTextInputName.editText?.text.toString()
                user.fullname = fullName
                updateUserInfo(user)
            }else{
                Toast.makeText(this,"Your information are already updated",Toast.LENGTH_LONG).show()
                layoutTextInputName.clearFocus()
            }
        }
    }

    private fun uploadImageToFireBaseStorage(user: User) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${user.uuid}")

        val bitMap = (ivUser.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()
        // upload the bytearray to firebase storage
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {uri ->
                user.image = uri.toString()
                updateUserInfo(user)
            }
        }
    }

    private fun updateUserInfo(user: User) {
        var updatedUser = hashMapOf<String,Any>(
            "fullname" to user.fullname,
            "image" to (user.image ?: "")
        )
        db.collection("users").document(user.uuid).update(updatedUser)
            .addOnSuccessListener {
                Toast.makeText(this,"Vos informations sont mises à jour",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                layoutTextInputName.error = "Une erreur s'est produite .. ressayez le plus proche possible !!"
                layoutTextInputName.isErrorEnabled = true
            }
    }

}