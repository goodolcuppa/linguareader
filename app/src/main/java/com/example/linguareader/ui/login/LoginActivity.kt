package com.example.linguareader.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.linguareader.MainActivity
import com.example.linguareader.R
import com.example.linguareader.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    private lateinit var progressBar: ProgressBar

    override fun onStart() {
        super.onStart()
        // Skips to main activity if the user is already logged in
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val root: View = binding.getRoot()
        setContentView(root)

        val loginForm = root.findViewById<LinearLayout>(R.id.login_form)
        val signupForm = root.findViewById<LinearLayout>(R.id.signup_form)

        val btnSwitchLogin = root.findViewById<Button>(R.id.btn_switch_login)
        val btnSwitchSignup = root.findViewById<Button>(R.id.btn_switch_signup)

        // Switches to the signup form
        btnSwitchLogin.setOnClickListener {
            loginForm.visibility = View.GONE
            signupForm.visibility = View.VISIBLE
        }

        // Switches to the login form
        btnSwitchSignup.setOnClickListener {
            loginForm.visibility = View.VISIBLE
            signupForm.visibility = View.GONE
        }

        progressBar = findViewById(R.id.loading)

        // Assigns the login views
        val etLoginEmail = findViewById<EditText>(R.id.et_login_email)
        val etLoginPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnLoginLocal = findViewById<Button>(R.id.btn_login_local)

        // Assigns the sign up views
        val etSignupEmail = findViewById<EditText>(R.id.et_signup_email)
        val etSignupPassword = findViewById<EditText>(R.id.et_signup_password)
        val etSignupPasswordConfirm = findViewById<EditText>(R.id.et_signup_password_confirm)
        val btnSignup = findViewById<Button>(R.id.btn_signup)
        val btnSignupLocal = findViewById<Button>(R.id.btn_signup_local)

        btnLogin.setOnClickListener {
            login(
                etLoginEmail.getText().toString(),
                etLoginPassword.getText().toString()
            )
        }

        btnSignup.setOnClickListener {
            signup(
                etSignupEmail.getText().toString(),
                etSignupPassword.getText().toString()
            )
        }

        btnLoginLocal.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignupLocal.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Attempts to log in with Firebase Authentication
    private fun login(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            progressBar.visibility = View.INVISIBLE
            // If login was successful, informs the user and starts MainActivity
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    R.string.login_success,
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.login_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Attempts to sign up with Firebase Authentication
    private fun signup(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            progressBar.visibility = View.INVISIBLE
            // If signup was successful, informs the user and logs in
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    R.string.signup_success,
                    Toast.LENGTH_LONG
                ).show()
                login(email, password)
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.signup_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}