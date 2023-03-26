package com.example.trello.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.trello.R
import com.example.trello.databinding.ActivityBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    private var binding: ActivityBaseBinding? = null
    private var doubleBackToExitPress = false
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    fun showProgressDialog(){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.progress_dialog)

        mProgressDialog.show()
    }

    fun hideProgressBar(){
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPress){
            super.onBackPressed()
            return
        }
        else{
            this.doubleBackToExitPress = true
            showToast("Please click back again to exit")
        }

        Handler().postDelayed({doubleBackToExitPress = false}, 2000)
    }

    fun showToast(text: String){
        Toast.makeText(
            this,
            "$text",
            Toast.LENGTH_LONG).show()
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
}