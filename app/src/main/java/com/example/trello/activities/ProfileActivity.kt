package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.trello.R
import com.example.trello.activities.BaseActivity
import com.example.trello.databinding.ActivityProfileBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.User

class ProfileActivity : BaseActivity() {
    private var binding: ActivityProfileBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
    }



    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarProfileActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "My Profile"
        }

        binding?.toolbarProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}