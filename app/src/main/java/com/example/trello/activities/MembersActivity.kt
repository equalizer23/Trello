package com.example.trello.activities

import android.os.Bundle
import android.provider.MediaStore.Audio.Genres.Members
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trello.R
import com.example.trello.adapters.MembersItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityMembersBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
import com.example.trello.models.User

class MembersActivity : BaseActivity() {
    private var binding: ActivityMembersBinding? = null
    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
        showProgressDialog()
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)

    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Members"
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun setupMembersList(list:ArrayList<User>){
        hideProgressBar()

        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MembersItemsAdapter(this, list)

        binding?.rvMembersList?.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}