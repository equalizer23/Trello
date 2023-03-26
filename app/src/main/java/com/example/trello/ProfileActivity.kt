package com.example.trello

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trello.activities.BaseActivity
import com.example.trello.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {
    private var binding: ActivityProfileBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}