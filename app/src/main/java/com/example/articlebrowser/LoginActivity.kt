package com.example.articlebrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.articlebrowser.databinding.ActivityLoginBinding
import com.example.articlebrowser.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater)}
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
    }
    fun register(view: View){
        if(binding.email.text.toString() == "" || binding.password.text.toString() == "")
            Toast.makeText(this, "Can't register with wrong data", Toast.LENGTH_SHORT).show()
        else {
            auth.createUserWithEmailAndPassword(
                binding.email.text.toString(),
                binding.password.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(
                    this,
                    "successfully registred user " + binding.email.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Can't register with wrong data", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun confirm(view: View){
        if(binding.email.text.toString() == "" || binding.password.text.toString() == "")
            Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show()
        else {
            auth.signInWithEmailAndPassword(
                binding.email.text.toString(),
                binding.password.text.toString()
            ).addOnSuccessListener {
                it.user
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}