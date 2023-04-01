package com.example.trello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.adapters.BoardItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityMainBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
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

        FireStoreClass().loadUserData(this, true)
        buttonAddBoard()

    }

    fun populateBoardListToUI(boardList: ArrayList<Board>){
        hideProgressBar()
        val rvBoards: RecyclerView = findViewById(R.id.rv_boards)
        val tvNoRecords: TextView = findViewById(R.id.tv_no_boards_available)
        if (boardList.size > 0){
            rvBoards.visibility = View.VISIBLE
            tvNoRecords.visibility = View.GONE
            rvBoards.layoutManager = LinearLayoutManager(this)
            rvBoards.setHasFixedSize(true)

            val adapter: BoardItemsAdapter = BoardItemsAdapter(this, boardList)
            rvBoards.adapter = adapter
        }
        else{
            rvBoards.visibility = View.GONE
            tvNoRecords.visibility = View.VISIBLE
        }


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

    fun updateNavigationUserData(user: User, readBoardsList: Boolean) {
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

        if (readBoardsList){
            showProgressDialog()
            FireStoreClass().getBoardsList(this)
        }

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