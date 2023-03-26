package com.example.trello.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.trello.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var binding: ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        loginOnClick()
        setFlags()
    }

    private fun setFlags(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun loginOnClick(){
        binding?.btnSignUp?.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignIn?.setOnClickListener {
            val intent = Intent(this, SIgnInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}