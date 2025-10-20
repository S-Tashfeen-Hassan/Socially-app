package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class sign_up : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val backButton = findViewById<ImageView>(R.id.back_button)
        val createButton = findViewById<TextView>(R.id.create_button)

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val firstNameInput = findViewById<EditText>(R.id.first_name_input)
        val lastNameInput = findViewById<EditText>(R.id.last_name_input)
        val dobInput = findViewById<EditText>(R.id.dob_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)

        backButton.setOnClickListener {
            startActivity(Intent(this, pre_login::class.java))
            finish()
        }

        createButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val dob = dobInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent double taps
            createButton.isEnabled = false
            createButton.alpha = 0.5f

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = mapOf(
                            "username" to username,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "dob" to dob,
                            "email" to email
                        )

                        // Write to Realtime DB
                        database.reference.child("users").child(uid).setValue(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                goToHome()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Database error: ${e.message}", Toast.LENGTH_SHORT).show()
                                goToHome() // still navigate even if DB write failed
                            }

                        // Fallback in case listener doesn't fire (rare Firebase delay)
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!isFinishing) goToHome()
                        }, 2000)
                    } else {
                        createButton.isEnabled = true
                        createButton.alpha = 1f
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, home_screen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
