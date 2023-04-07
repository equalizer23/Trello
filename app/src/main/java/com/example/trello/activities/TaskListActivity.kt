package com.example.trello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trello.R
import com.example.trello.adapters.TaskListItemsAdapter
import com.example.trello.constants.Constants
import com.example.trello.databinding.ActivityTaskListBinding
import com.example.trello.firebase.FireStoreClass
import com.example.trello.models.Board
import com.example.trello.models.Task

class TaskListActivity : BaseActivity() {
    private var binding: ActivityTaskListBinding? = null
    private lateinit var mBoardDetails: Board
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

    fun addTaskListSuccess(){
        hideProgressBar()

        showProgressDialog()
        FireStoreClass().getBoardDetails(this, mBoardDetails.documentID)
    }

    fun boardDetails(board: Board){
        mBoardDetails = board

        hideProgressBar()
        setupActionBar()

        val addTaskList = Task("Add List")
        board.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding?.rvTaskList?.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding?.rvTaskList?.adapter = adapter
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mBoardDetails.name
        }

        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog()

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog()
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog()

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


}