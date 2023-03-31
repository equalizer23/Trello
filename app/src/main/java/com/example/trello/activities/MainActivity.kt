package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.trello.CreateBordActivity
import com.example.trello.R
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityMainBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setupActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FireStoreClass().loadUserData(this)
        buttonAddBoard()

    }

    private fun buttonAddBoard(){
        val fabAddBoard: FloatingActionButton = findViewById(R.id.fab_add_board)
        fabAddBoard.setOnClickListener{
            val intent = Intent(this, CreateBordActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }
    }

    private fun setupActionBar(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer(){
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }

        binding?.drawerLayout?.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    fun updateNavigationUserData(user: User) {
        val navUserImage: CircleImageView = findViewById(R.id.nav_user_image)
        val tvUsername: TextView = findViewById(R.id.tv_username)
        mUserName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage);

        tvUsername.text = user.name

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FireStoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled", "Cancelled")
        }
    }

}