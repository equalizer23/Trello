package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trello.R
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityTaskListBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board

class TaskListActivity : BaseActivity() {
    private var binding: ActivityTaskListBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId: String = ""

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog()
        FireStoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board){
        hideProgressBar()
        setupActionBar(board.name!!)
    }

    private fun setupActionBar(title: String){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = title
        }

        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}