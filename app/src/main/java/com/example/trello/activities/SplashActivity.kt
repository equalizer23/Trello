package com.example.trello.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.trello.databinding.ActivitySplashBinding
import com.example.trello.firebase.FireStoreClass

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setFlags()
        setTypeFace()
        setupHandler()

    }

    private fun setFlags(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setTypeFace(){
        val typeFace: Typeface =
            Typeface.createFromAsset(assets, "Fonts/carbon bl.ttf")

        binding?.tvAppName?.typeface = typeFace
    }

    private fun setupHandler(){
        Handler().postDelayed({
            var currentUserId = FireStoreClass().getCurrentUserId()

            if(currentUserId.isNotEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }

            finish()
        }, 2500)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}