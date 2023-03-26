package com.example.trello.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trello.databinding.ActivitySignUpBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setActionBar()
        startActivityMain()
        setFlags()


    }

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()

        hideProgressBar()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setFlags(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setActionBar(){
        setSupportActionBar(binding?.toolbarSignUp)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarSignUp?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun startActivityMain(){
        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim{it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim{it <= ' '}
        val password: String = binding?.etPassword?.text.toString().trim{it <= ' '}

        if(validateForm(name, email, password)){
            showProgressDialog()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FireStoreClass().registerUser(this, user)
                        Toast.makeText(
                            this,
                            "You have succesfully registered the email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        hideProgressBar()
                        showErrorSnackBar(task.exception!!.message!!)
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String) : Boolean{
        return when{
            TextUtils.isEmpty(name) ->{
                showErrorSnackBar("Please enter a name")
                false
            }

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