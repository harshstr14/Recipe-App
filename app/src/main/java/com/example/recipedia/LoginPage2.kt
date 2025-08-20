package com.example.recipedia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.recipedia.databinding.ActivityLoginPage2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginPage2 : AppCompatActivity() {
    lateinit var binding: ActivityLoginPage2Binding
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPage2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loginbtn.setOnClickListener {
            val mail = binding.mail.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (mail.isNotEmpty() && password.isNotEmpty()) {
                readData(mail,password)
            } else {
                Toast.makeText(this,"Enter Mail and Pass", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtview2.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    private fun readData(mail: String, password: String) {
        val pref = getSharedPreferences("Pref_name", MODE_PRIVATE)

        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()
                pref.edit { putBoolean("isLoggedIn",true) }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("Auth", "Log-In failed: ${task.exception?.message}", task.exception)
                Toast.makeText(this, "Email was not registered or invalid.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("Auth", "FirebaseAuth error: ${e.message}", e)
            Toast.makeText(this, "Log-In failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}

