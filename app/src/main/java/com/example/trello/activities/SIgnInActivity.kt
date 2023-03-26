package com.example.trello.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.trello.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User

class SIgnInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setActionBar()

        binding?.btnSignIn?.setOnClickListener {
            signInUser()
        }
    }


    fun signInSuccess(user: com.example.trello.models.User){
        hideProgressBar()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setActionBar(){
        setSupportActionBar(binding?.toolbarSignIn)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarSignIn?.setNavigationOnClickListener {
            onBackPressed()
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun signInUser(){
        val email: String = binding?.etEmail?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPassword?.text.toString().trim{it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign In", "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this, MainActivity:: class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        hideProgressBar()
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        showErrorSnackBar("The password or email are incorrect")
                    }
                }
        }

    }

    private fun validateForm(email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please enter an email")
                false
            }

            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else ->{
                true
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}