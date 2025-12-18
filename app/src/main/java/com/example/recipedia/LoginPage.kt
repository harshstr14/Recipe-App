package com.example.recipedia

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.recipedia.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginPage : AppCompatActivity() {
    lateinit var binding: ActivityLoginPageBinding
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        enableEdgeToEdgeWithInsets(binding.root,binding.main)

        val pref = getSharedPreferences("Pref_name",MODE_PRIVATE)
        val isLoggedIn = pref.getBoolean("isLoggedIn",false)

        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        } else {
            binding.signupbtn.setOnClickListener {
                val name = binding.name.text.toString().trim()
                val username = binding.username.text.toString().trim()
                val mail = binding.mail.text.toString().trim()
                val password = binding.password.text.toString().trim()

                if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && mail.isNotEmpty()) {

                    if (binding.checkbox.isChecked) {
                        auth = FirebaseAuth.getInstance()
                        database = FirebaseDatabase.getInstance().getReference("Users")

                        auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userID = auth.currentUser?.uid
                                val userData = mapOf(
                                    "name" to name,
                                    "mail" to mail,
                                    "username" to username
                                )
                                if (userID != null) {
                                    database.child(userID).setValue(userData).addOnSuccessListener {
                                        Toast.makeText(this,"SignUp Successful",Toast.LENGTH_SHORT).show()
                                        pref.edit { putBoolean("isLoggedIn", true) }
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }.addOnFailureListener { e ->
                                        Log.e("FirebaseDB", "Failed to save user data: ${e.message}", e)
                                        Toast.makeText(this, "Could not save user info. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Log.e("Auth", "UID is null after successful registration")
                                    Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.e("Auth", "Sign-up failed: ${task.exception?.message}", task.exception)
                                Toast.makeText(this, "Email is already registered or invalid.", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { e ->
                            Log.e("Auth", "FirebaseAuth error: ${e.message}", e)
                            Toast.makeText(this, "Sign-up failed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.checkbox.buttonTintList = ColorStateList.valueOf(Color.RED)
                        Toast.makeText(this,"Please Accept T&C", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this,"Please Fill the Details", Toast.LENGTH_SHORT).show()
                }
            }

            binding.txtview2.setOnClickListener {
                val intent = Intent(this, LoginPage2::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
            }
        }
    }
    private fun enableEdgeToEdgeWithInsets(rootView: View, layoutView: View) {
        val activity = rootView.context as ComponentActivity
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            layoutView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
            }

            insets
        }
    }
}