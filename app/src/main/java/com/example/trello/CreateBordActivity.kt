package com.example.trello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trello.databinding.ActivityCreateBordBinding

class CreateBordActivity : AppCompatActivity() {
    private var binding: ActivityCreateBordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBordBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCreateBordActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Create Board"
        }

        binding?.toolbarCreateBordActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}